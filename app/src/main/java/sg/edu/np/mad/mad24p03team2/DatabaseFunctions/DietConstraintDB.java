package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.AbstractDBProcess;
import sg.edu.np.mad.mad24p03team2.ApplicationSetUp.StartUp;

/**
 * DietConstraintDB
 * Controller handles CRUD operations on Diet Constraint Database
 */
public class DietConstraintDB extends AbstractDBProcess {
    Statement stmt;
    private final Context context;
    private Connection dbCon = null;

    public DietConstraintDB(Context base) {
        super(base);
        this.context = base;
        getDBConnection();
    }

    public ResultSet GetRecord(int accountID) {
        String sql = "SELECT * FROM DietConstraint WHERE AccID = " + accountID;
        Log.d("DIETCONSTRAINTDB", "ACCOUNT ID: " + accountID);
        try {
            stmt = dbCon.createStatement();
            return stmt.executeQuery(sql);
        } catch (Exception e) {
            return null;
        }
    }

    private void getDBConnection() {
        if (context instanceof StartUp) {
            final StartUp app = (StartUp) context;
            dbCon = app.getConnection();
        }
    }

    public boolean UpdateRecord(int accountID, ArrayList<String> constraints) throws SQLException {
        boolean isSuccess = false;

        boolean delIsSuccess = DeleteRecord(accountID);
        if(delIsSuccess) {
            ResultSet resultSet = null;

            try {
                Log.d("DietConstraintDB", "Insert New Diet Pref to DB");
                for (String constraint : constraints) {
                    String sql = "INSERT INTO DietConstraint(AccID, DietType) VALUES (" + accountID + ",'" + constraint + "')";
                    stmt = dbCon.createStatement();
                    stmt.executeUpdate(sql);
                }
            } catch (Exception e) {
                return isSuccess;
            } finally {
                // Close resultset
                if (resultSet != null) {
                    resultSet.close();
                }
            }

            return true;
        }
        return false;
    }

    public boolean DeleteRecord(int acctID) throws SQLException {
        boolean isUpdateSuccessful = false;
        String sql = null;
        Log.d("DietConstraintDB", "*** Delete Diet Pref to DB");

        try {
            sql = "DELETE FROM DietConstraint WHERE AccID = " + acctID;
            stmt = dbCon.createStatement();
            stmt.executeUpdate(sql);
            isUpdateSuccessful = true;
        } catch (Exception e) {
            isUpdateSuccessful = false;
        }

        return isUpdateSuccessful;
    }

}
