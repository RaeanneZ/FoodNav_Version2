package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietConstraints;

public class UpdateDietConstraints extends AsyncTaskExecutorService<String, String, String> {

    Boolean isSuccess = false;
    ArrayList<IDBProcessListener> dbListeners = null;
    DietConstraintDB dietConstraintDB = null;

    public UpdateDietConstraints(Context appContext) {
        this.dietConstraintDB= new DietConstraintDB(appContext);
        this.dbListeners = new ArrayList<>();
    }

    public UpdateDietConstraints(Context appContext, IDBProcessListener listener) {
        this(appContext);
        if (listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener) {
        dbListeners.add(listener);
    }

    @Override
    protected void onPostExecute(String s) {
        for (IDBProcessListener listener : dbListeners) {
            listener.afterProcess(isSuccess,s, UpdateDietConstraints.class);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        int accID = Integer.parseInt(strings[0]);

        try {
            // Get the selected diet constraints from singleton
            ArrayList<String> dietConstraints = SingletonDietConstraints.getInstance().getDietProfileInDBFormat();
            isSuccess = dietConstraintDB.UpdateRecord(accID, dietConstraints);

        } catch (Exception e) {
            Log.d("Create Meal", "Error occurred: " + e.getMessage());
        }

        return "success";
    }

    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) {
        return null;
    }
}
