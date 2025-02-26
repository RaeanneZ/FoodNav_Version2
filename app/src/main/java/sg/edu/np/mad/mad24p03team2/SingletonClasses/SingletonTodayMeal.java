package sg.edu.np.mad.mad24p03team2.SingletonClasses;


import android.util.Log;

import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodItemClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.MealClass;

/**
 * SingletonTodayMeal
 * Store Toady's 3 meals locally, exchange between Model and UI
 */
//Declaration of customed Datatype - Enumeration
enum MealNames {
    BREAKFAST("Breakfast"),
    LUNCH("Lunch"),
    DINNER("Dinner");

    private String name;

    MealNames(String name) {
        this.name = name;
    }

    public String getString() {
        return name;
    }
}

public class SingletonTodayMeal {
    MealClass[] mealClassArray;

    private static volatile SingletonTodayMeal INSTANCE = null;

    // private constructor to prevent instantiation of the class
    private SingletonTodayMeal() {
        mealClassArray = new MealClass[3];
    }

    // public static method to retrieve the singleton instance
    public static SingletonTodayMeal getInstance() {
        // Check if the instance is already created
        if (INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (SingletonTodayMeal.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new SingletonTodayMeal();
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
    }

    public void AddMeal(MealClass meal) {
        for (MealNames n : MealNames.values()) {
            if (meal.getMealName().compareTo(n.getString()) == 0)
                mealClassArray[n.ordinal()] = meal;
        }
    }

    public void AddFood(String mealName, FoodItemClass fItem, int qty){
        for(MealNames n : MealNames.values()){
            if (mealName.compareTo(n.getString()) == 0){
                MealClass mClass = mealClassArray[n.ordinal()];
                mClass.getSelectedFoodList().put(fItem, qty);
            }
        }
    }

    public MealClass GetMeal(String mealName) {
        if(mealClassArray.length >0) {
            for (MealNames n : MealNames.values()) {
                if (mealName.compareTo(n.getString()) == 0) {
                    MealClass tmp = mealClassArray[n.ordinal()];
                    if(tmp != null) //ensure array has valid mealClass
                        return tmp;
                }
            }
        }

        //if not found, add and return new obj class
        MealClass newMClass = new MealClass(mealName);
        this.AddMeal(newMClass);
        return newMClass;
    }

    public void onDestroy(){
        INSTANCE = null;
    }
}


