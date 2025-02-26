package sg.edu.np.mad.mad24p03team2.SingletonClasses;

import java.lang.reflect.Array;
import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodItemClass;

/**
 * SingletonFoodSearchResult
 * Store a snapshot of current Food Search details, info exchange between Model and UI
 */
public class SingletonFoodSearchResult {
    private FoodItemClass selFoodItem = null;
    private ArrayList<FoodItemClass> foodItemList;

    private ArrayList<FoodItemClass> allFoodItemList;

    private String mealName = "Breakfast";

    private static volatile SingletonFoodSearchResult INSTANCE = null;

    // private constructor to prevent instantiation of the class
    private SingletonFoodSearchResult() {
        selFoodItem = new FoodItemClass();
        allFoodItemList = new ArrayList<>(); //initialise
    }

    // public static method to retrieve the singleton instance
    public static SingletonFoodSearchResult getInstance() {
        // Check if the instance is already created
        if(INSTANCE == null) {
            // synchronize the block to ensure only one thread can execute at a time
            synchronized (SingletonFoodSearchResult.class) {
                // check again if the instance is already created
                if (INSTANCE == null) {
                    // create the singleton instance
                    INSTANCE = new SingletonFoodSearchResult();
                }
            }
        }
        // return the singleton instance
        return INSTANCE;
    }

    public ArrayList<FoodItemClass> getFoodSearchResult() {
        return foodItemList;
    }

    public void setSelectedFoodFromSearchResult(FoodItemClass selectedFoodItem){
        selFoodItem = selectedFoodItem;
    }

    public void setAllFoodItemList(ArrayList<FoodItemClass> completeList){
        this.allFoodItemList = completeList;
    }

    public ArrayList<FoodItemClass> getCompleteFoodItemList(){
        return this.allFoodItemList;
    }

    public FoodItemClass getSelectedFoodItemFromSearchResult(){
        return selFoodItem;
    }

    // Database return search result
    public void setFoodItemList(ArrayList<FoodItemClass> foodList) {
        foodItemList = foodList;
    }

    public void setCurrentMeal(String mealName) {
        this.mealName = mealName;
    }

    public String getCurrentMeal(){
        return this.mealName;
    }

    public void onDestroy(){
        INSTANCE = null;
    }
}
