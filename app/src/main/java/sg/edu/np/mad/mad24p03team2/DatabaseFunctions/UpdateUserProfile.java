package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.GlobalUtil;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

/**
 * UpdateUserProfile
 * DB-Controller to handle profile update of current user
 * Used in New Account Sign up and Profile Edit of existing users
 */
public class UpdateUserProfile extends AsyncTaskExecutorService<String, String, String> {

    String z = "";
    Boolean isSuccess = false;
    ArrayList<IDBProcessListener> dbListeners = null;
    DietPlanOptDB dietPlanOptDB = null;
    AccountDB accountDB = null;

    public UpdateUserProfile(Context appContext) {
        // Later will pass in ApplicationContext
        this.accountDB = new AccountDB(appContext);
        this.dietPlanOptDB = new DietPlanOptDB(appContext);
        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public UpdateUserProfile(Context appContext, IDBProcessListener listener) {
        this(appContext);
        if (listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener) {
        dbListeners.add(listener);
    }

    @Override
    protected String doInBackground(String... inputs) {
        try {
            String email = inputs[0];
            String dietPlanOpt = inputs[1];
            String gender = inputs[2];
            String birthDate = inputs[3];
            String height = inputs[4];
            String weight = inputs[5];
            String trackBloodSugar = inputs[6]; //add in

            try {
                isSuccess = accountDB.UpdateRecord(email, gender, birthDate, height, weight, trackBloodSugar);

                if (isSuccess)
                    SingletonSession.getInstance().UpdateProfile(gender, GlobalUtil.DateFormatter.parse(birthDate),
                            Float.parseFloat(height), Float.parseFloat(weight), trackBloodSugar);

            } catch (Exception e) {
                Log.d("UpdateUserProfile", "Something went wrong: " + e.getMessage());
            }
        } catch (Exception e) {
            isSuccess = false;
            z = e.getMessage();
        }

        return z;
    }

    @Override
    protected void onPostExecute(String s) {
        // Notify all listeners
        for (IDBProcessListener listener : dbListeners) {
            listener.afterProcess(isSuccess, UpdateUserProfile.class);
        }
    }

    // IGNORE --------------------------------------------------------------------------------------
    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) {
        return null;
    }
    // IGNORE --------------------------------------------------------------------------------------
}
