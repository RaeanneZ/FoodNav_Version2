package sg.edu.np.mad.mad24p03team2;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IMealRecyclerViewInterface;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodItemClass;

/**
 * MealFoodAdapter
 * Manipulation and handles meal_item_view.xml that is added to the Recycler View in
 * each LogMealAndBSugarPane
 */

public class MealFoodAdapter extends RecyclerView.Adapter<MealFoodAdapter.MealFoodItemViewHolder> {

    private Context context;
    private static List<MealItem> foodItemList;
    private IMealRecyclerViewInterface mealRecyclerViewInterface;
    private static String mealName = "Breakfast";

    public void setFilteredList(String mealName, Map<FoodItemClass, Integer> foodList) {
        foodItemList = new ArrayList<MealItem>();
        int servings = 1;

        //repackage passed in Map<> foodList into ArrayList so all info for accessibility
        //as well as for easy retrieval of foodItem when MealViewItemHolder return position of
        // object clicked in recycler view.
        //Map<> does not have a fixed object index.
        MealItem mItem;
        for (FoodItemClass fItem : foodList.keySet()) {
            servings = foodList.get(fItem);
            mItem = new MealItem(fItem.getId(), fItem.getName(), servings, fItem.getCalories() * servings);
            foodItemList.add(mItem);
        }

        notifyDataSetChanged(); // Alert UI that dataset has been changed
    }

    public MealFoodAdapter(Context context, IMealRecyclerViewInterface recyclerViewInterface, String mealName){
        this.context = context;
        this.mealRecyclerViewInterface = recyclerViewInterface;
        this.mealName = mealName;
    }

    @NonNull
    @Override
    public MealFoodItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.meal_item_view, parent, false);
        return new MealFoodAdapter.MealFoodItemViewHolder(view, mealRecyclerViewInterface);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MealFoodAdapter.MealFoodItemViewHolder holder, int position) {
        // Assigning values to the views created based on position of Object<meal_item_view> in the recycler view
        if (foodItemList == null || foodItemList.isEmpty()) {
            return;
        }

        MealItem item = foodItemList.get(position);
        holder.foodId = item.getId();
        holder.nameView.setText(item.getName());
        holder.calNumView.setText(String.format("%.1f", item.getCalories()));
        holder.servingSizeView.setText(String.format("%d", item.getServings()));
    }

    @Override
    public int getItemCount() {
        // Total number of items to be displayed
        if(foodItemList == null)
            return 0;

        if(foodItemList.size()>4)
            return 4;

        return foodItemList.size();
    }


    public static class MealFoodItemViewHolder extends RecyclerView.ViewHolder {
        // Grabbing the views from layout file
        TextView nameView;
        TextView calNumView;
        TextView servingSizeView;
        ImageButton delButton;

        //to store foodID of fooditem displayed in this panel
        int foodId;

        public MealFoodItemViewHolder(@NonNull View itemView, IMealRecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            nameView = itemView.findViewById(R.id.Name);
            calNumView = itemView.findViewById(R.id.calNum);
            servingSizeView = itemView.findViewById(R.id.servingSize);

            //when on button clicked
            delButton = itemView.findViewById(R.id.btnDelete);
            delButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getBindingAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                                recyclerViewInterface.onItemClick(foodId);
                        }
                    }
                }
            });
        }
    }

    //Locally used for repackaging of FoodItemClass
    private static class MealItem {

        int id;
        double calories;
        String name;
        int servings;

        public MealItem(int id, String name, int servings, double calories) {
            this.calories = calories;
            this.id = id;
            this.servings = servings;
            this.name = name;
        }

        public int getId() {
            return id;
        }
        public double getCalories() {
            return calories;
        }
        public String getName() {
            return name;
        }
        public int getServings() {
            return servings;
        }
    }
}


