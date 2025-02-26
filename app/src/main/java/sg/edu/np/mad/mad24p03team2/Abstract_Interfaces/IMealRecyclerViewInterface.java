package sg.edu.np.mad.mad24p03team2.Abstract_Interfaces;

/**
 * IMealRecyclerViewInterface
 * A callback to implementer
 * This is used in LogMealAndBSugarPane
 * Return value of which food-object is clicked in the food listing (recyclerview)
 */
public interface IMealRecyclerViewInterface {
    void onItemClick(int foodId);
}
