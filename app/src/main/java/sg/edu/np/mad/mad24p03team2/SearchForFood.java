package sg.edu.np.mad.mad24p03team2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodItemClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.GetAllFood;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.GetFood;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonFoodSearchResult;

interface RecyclerViewInterface {

    void onItemClick(int itemPos);
}

/**
 * SearchForFood
 * UI-Fragement where user can search database for food by entering food name in full or partial
 * To add food, user can click on the search returned food list
 */
public class SearchForFood extends Fragment implements IDBProcessListener, RecyclerViewInterface {
    GetFood getFood = null;
    GetAllFood getAllFood = null;
    private RecyclerView recyclerView;
    private ArrayList<FoodItemClass> itemList;
    private FoodAdapter foodAdapter;
    private SearchView searchView;

    private TextView mealTitle;

    private Button newFoodBtn;

    // For voice feature
    private Button btnSpeak;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search_food, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        itemList = new ArrayList<>();
        getFood = new GetFood(requireContext().getApplicationContext(), this);
        foodAdapter = new FoodAdapter(requireContext(), itemList, this, true);

        if(SingletonFoodSearchResult.getInstance().getCompleteFoodItemList().isEmpty()) {
            Log.d("SearchForFood","CompleteFoodItem List is empty, getAllFood from db");
            getAllFood = new GetAllFood(requireContext().getApplicationContext(), this);
            getAllFood.execute(); // This is to get all food in database and save in SingletonFood
        }else{
            updateCompleteFoodList();
        }

        mealTitle = view.findViewById(R.id.textView3);
        mealTitle.setText(SingletonFoodSearchResult.getInstance().getCurrentMeal());

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setAdapter(foodAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus(); // Remove cursor from search bar

        newFoodBtn = view.findViewById(R.id.cameraIButton);
        newFoodBtn.setOnClickListener(v -> {
            FragmentActivity activity = getActivity();
            if (activity instanceof MainActivity2) {
                ((MainActivity2) activity).replaceFragment(new InputNewFood(), "inputNewFood", true);
            }
        });

        // navigate to the SpeechToText page
        btnSpeak = view.findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(v -> {
            FragmentActivity activity = getActivity();
            if (activity instanceof MainActivity2) {
                ((MainActivity2) activity).replaceFragment(new SpeechToText(), "speechToText", true);
            }
        });

        // make sure to unsubscribe the subscription.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {return false;}

            // 1. User enter query text, send the text to search the db
            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.isEmpty()) //260724
                    updateCompleteFoodList(); //getAllFood.execute();
                else
                    getFood.execute(newText); // This is to get from search query, Result get from Singleton in afterProcess

                return true;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchView.setOnQueryTextListener(null);
    }

    public void updateCompleteFoodList(){

        itemList.clear();
        ArrayList<FoodItemClass> allFoodList = SingletonFoodSearchResult.getInstance().getCompleteFoodItemList();
        for (FoodItemClass fitem : allFoodList) {
            if (itemList.size() <= 10) {  //add the top 10 recommended item
                if (fitem.isRecommended()) {
                    itemList.add(fitem);
                }
            }
        }
        foodAdapter.setFilteredList(itemList);
    }

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {
        // ALL PROCESSES AFTER DATABASE CALL MUST BE WRITTEN HERE !!
        if(executeStatus) {
            if (returnClass.isInstance(getAllFood)) {
                updateCompleteFoodList();
            } else {
                itemList.clear();
                ArrayList<FoodItemClass> searchedFoodList = SingletonFoodSearchResult.getInstance().getFoodSearchResult();
                for (FoodItemClass fitem : searchedFoodList) {
                    if (itemList.size() <= 10) {  //add the top 10 found items
                        itemList.add(fitem);
                    }
                }
            }
            foodAdapter.setFilteredList(itemList);
        }
    }

    @Override
    public void onItemClick(int itemPos) {
        //grab user selection
        FoodItemClass foodItemSelected = itemList.get(itemPos);
        // Move on to addfood page
        switchFragment(foodItemSelected);
    }

    private void switchFragment(FoodItemClass foodItemSelected) {
        FragmentActivity activity = getActivity();
        if (activity instanceof MainActivity2) {
            SingletonFoodSearchResult.getInstance().setSelectedFoodFromSearchResult(foodItemSelected);
            ((MainActivity2) activity).replaceFragment(new AddFood(), "addFood", true);
        }
    }

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {}

}
