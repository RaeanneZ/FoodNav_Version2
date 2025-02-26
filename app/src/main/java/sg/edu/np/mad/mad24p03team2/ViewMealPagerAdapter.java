package sg.edu.np.mad.mad24p03team2;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.adapter.FragmentViewHolder;

import java.util.List;

import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

/**
 *ViewPager2 adapter for LogFoodProduct Page -> Tab
 * To handle display of LogMealAndBSugar Pane depending on tab selection
 */
public class ViewMealPagerAdapter extends FragmentStateAdapter {

    LogMealAndBSugarPane bFastSugarLog = null;
    LogMealAndBSugarPane lunchSugarLog = null;
    LogMealAndBSugarPane dinnerSugarLog = null;

    Context parentContext;

    public ViewMealPagerAdapter(Fragment fragment, Context pContext) {
        super(fragment);
        parentContext = pContext;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        //return the meal associated panel to display depending on the tab selected
        switch (position) {
            case 1:
                if (lunchSugarLog == null)
                    lunchSugarLog = new LogMealAndBSugarPane("Lunch", this.parentContext);
                return lunchSugarLog;
            case 2:
                if (dinnerSugarLog == null)
                    dinnerSugarLog = new LogMealAndBSugarPane("Dinner", this.parentContext);
                return dinnerSugarLog;
            case 0:
            default:
                if (bFastSugarLog == null)
                    bFastSugarLog = new LogMealAndBSugarPane("Breakfast", this.parentContext);
                return bFastSugarLog;
        }
    }

    @Override
    public int getItemCount() {
        //return number of tabs
        return 3;
    }


}
