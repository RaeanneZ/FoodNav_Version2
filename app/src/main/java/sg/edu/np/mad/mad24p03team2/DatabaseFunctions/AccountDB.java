package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.AbstractDBProcess;

/**
 * AccountDB
 * Handles CRUD operations of Account table
 * Account database table holds information of the following:
 * Name, email, gender, birthdate, height, weight, dietOption, optionToTrackBloodSugar
 */
public class AccountDB extends AbstractDBProcess {

    Statement stmt;

    // CONNECT TO DATABASE
    protected AccountDB(Context appContext) {
        super(appContext);
    }

    // Read Function
    public ResultSet GetRecord(String email) {
        String sql = "SELECT * FROM Account WHERE AccEmail = '" + email + "'";

        try {
            stmt = dbCon.createStatement();
            return stmt.executeQuery(sql);
        } catch (Exception e) {
            return null;
        }
    }

    public ResultSet GetRecordID(String email) {
        String sql = "SELECT AccID FROM Account WHERE AccEmail = '" + email + "'";

        try {
            stmt = dbCon.createStatement();
            return stmt.executeQuery(sql);
        } catch (Exception e) {
            return null;
        }
    }

    // Create Function
    public boolean CreateRecord(String name, String email) throws SQLException {
        Boolean isSuccess = false;
        ResultSet resultSet = null;
        String sql = "INSERT INTO Account(Name, AccEmail) VALUES ('" + name + "', '" + email + "')";

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

    public boolean UpdateRecord(String email, String gender, String birthDate, String height, String weight, String trackBloodSugar) throws SQLException {
        boolean isUpdateSuccessful = false;
        ResultSet resultSet = null;
        String sql = null;

        try {
            resultSet = GetRecord(email);
            if (resultSet.next()) {
                // Create and execute the SQL statement to Database
                if (gender != null) {
                    sql = "UPDATE ACCOUNT SET Gender = '" + gender + "' WHERE AccEmail = '" + email + "'";
                    stmt = dbCon.createStatement();
                    stmt.executeUpdate(sql);
                }
                if (birthDate != null) {
                    sql = "UPDATE ACCOUNT SET BirthDate = '" + birthDate + "' WHERE AccEmail = '" + email + "'";
                    stmt = dbCon.createStatement();
                    stmt.executeUpdate(sql);
                }
                if (!Objects.equals(height, "")) {
                    sql = "UPDATE ACCOUNT SET Height = " + height + " WHERE AccEmail = '" + email + "'";
                    stmt = dbCon.createStatement();
                    stmt.executeUpdate(sql);
                }
                if (!Objects.equals(weight, "")) {
                    sql = "UPDATE ACCOUNT SET Weight = " + weight + " WHERE AccEmail = '" + email + "'";
                    stmt = dbCon.createStatement();
                    stmt.executeUpdate(sql);
                }
                if (!Objects.equals(trackBloodSugar, "")) {
                    sql = "UPDATE ACCOUNT SET TrackBloodSugar = '" + trackBloodSugar + "' WHERE AccEmail = '" + email + "'";
                    stmt = dbCon.createStatement();
                    stmt.executeUpdate(sql);
                }

                isUpdateSuccessful = true;
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

        Log.d("AccountDB ::Update Record", "status = " + isUpdateSuccessful);
        return isUpdateSuccessful;
    }

    // Delete Account
    public boolean DeleteAccount(String email) throws SQLException {
        boolean isUpdateSuccessful = false;
        String sql = null;

        try {
            sql = "DELETE FROM Account WHERE AccEmail = '"+ email +"'";
            stmt = dbCon.createStatement();
            stmt.executeUpdate(sql);
            isUpdateSuccessful = true;
        } catch (Exception e) {
            Log.d("DELETE ACCOUNT", "Deletion failed: " + e.getMessage());
            isUpdateSuccessful = false;
        }

        return isUpdateSuccessful;
    }
}
