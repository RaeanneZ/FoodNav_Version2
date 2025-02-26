package sg.edu.np.mad.mad24p03team2.SingletonClasses;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.BloodSugarClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.SecurityInfoClass;

/**
 * SingletonSecurityInfoResult
 * Store reset password account security info
 */
public class SingletonSecurityInfoResult {
    private SecurityInfoClass securityInfoObj = null;
    private static volatile SingletonSecurityInfoResult INSTANCE = null;

    // private constructor to prevent instantiation of the class
    private SingletonSecurityInfoResult() {
    }

    // public static method to retrieve the singleton instance
    public static SingletonSecurityInfoResult getInstance() {
        // Check if the instance is already created
        if(INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (SingletonSecurityInfoResult.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new SingletonSecurityInfoResult();
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
    }

    public void setSecurityInfo(String email, String question, String answer){
        securityInfoObj = new SecurityInfoClass(email, question, answer);
    }

    public SecurityInfoClass getSecurityInfo(){
        return securityInfoObj;
    }

    public void onDestroy(){
        INSTANCE = null;
    }
}
