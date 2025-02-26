package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.sql.ResultSet;
import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietPlanResult;

/**
 * GetDietPlanOption
 * DB-Controller to handle UI request to retrieve DietPlan recommended Daily Macros and
 * Calories intake based on current user gender and diet plan user opted.
 */
public class GetDietPlanOption extends AsyncTaskExecutorService<String, String, String> {

    int id = 0;
    String name = " ";
    boolean isSuccess = false;
    int reccCarbIntake = 0;
    int reccSugarIntake = 0;
    int reccFatsIntake = 0;
    String gender = " ";
    int calories = 0;
    DietPlanClass dietPlan;

    ArrayList<IDBProcessListener> dbListeners = null;
    // Login Data Class
    DietPlanOptDB dietPlanOptDB = null;
    private ArrayList<IDBProcessListener> listeners = new ArrayList<IDBProcessListener>();


    public GetDietPlanOption(Context appContext) {
        // Later will pass in ApplicationContext
        this.dietPlanOptDB = new DietPlanOptDB(appContext);
        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public GetDietPlanOption(Context appContext, IDBProcessListener listener) {
        this(appContext);
        if (listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener) {
        dbListeners.add(listener);
    }

    @Override
    protected String doInBackground(String... strings) {

        name = strings[0];
        gender = strings[1];
        ResultSet resultSet = dietPlanOptDB.GetRecord(name, gender);

        try {
            // If there is a result
            if (resultSet.next()) {
                id = resultSet.getInt("DietPlanID");
                name = resultSet.getString("Name");
                reccCarbIntake = resultSet.getInt("ReccCarbIntake");
                reccSugarIntake = resultSet.getInt("ReccSugarIntake");
                reccFatsIntake = resultSet.getInt("ReccFatsIntake");
                gender = resultSet.getString("Gender");
                calories = resultSet.getInt("Calories");

                dietPlan = new DietPlanClass(id, name, reccCarbIntake, reccSugarIntake, reccFatsIntake, gender, calories);
                isSuccess = true;

                //return dietPlan;
                SingletonDietPlanResult.getInstance().setDietPlan(dietPlan);
                Log.d("GetDietPlanOption::doInBckgnd", "Got Diet Record! Successful!");
            }
        } catch (Exception e) {
            Log.d("GetDietPlanOption::doInBckgnd", "There is no diet plan found");
        }

        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        for (IDBProcessListener listener : dbListeners) {
            listener.afterProcess(isSuccess, GetDietPlanOption.class);
        }
    }

    // IGNORED -------------------------------------------------------------------------------------
    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) {
        return null;
    }
    // IGNORED -------------------------------------------------------------------------------------
}
