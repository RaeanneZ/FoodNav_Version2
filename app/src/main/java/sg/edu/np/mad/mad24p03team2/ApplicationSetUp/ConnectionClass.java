package sg.edu.np.mad.mad24p03team2.ApplicationSetUp;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * ConnectionClass
 * Connection to MS Azure MSSQL
 */
public class ConnectionClass {
    String classes = "net.sourceforge.jtds.jdbc.Driver";

    // This is to create the connection between the server and the application
    public Connection connectDatabase(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;

        try{
            Class.forName(classes);
            connectionURL = "jdbc:jtds:sqlserver://mad-foodnav-database.database.windows.net:1433/FoodNav;user=foodNavAdmin@mad-foodnav-database;password=MADf00dnav;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;ssl=request;";
            connection = DriverManager.getConnection(connectionURL);
        }
        catch (Exception e){
            Log.e("SQL Connection Error : ", e.getMessage());
        }

        return connection;
    }

    // Connection con should be a variable that calls on the conn() class
    public void ExecuteDatabase(Connection con){

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            try {
                if(con == null){
                    String str = "Check Your Internet Connection";
                }
                else{
                    String str = "Connected with SQL server";
                }
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
