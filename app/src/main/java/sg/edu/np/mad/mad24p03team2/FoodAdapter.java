package sg.edu.np.mad.mad24p03team2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.FoodItemClass;

/**
 * FoodAdapter
 * RecyclerView-Adapter for RecyclerView component in SearchFood and FoodToNom
 */
public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {

    private Context context;
    private ArrayList<FoodItemClass> items = new ArrayList<FoodItemClass>();
    private final RecyclerViewInterface recyclerViewInterface;

    private boolean showRecommendation = false;

    public void setFilteredList(ArrayList<FoodItemClass> filteredList) {
        this.items.clear();
        this.items.addAll(filteredList);
        notifyDataSetChanged(); // Alert UI that dataset has been changed
    }

    public FoodAdapter(ArrayList<FoodItemClass> itemList, RecyclerViewInterface recyclerViewInterface, boolean recommend) {
        if (itemList != null && !itemList.isEmpty())
            this.items.addAll(itemList);

        this.recyclerViewInterface = recyclerViewInterface;
        this.showRecommendation = recommend;

    }

    public FoodAdapter(Context context, ArrayList<FoodItemClass> items, RecyclerViewInterface recyclerViewInterface, boolean recommend) {
        this(items, recyclerViewInterface, recommend);
        this.context = context;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout
        LayoutInflater inflator = LayoutInflater.from(context);
        View view = inflator.inflate(R.layout.item_view, parent, false);
        return new FoodAdapter.FoodViewHolder(view, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        // Assigning values to the views created based on position of the recycler view
        if (items == null || items.isEmpty()) {
            return;
        }

        FoodItemClass item = items.get(position);
        holder.nameView.setText(item.getName());
        holder.calNumView.setText(String.format("%.1f", item.getCalories()));
        holder.servingSizeView.setText(String.format("%.1f", item.getServing_size_g()));
        if (showRecommendation) {
            if (item.isRecommended())
                holder.recommendIcon.setVisibility(View.VISIBLE);
            else
                holder.recommendIcon.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        // Total number of items to be displayed
        if (items == null)
            return 0;

        return items.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        // Grabbing the views from layout file
        TextView nameView;
        TextView calNumView;
        TextView servingSizeView;

        ImageButton recommendIcon;

        public FoodViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            nameView = itemView.findViewById(R.id.Name);
            calNumView = itemView.findViewById(R.id.calNum);
            servingSizeView = itemView.findViewById(R.id.servingSize);
            recommendIcon = itemView.findViewById(R.id.ImageViewRecom);

            itemView.setOnClickListener(v -> {
                if (recyclerViewInterface != null) {
                    int pos = getBindingAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        recyclerViewInterface.onItemClick(pos);
                    }
                }
            });
        }
    }
}

