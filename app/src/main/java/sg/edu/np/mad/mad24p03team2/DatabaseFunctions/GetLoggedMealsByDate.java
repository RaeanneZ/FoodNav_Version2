package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonBloodSugarResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonTodayMeal;

/**
 * GetLoggedMealsByDate
 * Model-Controller that handles UI-request to retrieve all LoggedMeals of specified date
 */
public class GetLoggedMealsByDate extends AsyncTaskExecutorService<String, String , String> {

    private final String TAG = "GetLoggedMealsByDate";
    String z = "";
    Boolean isSuccess = false;
    MealDB mealDB = null;
    FoodDB foodDB = null;
    FoodItemClass foodItem = null;
    MealClass mealClass = null;
    BloodSugarTrackingDB bloodSugarTrackingDB = null;

    private ArrayList<IDBProcessListener> dbListeners;

    @Override
    protected String doInBackground(String... strings) {
        String date = strings[0];
        ResultSet resultSet = null;
        try{
            int accID = SingletonSession.getInstance().GetAccount().getId();

            //Load meal Logs
            getMeal("Breakfast", date, accID);
            getMeal("Lunch", date, accID);
            getMeal("Dinner", date, accID);

            //load glucose logs
            if(SingletonSession.getInstance().GetAccount().isTrackBloodSugar()){
                getGlucoseReading(date, accID);
            }

            isSuccess = true;
        }
        catch (Exception e) {
            isSuccess = false;
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

    private void getGlucoseReading(String date, int accID){
        ResultSet resultSet = bloodSugarTrackingDB.GetRecordByDate(accID, date);
        try {
            // Return empty String if there is no results
            if(resultSet == null || (!resultSet.isBeforeFirst() && resultSet.getRow() == 0)) {
                Log.d(TAG,"No record of Blood Sugar recorded for "+accID);

            }else{
                while (resultSet.next()) {
                    // Make sure there is a record
                    BloodSugarClass bloodSugarObj = new BloodSugarClass(resultSet.getInt("BloodSugarID"),
                            resultSet.getFloat("BloodSugarReading"),
                            resultSet.getString("MealName"), resultSet.getString("Timestamp"));

                    // Add the result into the singleton for global access
                    SingletonBloodSugarResult.getInstance().addBloodSugarByMeal(bloodSugarObj);
                }
            }
        }
        catch (SQLException e) {
            z = "Fail to fetch user glucose reading from DB";
            Log.d(TAG, "Exception msg = "+e.getMessage());
        }
        finally {
            try{
                if(resultSet != null){
                    resultSet.close();
                }
            } catch (Exception e) {
                Log.d("Get Today Blood Sugar by Meal", "ResultSet unable to close");
            }
        }
    }

    private void getMeal(String mealName, String date, int accID){
        //get record OF the DAY for the accID and mealName
        ResultSet mealResultSet = mealDB.GetRecordByName(
                SingletonSession.getInstance().GetAccount().getId(), mealName, date);
        ResultSet foodResultSet = null;

        try {
            mealClass = new MealClass(mealName);
            if(mealResultSet != null) {
                //if theres is food item
                while (mealResultSet.next()) {
                    try {
                        mealClass.setId(mealResultSet.getInt("MealID"));
                        // Get the foodData from Meal data
                        foodResultSet = foodDB.GetRecordById(mealResultSet.getInt("FoodID"));
                        if (foodResultSet.next()) {
                            // Change it into an object
                            // int id, String name, double calories, double serving_size_g, double fat_total_g, double protein_g, double carbohydrates_total_g)
                            foodItem = new FoodItemClass(foodResultSet.getInt("FoodID"),
                                    foodResultSet.getString("Name"),
                                    foodResultSet.getDouble("Calories"),
                                    foodResultSet.getDouble("ServingSize"),
                                    foodResultSet.getDouble("Fats"),
                                    foodResultSet.getDouble("Sugar"),
                                    foodResultSet.getDouble("Carbohydrates"),
                                    foodResultSet.getString("Recommend"));

                            mealClass.getSelectedFoodList().put(foodItem, mealResultSet.getInt("Quantity"));
                        }else{
                            Log.d(TAG, "GetMeal - There is no foodItem found ****");
                        }

                    } catch (Exception e) {
                        z = "Fail to get food items for "+mealName+"from DB";
                        Log.d(TAG, e.getMessage());
                    } finally {
                        if (foodResultSet != null) {
                            foodResultSet.close();
                        }
                    }
                }
            }else{
                Log.d(TAG, "There is no meal records found for = "+accID +" on this date = "+date);
            }
            // Save db return for global access
            SingletonTodayMeal.getInstance().AddMeal(mealClass);
            Log.d(TAG, "updated SingletonTodayMeal = "+accID +" on this date = "+date);
        }
        catch (SQLException e) {
            Log.d(TAG, "Exception msg = "+e.getMessage());
        }
        finally {
            try{
                if(mealResultSet != null) {
                    mealResultSet.close();
                }
            } catch (Exception e) { Log.d("Get Meal", "ResultSet unable to close"); }
        }
    }

    public GetLoggedMealsByDate(Context appContext){
        this.mealDB = new MealDB(appContext);
        this.foodDB = new FoodDB(appContext);
        this.bloodSugarTrackingDB = new BloodSugarTrackingDB(appContext);

        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public GetLoggedMealsByDate(Context appContext, IDBProcessListener listener){
        this(appContext);
        if(listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener){
        dbListeners.add(listener);
    }

    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) { return null; }

    @Override
    protected void onPostExecute(String s) {
        for(IDBProcessListener listener : dbListeners){
            listener.afterProcess(isSuccess, s, GetLoggedMealsByDate.class);
        }
    }
}
