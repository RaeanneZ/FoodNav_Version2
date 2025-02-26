package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;

public class UpdateUserPassword extends AsyncTaskExecutorService<String, String , String>  {

    String z = "";
    Boolean isSuccess = false;
    ArrayList<IDBProcessListener> dbListeners = null;
    // Login Data Class
    LoginInfoDB loginInfoDB = null;

    public UpdateUserPassword(Context appContext){
        // Later will pass in ApplicationContext
        this.loginInfoDB = new LoginInfoDB(appContext);
        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public UpdateUserPassword(Context appContext, IDBProcessListener listener){
        this(appContext);
        if(listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener){
        dbListeners.add(listener);
    }

    @Override
    protected String doInBackground(String... inputs) {
        try{
            String email = inputs[0];
            String password = inputs[1];
            isSuccess = loginInfoDB.UpdateRecord(email, password);
        }
        catch (Exception e){
            isSuccess = false;
            z = e.getMessage();
        }

        return z;
    }

    @Override
    protected void onPostExecute(String s) {
        // Notify all listeners
        for(IDBProcessListener listener : dbListeners){
            listener.afterProcess(isSuccess, UpdateUserPassword.class);
        }
    }

    // IGNORE --------------------------------------------------------------------------------------

    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) { return null; }
    // IGNORE --------------------------------------------------------------------------------------
}
