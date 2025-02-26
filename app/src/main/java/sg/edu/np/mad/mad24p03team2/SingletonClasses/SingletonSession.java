package sg.edu.np.mad.mad24p03team2.SingletonClasses;

import android.accounts.Account;

import java.util.Calendar;
import java.util.Date;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.AccountClass;


/**
 * SingletonSession
 * To store current login user info, exchange of info between Model and UI
 */
public class SingletonSession {

    private AccountClass account = null;
    Calendar mealDate;
    private static volatile SingletonSession INSTANCE = null;

    // private constructor to prevent instantiation of the class
    private SingletonSession() {
        //default to blank
        account = new AccountClass();
        account.setName("");
        account.setEmail("");
        mealDate = Calendar.getInstance();
    }

    // public static method to retrieve the singleton instance
    public static SingletonSession getInstance() {
        // Check if the instance is already created
        if (INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (SingletonSession.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new SingletonSession();
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
    }

    public void SetUpAccount(String name, String email) {
        account.setName(name);
        account.setEmail(email);
    }

    public void setAccount(AccountClass acctInfo){
        this.account = acctInfo;
    }

    public void UpdateProfile(String gender, Date birthDate, float height, float weight) {
        account.setBirthDate(birthDate);
        account.setGender(gender);

        account.setHeight(height);
        account.setWeight(weight);
        account.setDietPlanOpt("Diabetic Friendly");
    }

    public void UpdateProfile(String gender, Date birthDate, float height, float weight, String toTrackBloodSugar) {
        UpdateProfile(gender, birthDate, height, weight);
        SetBloodSugarTracking(toTrackBloodSugar);
    }

    public void setMealDate(int year, int mth, int dayOfMth){
        mealDate.set(year, mth, dayOfMth);
    }

    public Calendar getMealDate(){
        return mealDate;
    }

        public void SetBloodSugarTracking(Boolean toTrack) {
        account.setTrackBloodSugar(toTrack);
    }

    public void SetBloodSugarTracking(String toTrack) {
        account.setTrackBloodSugar(toTrack);
    }

    public AccountClass GetAccount() {
        return account;
    }

    public void onDestroy(){
        INSTANCE = null;
    }
}
