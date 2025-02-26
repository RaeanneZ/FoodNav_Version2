package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.ApiHandler;
import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonFoodSearchResult;

/**
 * GetAllFood
 * Model-Controller that handles UI-request to retrieve all records in Food Database
 * and translate db records into FoodItemClass object for UI display
 */
public class GetAllFood extends AsyncTaskExecutorService<String, String , String> {

    boolean isSuccess = false;
    FoodDB foodDB = null;
    ArrayList<IDBProcessListener> dbListeners = null;
    FoodItemClass foodItem;
    ArrayList<FoodItemClass> foodItems = null;

    public GetAllFood(Context appContext){
        // Later will pass in ApplicationContext
        this.foodDB = new FoodDB(appContext);
        this.dbListeners = new ArrayList<IDBProcessListener>();
        this.foodItems = new ArrayList<FoodItemClass>();
    }

    public GetAllFood(Context appContext, IDBProcessListener listener){
        this(appContext);
        if(listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener){
        dbListeners.add(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        ResultSet resultSet = foodDB.GetAllRecord();
        if(resultSet!=null) {
            try {
                while (resultSet.next()) {
                    foodItem = new FoodItemClass(resultSet.getInt("FoodID"),
                            resultSet.getString("Name"),
                            resultSet.getFloat("Calories"),
                            resultSet.getFloat("ServingSize"),
                            resultSet.getFloat("Fats"),
                            resultSet.getFloat("Sugar"),
                            resultSet.getFloat("Carbohydrates"),
                            resultSet.getString("Recommend"));
                    foodItems.add(foodItem);
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                try {
                    if (resultSet != null) {
                        resultSet.close();
                    }
                } catch (Exception e) {
                    Log.d("Get Food", "Resultset unable to close");
                }
            }
            SingletonFoodSearchResult.getInstance().setAllFoodItemList(foodItems);
            isSuccess = true;
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        for(IDBProcessListener listener : dbListeners){
            listener.afterProcess(isSuccess, GetAllFood.class);
        }
    }

    // Ignored -------------------------------------------------------------------------------------
    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) {
        return null;
    }
    // Ignored -------------------------------------------------------------------------------------
}
