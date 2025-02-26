package sg.edu.np.mad.mad24p03team2.Abstract_Interfaces;

import android.content.Context;
import android.content.ContextWrapper;

import java.sql.Connection;

import sg.edu.np.mad.mad24p03team2.ApplicationSetUp.StartUp;

/**
 * AbstractDBProcess
 * Code Reuse :
 * Handle Database connection for all the classes that
 * interact with the backend with SQL statements
 */
public class AbstractDBProcess extends ContextWrapper {

    private final Context context;

    protected Connection dbCon = null;

    protected AbstractDBProcess(Context appContext){
        super(appContext);

        this.context = appContext;
        getDBConnection();
    }

    private void getDBConnection(){
        if(context instanceof StartUp){
            final StartUp app = (StartUp) context;
            dbCon=app.getConnection();
        }
    }

    public Context getApplicationContext(){
        return context;
    }


}
