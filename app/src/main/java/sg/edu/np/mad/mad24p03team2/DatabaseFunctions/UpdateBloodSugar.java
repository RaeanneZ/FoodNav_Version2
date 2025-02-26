package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.GlobalUtil;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

/**
 * UpdateBloodSugar
 * DB-Controller to handle UI-request to update blood sugar level to database
 */
public class UpdateBloodSugar extends AsyncTaskExecutorService<String, String , String> {

    Boolean isSuccess = false;
    ArrayList<IDBProcessListener> dbListeners = null;
    BloodSugarTrackingDB bloodSugarTrackingDB = null;

    public UpdateBloodSugar(Context appContext){
        this.bloodSugarTrackingDB = new BloodSugarTrackingDB(appContext);
        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public UpdateBloodSugar(Context appContext, IDBProcessListener listener){
        this(appContext);
        if(listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener){
        dbListeners.add(listener);
    }

    @Override
    protected String doInBackground(String... strings) {
        String mealName = strings[0];
        float bloodSugar = Float.parseFloat(strings[1]);
        //String timestamp = strings[2];

        try {
            isSuccess = bloodSugarTrackingDB.UpdateRecord(SingletonSession.getInstance().GetAccount().getId(),
                    bloodSugar, mealName, GlobalUtil.DateFormatter.format(SingletonSession.getInstance().getMealDate().getTime()));//, timestamp);
        } catch (Exception e) {
            Log.d("UpdateBloodSugar", "Error occurred: " + e.getMessage());
            isSuccess = false;
        }
        return mealName;
    }

    @Override
    protected void onPostExecute(String s) {
        for(IDBProcessListener listener : dbListeners){
            listener.afterProcess(isSuccess, s, UpdateBloodSugar.class);
        }
    }

    // IGNORE --------------------------------------------------------------------------------------

    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) {
        return null;
    }
    // IGNORE --------------------------------------------------------------------------------------
}
