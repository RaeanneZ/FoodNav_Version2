package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;

import java.sql.ResultSet;
import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSecurityInfoResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

/**
 * GetSecurityInfo
 * Model-Controller that handles UI-request to get security info to reset password
 */
public class GetSecurityInfo extends AsyncTaskExecutorService<String, String , String> {

    String z = "";

    Boolean isSuccess = false;
    ArrayList<IDBProcessListener> dbListeners = null;


    // Login Data Class
    SecurityInfoDB securityInfoDB = null;

    public GetSecurityInfo(Context appContext){
        // Later will pass in ApplicationContext
        this.securityInfoDB = new SecurityInfoDB(appContext);
        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public GetSecurityInfo(Context appContext, IDBProcessListener listener){
        this(appContext);
        if(listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener){
        dbListeners.add(listener);
    }

    // This is to check password corresponds
    @Override
    protected String doInBackground(String... inputs) {
        String email = inputs[0];
        ResultSet resultSet = securityInfoDB.GetRecord(email);
        try{

            if (resultSet.next()) {
                isSuccess = true;
                SingletonSecurityInfoResult.getInstance().setSecurityInfo(
                        email, resultSet.getString("Question"),
                        resultSet.getString("Answer"));

            }
        }
        catch (Exception e){
            z = e.getMessage();
        }
        return z;
    }


    @Override
    protected void onPostExecute(String s) {
        //Notify all listeners
        for(IDBProcessListener listener : dbListeners){
            listener.afterProcess(isSuccess, GetSecurityInfo.class);
        }
    }

    // IGNORE --------------------------------------------------------------------------------------

    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) { return null; }
    // IGNORE --------------------------------------------------------------------------------------
}
