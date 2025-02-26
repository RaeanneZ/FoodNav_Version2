package sg.edu.np.mad.mad24p03team2;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.HashMap;

import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonFoodSearchResult;

/**
 * LogFoodProduct
 * UI-Fragment, parent page to display food eaten for each meal
 * User can toggle between the meals by tapping or swiping
 */
public class LogFoodProduct extends Fragment {

    public static final HashMap<Integer, String> MealIndex = new HashMap<Integer, String>();
    static{
        MealIndex.put(0, "Breakfast");
        MealIndex.put(1, "Lunch");
        MealIndex.put(2, "Dinner");
    }

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private ViewMealPagerAdapter mealPagerAdapter;

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager2= view.findViewById(R.id.viewPager2);
        mealPagerAdapter = new ViewMealPagerAdapter( this, view.getContext());
        viewPager2.setAdapter(mealPagerAdapter);
        viewPager2.setOffscreenPageLimit(1);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SingletonFoodSearchResult.getInstance().setCurrentMeal(tab.getText().toString());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        //to bind tabLayout and viewpager2 together so both component are in sync
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(MealIndex.get(position))
        ).attach();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_log_food, container, false);
        return view;// Return the inflated view
    }
}