package sg.edu.np.mad.mad24p03team2;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Set;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IMealRecyclerViewInterface;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.BloodSugarClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodItemClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.MealClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.UpdateBloodSugar;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.UpdateMeal;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonBloodSugarResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonFoodSearchResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonTodayMeal;

/**
 * LogMealAndBSugarPane
 * UI-Fragment allowing current user to food log and track blood sugar level
 * Bloodsugar input changes depending on user options.
 */
public class LogMealAndBSugarPane extends Fragment implements IDBProcessListener, IMealRecyclerViewInterface {

    private final String LOG_TAG = "LogMealAndBSugarPane";
    private final static SimpleDateFormat TIMESTAMP_FORMATTER = new SimpleDateFormat("dd-MM-yyyy HH:mm");
    TextView sugarBox;
    TextView carbsBox;
    TextView fatsBox;
    TextView calBox;
    Button addFoodBtn;
    FloatingActionButton fabMealPlanner;

    TextView bsugarTimestamp;
    TextView bsugarLvl;
    MealFoodAdapter mealFoodAdapter;
    RecyclerView recyclerView;

    //Model access
    UpdateMeal updateMeal = null;// Initialize UpdateMeal object
    UpdateBloodSugar updateBloodSugar;

    //To differentiate which meal is this pane setup for
    String mealName = "Breakfast";

    boolean bsugarReadingChanged = false;
    boolean bsugarInputHasFocus = false;

    LinearLayout layoutBSugar;

    //to handle nested fragment situation
    Context parentContext;

    boolean toTrackBloodSugar = false;

    public LogMealAndBSugarPane(String meal, Context pContext) {
        this.mealName = meal;
        this.parentContext = pContext;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        // Initialize UpdateMeal object with application context and current fragment
        updateMeal = new UpdateMeal(requireContext().getApplicationContext(), this);

        //if user opt to track user level
        toTrackBloodSugar = SingletonSession.getInstance().GetAccount().isTrackBloodSugar();
        if(toTrackBloodSugar) {

            //ensure the softkeyboard doesn't change components layout when shown
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
            updateBloodSugar = new UpdateBloodSugar(requireActivity().getApplicationContext(), this);
        }
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_msugar, container, false);
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Log.d("LogMealnSugar", "OnViewStateRestored");
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d("LogMealnSugar", "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("LogMealnSugar", "onStop");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("LogMealnSugar", "onStart");
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("LogMealnSugar", "onPause");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sugarBox = view.findViewById(R.id.tvp1);
        carbsBox = view.findViewById(R.id.tvc1);
        fatsBox = view.findViewById(R.id.tvf1);
        calBox = view.findViewById(R.id.tvk1);
        addFoodBtn = view.findViewById(R.id.button2);
        fabMealPlanner = view.findViewById(R.id.fabMealPlanner);

        //if user opt to track blood sugar
        if(toTrackBloodSugar) {
            bsugarTimestamp = view.findViewById(R.id.textView11);
            bsugarLvl = view.findViewById(R.id.bsugarLvl);
            bsugarLvl.addTextChangedListener(getTextWatcher());
            bsugarLvl.setOnFocusChangeListener((v, hasFocus) -> {

                if (!bsugarInputHasFocus && hasFocus) {
                    //user set focus on component
                    bsugarInputHasFocus = hasFocus;
                } else if (!hasFocus && bsugarReadingChanged) {
                    //component lose focus and with reading changed = true
                    //meaning user finished editing
                    //update value to Model
                    updateBloodSugar.execute(this.mealName,
                            bsugarLvl.getText().toString());

                    //reset boolean after saving to Model
                    bsugarReadingChanged = bsugarInputHasFocus = false;
                }
            });
        } else {
            layoutBSugar = view.findViewById(R.id.lay33);
            layoutBSugar.setVisibility(View.GONE);
        }

        mealFoodAdapter = new MealFoodAdapter(view.getContext(), this, this.mealName);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(parentContext));
        recyclerView.setAdapter(mealFoodAdapter);

        addFoodBtn.setOnClickListener(v -> switchFragment());

        fabMealPlanner.setOnClickListener(v -> openMealPlanner());

        //read Model and update UI
        updateUI();
        updateSugarReading();
    }

    private TextWatcher getTextWatcher(){
        TextWatcher txtWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bsugarReadingChanged = true;
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        };

        return txtWatcher;
    }

    private void switchFragment() {
        // Get the current activity
        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity2) {
            // Replace the current fragment with the SearchForFood fragment
            ((MainActivity2) activity).
                    replaceFragment(new SearchForFood(), "searchForFood", false);
        }
    }

    private void openMealPlanner() {
        // Get the current activity
        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity2) {
            // Replace the current fragment with the MealPlannerFragment
            ((MainActivity2) activity).replaceFragment(new MealPlanner(), "mealPlanner", true);
        }
    }

    private void updateUI() {
        MealClass mClass = SingletonTodayMeal.getInstance().GetMeal(this.mealName);
        MealMacros mMacros = GlobalUtil.getMealTotalMacros(mClass);
        sugarBox.setText(String.format("%.1f", mMacros.gettSugar()));
        carbsBox.setText(String.format("%.1f", mMacros.gettCarbs()));
        fatsBox.setText(String.format("%.1f", mMacros.gettFats()));
        calBox.setText(String.format("%.1f", mMacros.gettCalories()));

        //update Recycler view
        Log.d("LogMealAndBSugarPane", "Meal Class = "+mClass.getMealName()+"/ FoodList size = "+mClass.getSelectedFoodList().size());
        mealFoodAdapter.setFilteredList(mClass.getMealName(), mClass.getSelectedFoodList());
    }

    public void updateSugarReading() {

        //if user did not opt for glucose tracking, then nothing to update
        if(!toTrackBloodSugar)
            return;

        //Update sugar level stored in Model
        BloodSugarClass bSugar = SingletonBloodSugarResult.getInstance().getBloodSugarByMeal(this.mealName);
        if(bSugar != null){
            this.bsugarLvl.setText(String.valueOf(bSugar.getBloodSugarReading()));
        } else {
            Log.d(LOG_TAG + "::UpdateSugarReading", "SingletonBloodSugar return null");
        }

    }

    @Override
    public void onItemClick(int foodId) {
        //invoked when delete button of food item was clicked
        //update database to delete food from record and update UI
        Map<FoodItemClass, Integer> foodList = SingletonTodayMeal.getInstance().GetMeal(this.mealName).getSelectedFoodList();
        Set<FoodItemClass> foodSet = foodList.keySet();
        for(FoodItemClass food : foodSet){
            if(food.getId() == foodId) {
                // Add the meal to SingletonTodayMeal
                SingletonFoodSearchResult.getInstance().setSelectedFoodFromSearchResult(food);
                //UPDATE MODEL that a food has been deleted from Meal
                updateMeal.execute(SingletonFoodSearchResult.getInstance().getCurrentMeal(), "0" );
            }
        }
    }

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {
        //Any update to the Model, reset the display list for UI update.
        if(executeStatus) {
            if(msg.compareToIgnoreCase(this.mealName)== 0) {
                //return result from Meal-Model
                if(returnClass.isInstance(updateMeal)) {
                    updateUI();

                }else if(returnClass.isInstance(updateBloodSugar)){
                    //return result from BloodSugar-Model
                    updateSugarReading();
                }
            }
        }
    }

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {}

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {}

}