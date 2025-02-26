package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;

/**
 * LoginUser
 * Controller to handle User Login
 */
public class LoginUser extends AsyncTaskExecutorService<String, String , String>{

    String z = "";
    Boolean[] checks;
    ArrayList<IDBProcessListener> dbListeners = null;
    Boolean isValid;
    Boolean isExist;
    Integer isValidIdx = 0;
    Integer isExistIdx = 1;


    // Login Data Class
    LoginInfoDB loginInfoDB = null;

    public LoginUser(Context appContext){
        // Later will pass in ApplicationContext
        this.loginInfoDB = new LoginInfoDB(appContext);
        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public LoginUser(Context appContext, IDBProcessListener listener){
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
            String password = inputs[1];
            checks = loginInfoDB.ValidateRecord(email, password);
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
