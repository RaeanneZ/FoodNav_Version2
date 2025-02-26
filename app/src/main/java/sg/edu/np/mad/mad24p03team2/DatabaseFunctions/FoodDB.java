package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.AbstractDBProcess;
import sg.edu.np.mad.mad24p03team2.ApplicationSetUp.StartUp;

/**
 * FoodDB
 * Controller handles CRUD operations on Food Database
 */
public class FoodDB extends AbstractDBProcess {
    Statement stmt;
    private final Context context;
    private Connection dbCon = null;

    public FoodDB(Context base) {
        super(base);
        this.context = base;
        getDBConnection();
    }

    public ResultSet GetAllRecord(){
        String sql = "SELECT * FROM Food";

        try {
            stmt = dbCon.createStatement();
            return stmt.executeQuery(sql); // Column 1 = email, Column 2 = password
        }
        catch (Exception e) {
            return null;
        }
    }

    public ResultSet GetRecord(String name){
        String sql = "SELECT * FROM Food WHERE Name LIKE '%"+name+"%'";

        try {
            stmt = dbCon.createStatement();
            return stmt.executeQuery(sql); // Column 1 = email, Column 2 = password
        }
        catch (Exception e) {
            return null;
        }
    }

    public ResultSet GetRecordById(int id){
        String sql = "SELECT * FROM Food WHERE FoodID = " + id;

        try {
            stmt = dbCon.createStatement();
            return stmt.executeQuery(sql); // Column 1 = email, Column 2 = password
        }
        catch (Exception e) {
            return null;
        }
    }

    public ResultSet GetSpecificRecord(String name){
        String sql = "SELECT * FROM Food WHERE Name = '"+name+"'";

        try {
            stmt = dbCon.createStatement();
            return stmt.executeQuery(sql); // Column 1 = email, Column 2 = password
        }
        catch (Exception e) {
            return null;
        }
    }

    private void getDBConnection(){
        if(context instanceof StartUp){
            final StartUp app = (StartUp) context;
            dbCon=app.getConnection();
        }
    }

    public boolean CreateRecord(FoodItemClass foodItem) throws SQLException {
        Boolean isSuccess = false;
        ResultSet resultSet = null;
        String sql = "INSERT INTO Food(Name, Calories, Carbohydrates, Protein, Fats, ServingSize, Recommend, Sugar) VALUES " +
                "('"+foodItem.getName()+"','"+foodItem.getCalories()+"','"+foodItem.getCarbohydrates_total_g()+"','"+foodItem.getProtein_g()
                +"','"+foodItem.getFat_total_g()+"','"+foodItem.getServing_size_g()+"','"+foodItem.getRecommend()+"', '"+foodItem.getSugar_g()+"')";

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
}

