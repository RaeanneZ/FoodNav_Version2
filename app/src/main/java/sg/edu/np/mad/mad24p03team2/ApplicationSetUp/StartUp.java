package sg.edu.np.mad.mad24p03team2.ApplicationSetUp;

import android.app.Application;
import android.util.Log;

import java.sql.Connection;

/**
 * StartUp
 * Handles running of DB processes upon Application startUp
 * and closing when Application Terminates
 */
public class StartUp extends Application {
    private Connection con = null;

    @Override
    public void onTerminate() {
        super.onTerminate();
        try {
            con.close();
        } catch(Exception e) {
            Log.d("STARTUP", "Unable to close connection: " + e.getMessage());
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Connection
        ConnectionClass connectionClass = new ConnectionClass();
        con = connectionClass.connectDatabase();
        connectionClass.ExecuteDatabase(con);

        if(con != null) {
            Log.d("STARTUP","Connection is established");
        } else {
            Log.d("STARTUP","Connection is not established");
        }

        this.getApplicationContext();
    }

    public Connection getConnection(){
        return con;
    }
}
