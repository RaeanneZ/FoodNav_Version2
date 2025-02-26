package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;

import java.sql.ResultSet;
import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

/**
 * SaveSecurityInfo
 * Model-Controller that handles UI-request to retrieve all Save Security info at Sign Up
 */
public class SaveSecurityInfo extends AsyncTaskExecutorService<String, String , String> {

    String z = "";
    Boolean isSuccess = false;
    ArrayList<IDBProcessListener> dbListeners = null;


    // Login Data Class
    SecurityInfoDB securityInfoDB = null;

    public SaveSecurityInfo(Context appContext){
        // Later will pass in ApplicationContext
        this.securityInfoDB = new SecurityInfoDB(appContext);
        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public SaveSecurityInfo(Context appContext, IDBProcessListener listener){
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
        boolean isSuccessful = false;
        try{
            String email = inputs[0];
            String question = inputs[1];
            String answer = inputs[2];
            isSuccess = securityInfoDB.CreateRecord(email, question, answer);
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
            listener.afterProcess(isSuccess, SaveSecurityInfo.class);
        }
    }

    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) { return null;}
}
