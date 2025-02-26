package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.GlobalUtil;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietConstraints;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietPlanResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

/**
 * GetCurrentUserProfile
 * Model-Controller that handles UI-request to retrieve all currentUserProfile and Logged Meals at Login
 */
public class GetCurrentUserProfile extends AsyncTaskExecutorService<String, String , String> {

    private final String TAG = "GetCurrentUserProfile";
    String z = "";
    Boolean isSuccess = false;
    ArrayList<IDBProcessListener> dbListeners;

    // Account DB
    AccountDB accountDB;
    DietPlanOptDB dietPlanOptDB;
    DietConstraintDB dietConstraintDB;

    GetLoggedMealsByDate getLoggedMealsByDate;

    private ArrayList<IDBProcessListener> listeners = new ArrayList<>();

    @Override
    protected String doInBackground(String... strings) {
        String email = strings[0];
        ResultSet resultSet = null;

        try{
            resultSet = accountDB.GetRecord(email);
            if (resultSet.next()) {

                // Store info locally
                int accID = resultSet.getInt("AccID");
                SingletonSession.getInstance().GetAccount().setId(accID);
                SingletonSession.getInstance().SetUpAccount(resultSet.getString("Name"),
                        resultSet.getString("AccEmail"));

                // Update the user profile
                String gender = resultSet.getString("Gender");

                SingletonSession.getInstance().UpdateProfile(gender,
                        resultSet.getDate("BirthDate"), resultSet.getInt("Height"),
                        resultSet.getFloat("Weight"));
                SingletonSession.getInstance().SetBloodSugarTracking(resultSet.getString("TrackBloodSugar"));

                //loadDietPlanOption
                getDietPlanOption("Diabetic Friendly", gender);

                //load diet constraints
                getDietConstraint(accID);

                Calendar now = Calendar.getInstance();
                SingletonSession.getInstance().setMealDate(now.get(Calendar.YEAR),now.get(Calendar.MONTH),now.get(Calendar.DAY_OF_MONTH));

                this.getLoggedMealsByDate.execute(GlobalUtil.DateFormatter.format(now.getTime()));
            }

            isSuccess = true;
        }
        catch (Exception e) {
            z = "Fail to load user application details";
            Log.d(TAG,"Exception = "+e.getMessage());
        } finally {
            try{
                if(resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e) { Log.d(TAG, "ResultSet unable to close"); }
        }

        return z;
    }

    private void getDietConstraint(int accID){
        ResultSet dietTypeResultSet = dietConstraintDB.GetRecord(accID);
        ArrayList<String> dietContraintList = new ArrayList<>();

        try {
            //no constraint
            if (dietTypeResultSet != null) {
                // If there are any diet constraints
                while (dietTypeResultSet.next()) {
                    try {
                        // Append it to a list to contain all constraints
                        dietContraintList.add(dietTypeResultSet.getString("DietType"));
                    } catch (Exception e) {
                        Log.d("GetCurrentUserProfile::DietConstraint ", e.getMessage());
                    }
                }
            }else{
                Log.d("GetCurrentUserProfile::DietConstraint ", "No dietary profile found for user");
            }

            // Save db return for global access
            SingletonDietConstraints.getInstance().setDietProfile(dietContraintList);
        }
        catch (SQLException e) {
            z = "Fail to get Diet Constraints from DB";
            Log.d(TAG, "Exception msg = "+e.getMessage());
        }
        finally {
            try{
                if(dietTypeResultSet != null) {
                    dietTypeResultSet.close();
                }
            } catch (Exception e) { Log.d("Get Diet Constraint", "ResultSet unable to close"); }
        }
    }

    private void getDietPlanOption(String name, String gender) throws SQLException {
        ResultSet resultSet = dietPlanOptDB.GetRecord(name, gender);

        try {
            // If there is a result
            if (resultSet.next()) {
                int id = resultSet.getInt("DietPlanID");
                name = resultSet.getString("Name");
                int reccCarbIntake = resultSet.getInt("ReccCarbIntake");
                int reccSugarIntake = resultSet.getInt("ReccSugarIntake");
                int reccFatsIntake = resultSet.getInt("ReccFatsIntake");
                gender = resultSet.getString("Gender");
                int calories = resultSet.getInt("Calories");

                DietPlanClass dietPlan = new DietPlanClass(id, name, reccCarbIntake, reccSugarIntake, reccFatsIntake, gender, calories);
                SingletonDietPlanResult.getInstance().setDietPlan(dietPlan);
            }
        } catch (Exception e) {
            z = "Fail to fetch user diet plan's option";
            Log.d(TAG, "Exception msg = "+e.getMessage());
        }finally {
            if (resultSet != null) {
                resultSet.close();
            }
        }
    }

    public GetCurrentUserProfile(Context appContext){
        this.accountDB = new AccountDB(appContext);
        this.dietPlanOptDB = new DietPlanOptDB(appContext);
        this.dietConstraintDB = new DietConstraintDB(appContext);
        this.getLoggedMealsByDate = new GetLoggedMealsByDate(appContext);

        this.dbListeners = new ArrayList<>();
    }

    public GetCurrentUserProfile(Context appContext, IDBProcessListener listener){
        this(appContext);
        if(listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener){
        listeners.add(listener);
    }

    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        for(IDBProcessListener listener : dbListeners){
            listener.afterProcess(isSuccess, s, GetCurrentUserProfile.class);
        }
    }
}
