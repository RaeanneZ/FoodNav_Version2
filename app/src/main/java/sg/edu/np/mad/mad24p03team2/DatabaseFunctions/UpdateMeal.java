package sg.edu.np.mad.mad24p03team2.DatabaseFunctions;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.GlobalUtil;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonFoodSearchResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

public class UpdateMeal extends AsyncTaskExecutorService<String, String, String> {

    Boolean isSuccess = false;
    ArrayList<IDBProcessListener> dbListeners = null;
    MealDB mealDB = null;

    public UpdateMeal(Context appContext) {
        this.mealDB = new MealDB(appContext);
        this.dbListeners = new ArrayList<IDBProcessListener>();
    }

    public UpdateMeal(Context appContext, IDBProcessListener listener) {
        this(appContext);
        if (listener != null)
            registerListener(listener);
    }

    public void registerListener(IDBProcessListener listener) {
        dbListeners.add(listener);
    }

    @Override
    protected void onPostExecute(String s) {
        for (IDBProcessListener listener : dbListeners) {
            listener.afterProcess(isSuccess,s,UpdateMeal.class);
        }
    }

    @Override
    protected String doInBackground(String... strings) {
        //int accId = Integer.parseInt(strings[0]);
        String mealName = strings[0];
        int quantity = Integer.parseInt(strings[1]);

        try {
            // Get the selected food item
            FoodItemClass foodItem = SingletonFoodSearchResult.getInstance().getSelectedFoodItemFromSearchResult();
            // If there is record, update the record
            // If there is no record, create the record
            // If quantity is reduced to 0, delete the record
            isSuccess = mealDB.UpdateQuantity(SingletonSession.getInstance().GetAccount().getId(),
                    foodItem, mealName,
                    GlobalUtil.DateFormatter.format(SingletonSession.getInstance().getMealDate().getTime()),
                    quantity);

        } catch (Exception e) {
            Log.d("Create Meal", "Error occurred: " + e.getMessage());
        }

        return mealName;
    }

    @Override
    protected ArrayList<FoodItemClass> doInBackground(String name) {
        return null;
    }
}
