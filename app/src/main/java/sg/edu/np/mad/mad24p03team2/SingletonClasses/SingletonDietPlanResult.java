package sg.edu.np.mad.mad24p03team2.SingletonClasses;

import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.DietPlanClass;

/**
 * SingletonDietPlanResult
 * Store DietPlan details locally, info exchange between Model and UI
 */
public class SingletonDietPlanResult {
    private DietPlanClass dietPlan = null;
    private static volatile SingletonDietPlanResult INSTANCE = null;

    // private constructor to prevent instantiation of the class
    private SingletonDietPlanResult() {
        dietPlan = new DietPlanClass();
    }

    // public static method to retrieve the singleton instance
    public static SingletonDietPlanResult getInstance() {
        // Check if the instance is already created
        if(INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (SingletonDietPlanResult.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new SingletonDietPlanResult();
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
    }

    public DietPlanClass getDietPlan() {
        return dietPlan;
    }
    public void setDietPlan (DietPlanClass dietPlan) {
        this.dietPlan = dietPlan;
    }

    public void onDestroy(){
        INSTANCE = null;
    }
}
