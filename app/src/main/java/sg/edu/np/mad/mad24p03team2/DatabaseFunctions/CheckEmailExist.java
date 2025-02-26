package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;

/**
 * CheckEmailExist
 * This to check if email already in DB during new member sign up
 */
public class CheckEmailExist extends AsyncTaskExecutorService<String, String , String>{

    String z = "";
    Boolean[] checks;
    ArrayList<IDBProcessListener> dbListeners = null;
    Boolean isValid;
    Boolean isExist;
    Integer isValidIdx = 0;
    Integer isExistIdx = 1;


    // Login Data Class
    LoginInfoDB loginInfoDB = null;

    public CheckEmailExist(Context appContext){
        // Later will pass in ApplicationContext
        this.loginInfoDB = new LoginInfoDB(appContext);
        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public CheckEmailExist(Context appContext, IDBProcessListener listener){
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
        try{
            String email = inputs[0];
            checks = loginInfoDB.CheckEmailRecExist(email);
            isValid = checks[isValidIdx];
            isExist = checks[isExistIdx];
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
            listener.afterProcess(isValid, isExist);
        }
    }

    // IGNORE --------------------------------------------------------------------------------------

    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) { return null; }
    // IGNORE --------------------------------------------------------------------------------------
}
