package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

public class CreateFoodRecord extends AsyncTaskExecutorService<String, String , String> {

    String z = "";
    Boolean isSuccess = false;
    ArrayList<IDBProcessListener> dbListeners = null;

    // Food DB Class
    FoodDB foodDB = null;

    // FoodItem Object
    FoodItemClass foodItemClass = new FoodItemClass();

    private ArrayList<IDBProcessListener> listeners = new ArrayList<IDBProcessListener>();


    public CreateFoodRecord(Context appContext){
        this.foodDB = new FoodDB(appContext);
        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public CreateFoodRecord(Context appContext, IDBProcessListener listener){
        this(appContext);
        if(listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener){
        listeners.add(listener);
    }

    @Override
    protected String doInBackground(String... strings) {

        try{
            String sugar = strings[0];
            String protein = strings[1];
            String carbs = strings[2];
            String fats = strings[3];
            String calories = strings[4];
            String name = strings[5];
            String serving = strings[6];

            // Check if food is recommended
            if(Double.parseDouble(fats) <= (Double.parseDouble(serving) * 0.1)
                    && Double.parseDouble(sugar) <= (Double.parseDouble(serving) * 0.08)
                    && Double.parseDouble(carbs) <= (Double.parseDouble(serving) * 0.15) ) {
                foodItemClass.setRecommend("T");
            } else {
                foodItemClass.setRecommend("F");
            }

            // Populating the food item object
            foodItemClass.setName(name);
            foodItemClass.setCalories(Double.parseDouble(calories));
            foodItemClass.setFat_total_g(Double.parseDouble(fats));
            foodItemClass.setCarbohydrates_total_g(Double.parseDouble(carbs));
            foodItemClass.setProtein_g(Double.parseDouble(protein));
            foodItemClass.setSugar_g(Double.parseDouble(sugar));
            foodItemClass.setServing_size_g(Double.parseDouble(serving));

            // Execute SQL Query
            isSuccess = (foodDB.CreateRecord(foodItemClass));
        }
        catch (Exception e) {
            z = e.getMessage();
        }
        return z;
    }

    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) {
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        for(IDBProcessListener listener : listeners){
            listener.afterProcess(isSuccess, CreateFoodRecord.class);
        }
    }
}
