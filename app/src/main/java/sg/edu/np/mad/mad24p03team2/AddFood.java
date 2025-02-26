package sg.edu.np.mad.mad24p03team2;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodItemClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.UpdateMeal;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonFoodSearchResult;

/**
 * AddFood
 * UI-Fragment to display selected food details,
 * users can edit quantity before adding food to specified meal of the day
 * and saved to Model
 */
public class AddFood extends Fragment implements IDBProcessListener{

    UpdateMeal updateMeal = null;// Initialize UpdateMeal object
    private int ns=1;// Initialize the counter for the number of plates\

    TextView foodName;
    TextView nofServing;
    TextView servingSize;
    TextView addn;
    TextView Mn;

    TextView carbs;
    TextView sugar;
    TextView fats;
    TextView calories;

    Button addFoodButton;
    Button cancelButton;

    //currently selectedFood
    FoodItemClass foodItemSelected;
    String foodPortion = "";
    private final NumberFormat numberFormatter = new DecimalFormat("0.0");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        updateMeal = new UpdateMeal(requireContext().getApplicationContext(), this);
        // Initialize UpdateMeal object with application context and current fragment
        foodItemSelected = SingletonFoodSearchResult.getInstance().getSelectedFoodItemFromSearchResult();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_food, container, false);
        // Find views by their IDs
        addFoodButton = view.findViewById(R.id.button2);
        cancelButton = view.findViewById(R.id.cancelBtn);
        foodName =view.findViewById(R.id.textView23);
        nofServing=view.findViewById(R.id.textView16);
        servingSize = view.findViewById(R.id.textView26);
        addn= view.findViewById(R.id.tvadd);
        Mn= view.findViewById(R.id.textView2);
        carbs = view.findViewById(R.id.tvc1);
        sugar = view.findViewById(R.id.tvp1);
        fats = view.findViewById(R.id.tvf1);
        calories = view.findViewById(R.id.tvk1);

        cancelButton.setOnClickListener(v -> { returnToPreviousPage(); });



        // Set click listener for the add button to increment the plate count
        addn.setOnClickListener(v -> {
            ns++;  // Increment the counter

            // Update the TextView with the new value
            foodPortion = String.valueOf(numberFormatter.format(foodItemSelected.getServing_size_g()*ns));
            servingSize.setText(foodPortion);
            nofServing.setText(String.valueOf(ns));
            updateMacro(ns);
        });

        // Set click listener for the minus button to decrement the plate count
        Mn.setOnClickListener(v -> {
            //does not allow 0
            if(ns > 1) {
                ns--;  // decrement the counter
                // Update the TextView with the new value
                foodPortion = String.valueOf(numberFormatter.format(foodItemSelected.getServing_size_g()*ns));
                servingSize.setText(foodPortion);
                nofServing.setText(String.valueOf(ns));
                updateMacro(ns);
            }
        });

        //update UI on foodselected
        foodName.setText(foodItemSelected.getName().toUpperCase());
        nofServing.setText(String.valueOf(ns));
        servingSize.setText(String.valueOf(foodItemSelected.getServing_size_g()));
        updateMacro(ns);

        // Set click listener for the button to execute update and switch fragments
        addFoodButton.setOnClickListener(v -> {
            // Execute the updateMeal task with the mealName, quantity
            updateMeal.execute(SingletonFoodSearchResult.getInstance().getCurrentMeal(), String.valueOf(ns));

            returnToPreviousPage();
        });
        return view;// Return the inflated view
    }

    private void returnToPreviousPage(){
        //remove 'this' from frag-stack
        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity2) {
            ((MainActivity2) activity).removeFragment(this);
        }
    }

    private void updateMacro(int serving){

        carbs.setText(String.format("%.1f", foodItemSelected.getCarbohydrates_total_g()*serving));
        sugar.setText(String.format("%.1f",foodItemSelected.getSugar_g()*serving));
        fats.setText(String.format("%.1f",foodItemSelected.getFat_total_g()*serving));
        calories.setText(String.format("%.1f",foodItemSelected.getCalories()*serving));

    }

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {}

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {}

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}
}