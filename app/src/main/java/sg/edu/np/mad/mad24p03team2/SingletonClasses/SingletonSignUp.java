package sg.edu.np.mad.mad24p03team2.SingletonClasses;

import android.content.Context;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.security.crypto.EncryptedSharedPreferences;

import java.util.Date;

import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.AccountClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.DietPlanClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.LoginInfoClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.SecurityInfoClass;
import sg.edu.np.mad.mad24p03team2.GlobalUtil;

/**
 * SingletonSignUp
 * To hold all info entered in the process of sign up
 * Only Commit to database after user complete the sign up process
 * This is to avoid data inconsistency due to incomplete sign up
 */
public class SingletonSignUp {
    //sign up data includes
    //logininfo, account, securityinfo, dietplan info

    private LoginInfoClass loginInfo = null;
    private AccountClass account = null;
    private SecurityInfoClass securityInfo = null;
    private DietPlanClass dietPlan = null;

    private Boolean rememberMe = false;

    private Context applicationContext = null;

    private static volatile SingletonSignUp INSTANCE = null;

    // private constructor to prevent instantiation of the class
    private SingletonSignUp() {
        //default to blank
        loginInfo = new LoginInfoClass();
        account = new AccountClass();
        securityInfo = new SecurityInfoClass();
        dietPlan = new DietPlanClass();
    }

    // public static method to retrieve the singleton instance
    public static SingletonSignUp getInstance() {
        // Check if the instance is already created
        if (INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (SingletonSignUp.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new SingletonSignUp();
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
    }

    public void initSignUp(String name, String email, String password, Context appContext){
        loginInfo.setEmail(email);
        loginInfo.setPassword(password);

        account.setName(name);
        account.setEmail(email);

        securityInfo.setEmail(email);

        applicationContext = appContext;
    }

    public void ended(){

        handleRememberMe();

        loginInfo = null;
        account = null;
        dietPlan = null;
        securityInfo = null;

        //check on Remember Status
    }

    private void handleRememberMe(){
        //handle REMEMBER ME check
        if(rememberMe){
            EncryptedSharedPreferences sharedPreferences = GlobalUtil.getEncryptedSharedPreference(applicationContext);
            if(sharedPreferences != null){
                //save email and password login
                sharedPreferences.edit().putString(GlobalUtil.SHARED_PREFS_LOGIN_KEY, loginInfo.getEmail()).apply();
                sharedPreferences.edit().putString(GlobalUtil.SHARED_PREFS_LOGIN_PSWD, loginInfo.getPassword()).apply();
            }
        }
    }
    public void setRememberMe(boolean save){
        rememberMe = save;
    }

    public String getName(){
        return account.getName();
    }

    public LoginInfoClass getLoginInfo() {
        return loginInfo;
    }

    public AccountClass getAccount() {
        return account;
    }

    public void setProfile(Date birthDate, String gender,float height, float weight){
        account.setBirthDate(birthDate);
        account.setGender(gender);
        account.setHeight(height);
        account.setWeight(weight);
        // Log.d("SingletonSignUp", "UpdateProfile ="+ GlobalUtil.DateFormatter.format(birthDate)+" / "
        //         +gender+"/"+height+"/"+weight);
    }

    public void setBloodTrackingOpt(boolean option){
        account.setTrackBloodSugar(option);
    }

    public SecurityInfoClass getSecurityInfo() {
        return securityInfo;
    }

    public void setSecurityInfo(String qns, String ans) {
        securityInfo.setQuestion(qns);
        securityInfo.setAnswer(ans);
    }

    public DietPlanClass getDietPlan() {
        return dietPlan;
    }

    public void setDietPlan(DietPlanClass dietPlan) {
        this.dietPlan = dietPlan;
    }

    public void onDestroy(){
        INSTANCE = null;
    }

}
