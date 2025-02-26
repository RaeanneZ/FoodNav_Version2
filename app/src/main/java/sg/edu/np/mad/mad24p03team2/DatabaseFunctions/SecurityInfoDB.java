package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.AbstractDBProcess;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonBloodSugarResult;

/**
 * SecurityInfoDB
 * Handles all the CRUD operations to the database
 */
public class SecurityInfoDB extends AbstractDBProcess {
    Statement stmt;

    // Connect to database
    protected SecurityInfoDB(Context appContext) {
        super(appContext);
    }

    // Read Function - Get the record by querying with email
    public ResultSet GetRecord(String email) {

        String sql = "SELECT * FROM SecurityInfo WHERE AccEmail = '" + email + "'";
        try {
            stmt = dbCon.createStatement();
            return stmt.executeQuery(sql);
        } catch (Exception e) {
            return null;
        }
    }

    // Create Function
    public boolean CreateRecord(String email, String question, String answer) throws SQLException {
        Boolean isSuccess = false;
        ResultSet resultSet = null;
        String sql = "INSERT INTO SecurityInfo(AccEmail, Question, Answer) VALUES " +
                "('" + email + "', '" + question + "', '" + answer+ "')";

        try {
            // Check if record already exist
            resultSet = GetRecord(email);
            //if record does not exist
            if (!resultSet.isBeforeFirst() && resultSet.getRow() == 0) {
                // Create and execute the SQL statement to Database
                stmt = dbCon.createStatement();
                stmt.executeUpdate(sql);
                isSuccess = true;
            }
        } catch (Exception e) {
            return isSuccess;
        } finally {
            // Close resultset
            if (resultSet != null) {
                resultSet.close();
            }
        }
        return isSuccess;
    }

    public boolean UpdateRecord(String email, String question, String answer) throws SQLException {
        boolean isUpdateSuccessful = false;
        ResultSet resultSet = null;
        String sql = "UPDATE SecurityInfo(AccEmail, Question, Answer) VALUES " +
                "('" + email + "', '" + question + "', '" + answer+ "')";

        try {
            // Check if record already exist
            resultSet = GetRecord(email);
            //if record exist
            if(resultSet.next()){
                // Create and execute the SQL statement to Database
                stmt = dbCon.createStatement();
                stmt.executeUpdate(sql);
                isUpdateSuccessful = true;
            }
        } catch (Exception e) {
            return isUpdateSuccessful;
        } finally {
            // Close resultset
            if (resultSet != null) {
                resultSet.close();
            }
        }

        return isUpdateSuccessful;
    }

    // Delete Record
    public boolean DeleteSecurityInfo(String email) throws SQLException {
        boolean isUpdateSuccessful = false;
        String sql = null;

        try {
            sql = "DELETE FROM SecurityInfo WHERE AccEmail = '"+ email +"'";
            stmt = dbCon.createStatement();
            stmt.executeUpdate(sql);
            isUpdateSuccessful = true;
        } catch (Exception e) {
            isUpdateSuccessful = false;
        }

        return isUpdateSuccessful;
    }
}
