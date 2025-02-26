package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.AbstractDBProcess;
import sg.edu.np.mad.mad24p03team2.ApplicationSetUp.StartUp;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonTodayMeal;

/**
 * MealDB
 * handles Database CRUD operations
 */
public class MealDB extends AbstractDBProcess {
    Statement stmt;
    private final Context context;
    private Connection dbCon = null;

    protected MealDB(Context appContext) {
        super(appContext);
        this.context = appContext;
        getDBConnection();
    }

    private void getDBConnection(){
        if(context instanceof StartUp){
            final StartUp app = (StartUp) context;
            dbCon = app.getConnection();
        }
    }

    //250724
    public ResultSet GetRecordByName(int accountID, String mealName, String date){
        // Get records that are equal to the mealname (eg Breakfast) and is today's record
        String sql = "SELECT * FROM Meal WHERE CONVERT (date, Timestamp) = '"+date+"' AND AccID = "+accountID+
                " AND MealName = '"+mealName+"'";

        Log.d("MealDB", "MEAL REC SQL BY DATE = "+sql);
        try{
            stmt = dbCon.createStatement();
            return stmt.executeQuery(sql);
        }
        catch (Exception e) {
            Log.d("MealDB","GetRecordByDateAndName exception = "+e.getMessage());
            return null;
        }
    }

    //250724
    public ResultSet GetRecordByFood(int foodID, int accountID, String mealName, String date) {
        // Get records that are equal to the mealname (eg Breakfast) and is today's record
        String sql = "SELECT * FROM Meal WHERE CONVERT (date, Timestamp) = '"+date+"' AND FoodID = "+foodID+" AND AccID = "+accountID +
                " AND MealName = '"+mealName+"'";
        try {
            stmt = dbCon.createStatement();
            return stmt.executeQuery(sql);
        }
        catch (Exception e) {
            return null;
        }
    }

    //250724
    public ResultSet GetRecord(String mealName, int accountID, String date){
        // Get records that are equal to the mealname (eg Breakfast) and is today's record
        String sql = "SELECT * FROM Meal WHERE CONVERT (date, Timestamp) = '"+date+"' AND MealName = '"+mealName+"' AND AccID = " + accountID;

        try {
            stmt = dbCon.createStatement();
            return stmt.executeQuery(sql); // Column 1 = email, Column 2 = password
        }
        catch (Exception e) {
            return null;
        }
    }

    //250724
    public boolean CreateRecord(int accId, FoodItemClass foodItem, String mealName, int quantity, String date) throws SQLException {

        Boolean isSuccess = false;
        ResultSet resultSet = null;
        String sql = "INSERT INTO Meal(AccID, FoodID, MealName, Quantity, Timestamp) VALUES ('"+accId+"','"+foodItem.getId()+"','"+mealName+"',"+quantity+",'"+date+"')";

        try{
            // Check if record is already inside LoginInfo
            stmt = dbCon.createStatement();
            stmt.executeUpdate(sql);
            isSuccess = true;
        }
        catch (Exception e) {
            return isSuccess;
        }
        finally{
            // Close resultset
            if(resultSet != null) {
                resultSet.close();
            }
        }

        return isSuccess;
    }

    //250724
    public boolean UpdateQuantity(int accId, FoodItemClass foodItem, String mealName, String date, int quantity) throws SQLException {
        boolean isUpdateSuccessful = false;
        ResultSet resultSet = null;
        String sql = null;
        int mealId = 0;
        try {
            resultSet = GetRecordByFood(foodItem.getId(), accId, mealName, date);
            if (resultSet.next()) {
                // If there is record
                mealId = resultSet.getInt("MealID");
                if (quantity > 0) {
                    //add on existing quanity in records
                    quantity += resultSet.getInt("Quantity");

                    // Create and execute the SQL statement to Database
                    sql = "UPDATE Meal SET Quantity = '"+quantity+"' WHERE MealId = '"+mealId+"'";
                    stmt = dbCon.createStatement();
                    stmt.executeUpdate(sql);

                    //update Local copy
                    Map<FoodItemClass, Integer> foodMap = SingletonTodayMeal.getInstance().
                            GetMeal(mealName).getSelectedFoodList();

                    if(foodMap.containsKey(foodItem))
                        //increase quantity if food record already exist
                        foodMap.put(foodItem, foodMap.get(foodItem) + quantity);


                } else {
                    // Quantity is 0, so should delete the record
                    if(DeleteMealItem(mealId, date)) {
                        //update local record
                        SingletonTodayMeal.getInstance().GetMeal(mealName).
                                getSelectedFoodList().remove(foodItem);
                    }
                }
                isUpdateSuccessful = true;
            } else {
                // Create a new record
                CreateRecord(accId, foodItem, mealName, quantity, date);

                //update Local Copy
                SingletonTodayMeal.getInstance().AddFood(mealName, foodItem, quantity);
            }
        } catch (Exception e) {
            Log.d("UpdateRecord failed", e.getMessage());
            return isUpdateSuccessful = false;
        } finally {
            // Close resultset
            if (resultSet != null) {
                resultSet.close();
            }
        }

        Log.d("Update Record", "status = " + isUpdateSuccessful);
        return isUpdateSuccessful;
    }

    //250724
    public boolean DeleteMealItem(int mealId, String date) throws SQLException {
        boolean isUpdateSuccessful = false;
        String sql = null;

        try {
            sql = "DELETE FROM Meal WHERE CONVERT (date, Timestamp) = '"+date+"' AND MealID = " + mealId;
            stmt = dbCon.createStatement();
            stmt.executeUpdate(sql);
            isUpdateSuccessful = true;
        } catch (Exception e) {
            Log.d("DELETE MEAL", "Deletion failed: " + e.getMessage());
            isUpdateSuccessful = false;
        }

        return isUpdateSuccessful;
    }
}