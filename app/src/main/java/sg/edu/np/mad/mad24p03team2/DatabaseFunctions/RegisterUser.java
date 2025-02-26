package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.sql.ResultSet;
import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.GlobalUtil;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietConstraints;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSignUp;

public class RegisterUser extends AsyncTaskExecutorService<String, String, String> {

    private final String TAG = "RegisterUser";
    String z = "Sign Up Successful";
    Boolean isSuccess = false;
    ArrayList<IDBProcessListener> dbListeners = null;

    // LoginInfo DB Class
    LoginInfoDB loginInfoDB = null;
    SecurityInfoDB securityInfoDB = null;
    // Account DB
    AccountDB accountDB = null;
    //DietPlanDB
    DietPlanOptDB dietPlanOptDB = null;

    //GetDietPlanOpt Function
    GetDietPlanOption getDietPlanOption = null;

    UpdateDietConstraints updateDietConstraints = null;
    private ArrayList<IDBProcessListener> listeners = new ArrayList<IDBProcessListener>();


    public RegisterUser(Context appContext) {
        this.loginInfoDB = new LoginInfoDB(appContext);
        this.accountDB = new AccountDB(appContext);
        this.securityInfoDB = new SecurityInfoDB(appContext);
        this.dietPlanOptDB = new DietPlanOptDB(appContext);
        this.getDietPlanOption = new GetDietPlanOption(appContext);
        this.updateDietConstraints = new UpdateDietConstraints(appContext);

        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public RegisterUser(Context appContext, IDBProcessListener listener) {
        this(appContext);
        if (listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener) {
        listeners.add(listener);
    }


    @Override
    protected String doInBackground(String... inputs) {
        try {
            //Login Info Table
            LoginInfoClass loginInfo = SingletonSignUp.getInstance().getLoginInfo();
            isSuccess = loginInfoDB.CreateRecord(loginInfo.getEmail(), loginInfo.getPassword());
            if (!isSuccess) {
                return z = "Login Database Creation Fail";
            }
            //security Table
            SecurityInfoClass securityInfo = SingletonSignUp.getInstance().getSecurityInfo();
            isSuccess = securityInfoDB.CreateRecord(securityInfo.getEmail(),
                    securityInfo.getQuestion(), securityInfo.getAnswer());

            if (!isSuccess) {
                loginInfoDB.DeleteLoginRecord(loginInfo.getEmail());
                return z = "Security Setup Fail";
            }

            //profile table
            AccountClass acctInfo = SingletonSignUp.getInstance().getAccount();
            isSuccess = accountDB.CreateRecord(acctInfo.getName(), acctInfo.getEmail());
            if (!isSuccess) {
                securityInfoDB.DeleteSecurityInfo(loginInfo.getEmail());
                loginInfoDB.DeleteLoginRecord(loginInfo.getEmail());
                return z = "Profile Setup Fail";
            }

            Log.d("RegisterUser", "TrackBloodSugar = "+acctInfo.getTrackBloodSugar());
            isSuccess = accountDB.UpdateRecord(acctInfo.getEmail(),
                    acctInfo.getGender(),
                    GlobalUtil.DateFormatter.format(acctInfo.getBirthDate()),
                    Float.toString(acctInfo.getHeight()), Float.toString(acctInfo.getWeight()),
                    acctInfo.getTrackBloodSugar());

            if (!isSuccess) {
                securityInfoDB.DeleteSecurityInfo(loginInfo.getEmail());
                accountDB.DeleteAccount(loginInfo.getEmail());
                loginInfoDB.DeleteLoginRecord(loginInfo.getEmail());
                return z = "Profile Update Fail";
            }

            //update current SingletonSession userInfo - signup successful
            ResultSet resultSet = accountDB.GetRecordID(acctInfo.getEmail());
            if(resultSet.next())
                acctInfo.setId(resultSet.getInt("AccID"));
            SingletonSession.getInstance().setAccount(acctInfo);

            //by default - set diet constraint to be sugar free (diabetic friendly diet plan)
            updateDietConstraints.execute(Integer.toString(acctInfo.getId()));

        } catch (Exception e) {
            z = e.getMessage();
        }
        return z;
    }

    @Override
    protected void onPostExecute(String s) {
        for (IDBProcessListener listener : listeners) {
            listener.afterProcess(isSuccess, s, RegisterUser.class);
        }
    }

    // IGNORE --------------------------------------------------------------------------------------

    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) {
        return null;
    }

    // IGNORE --------------------------------------------------------------------------------------
}
