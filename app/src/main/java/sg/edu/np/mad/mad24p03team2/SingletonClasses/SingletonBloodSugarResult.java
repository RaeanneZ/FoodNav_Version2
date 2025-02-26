package sg.edu.np.mad.mad24p03team2.SingletonClasses;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.BloodSugarClass;

/**
 * SingletonBloodSugarResult
 * Store current user Blood sugar readings, exchange between Model and UI
 */
public class SingletonBloodSugarResult {
    private ArrayList<BloodSugarClass> bloodSugarArrayList;
    private static volatile SingletonBloodSugarResult INSTANCE = null;

    // private constructor to prevent instantiation of the class
    private SingletonBloodSugarResult() {
        bloodSugarArrayList = new ArrayList<>();
    }

    // public static method to retrieve the singleton instance
    public static SingletonBloodSugarResult getInstance() {
        // Check if the instance is already created
        if(INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (SingletonBloodSugarResult.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new SingletonBloodSugarResult();
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
    }

    public BloodSugarClass getBloodSugarByMeal(String mealName){
        if(bloodSugarArrayList != null) {
            for (BloodSugarClass bloodSugar : bloodSugarArrayList) {
                if (bloodSugar.getMealName().compareToIgnoreCase(mealName) == 0) {
                    return bloodSugar;
                }
            }
        }

        return new BloodSugarClass();
    }
    public void addBloodSugarByMeal(BloodSugarClass bloodSugarObject){
        for(BloodSugarClass bloodSugar : bloodSugarArrayList){
            //if record already exist, update
            if(bloodSugar.getMealName().compareToIgnoreCase(bloodSugarObject.getMealName())==0)
                bloodSugarArrayList.set(bloodSugarArrayList.indexOf(bloodSugar), bloodSugarObject);
        }

        //new records
        bloodSugarArrayList.add(bloodSugarObject);
    }

    public void onDestroy(){
        INSTANCE = null;
    }
}
