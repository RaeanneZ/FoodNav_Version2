package sg.edu.np.mad.mad24p03team2;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.UpdateDietConstraints;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietConstraints;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

public class DietConstraintActivity extends AppCompatActivity implements IDBProcessListener {

    private CheckBox cbVegan;
    private CheckBox cbVegeterian;
    private CheckBox cbDiaryFree;
    private CheckBox cbSoyFree;
    private CheckBox cbSugarFree;
    private CheckBox cbNoSeafood;
    private CheckBox cbNutFree;
    private CheckBox cbEggless;
    private CheckBox cbGlutenFree;

    UpdateDietConstraints updateDietConstraints = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diet_constraint);


        cbVegan =findViewById(R.id.checkBoxVegan);
        cbVegeterian= findViewById(R.id.checkBoxVegeterian);
        cbDiaryFree= findViewById(R.id.checkBoxDairy);
        cbSoyFree= findViewById(R.id.checkBoxSoy);
        cbSugarFree= findViewById(R.id.checkBoxSugar);
        cbNoSeafood= findViewById(R.id.checkBoxSeafood);
        cbNutFree= findViewById(R.id.checkBoxNuts);
        cbEggless= findViewById(R.id.checkBoxEgg);
        cbGlutenFree= findViewById(R.id.checkBoxGluten);

        //grab from SingletonDietConstraints the setting previously and check accordingly
        //update checkbox on selection
        updateDietPreferences(SingletonDietConstraints.getInstance().getDietProfile());

        Button save = findViewById(R.id.saveBtn);
        updateDietConstraints = new UpdateDietConstraints(getApplicationContext(), this);
        save.setOnClickListener(v -> {
            SingletonDietConstraints.getInstance().setDietProfile(getUpdatedDietPreference());
            updateDietConstraints.execute(Integer.toString(SingletonSession.getInstance().GetAccount().getId()));
            this.finish();
        });

        Button cancel = findViewById(R.id.cancelBtn);
        cancel.setOnClickListener(v -> finish());
    }
    private ArrayList<String> getUpdatedDietPreference(){
        ArrayList<String> newPref = new ArrayList<>();
        if(cbEggless.isChecked())
            newPref.add(SingletonDietConstraints.DIET_CONSTRAINTS.EGGLESS.name().toLowerCase());
        if(cbVegeterian.isChecked())
            newPref.add(SingletonDietConstraints.DIET_CONSTRAINTS.VEGETARIAN.name().toLowerCase());
        if(cbGlutenFree.isChecked())
            newPref.add(SingletonDietConstraints.DIET_CONSTRAINTS.GLUTEN_FREE.name().toLowerCase());
        if(cbNutFree.isChecked())
            newPref.add(SingletonDietConstraints.DIET_CONSTRAINTS.NUT_FREE.name().toLowerCase());
        if(cbNoSeafood.isChecked())
            newPref.add(SingletonDietConstraints.DIET_CONSTRAINTS.NO_SEAFOOD.name().toLowerCase());
        if(cbSugarFree.isChecked())
            newPref.add(SingletonDietConstraints.DIET_CONSTRAINTS.SUGAR_FREE.name().toLowerCase());
        if(cbSoyFree.isChecked())
            newPref.add(SingletonDietConstraints.DIET_CONSTRAINTS.SOY_FREE.name().toLowerCase());
        if(cbDiaryFree.isChecked())
            newPref.add(SingletonDietConstraints.DIET_CONSTRAINTS.DAIRY_FREE.name().toLowerCase());
        if(cbVegan.isChecked())
            newPref.add(SingletonDietConstraints.DIET_CONSTRAINTS.VEGAN.name().toLowerCase());

        return newPref;
    }

    private void updateDietPreferences(ArrayList<SingletonDietConstraints.DIET_CONSTRAINTS> dietProfile){
        Log.d("DietConstraintActivity", "dietProfile size = "+ dietProfile.size());

        for(SingletonDietConstraints.DIET_CONSTRAINTS constraint : dietProfile) {
            Log.d("DietConstraintActivity", "****diet constraint = "+constraint.name());
            switch (constraint) {
                case VEGAN:
                    cbVegan.setChecked(true);
                    break;
                case VEGETARIAN:
                    cbVegeterian.setChecked(true);
                    break;
                case DAIRY_FREE:
                    cbDiaryFree.setChecked(true);
                    break;
                case SOY_FREE:
                    cbSoyFree.setChecked(true);
                    break;
                case SUGAR_FREE:
                    cbSugarFree.setChecked(true);
                    break;
                case NO_SEAFOOD:
                    cbNoSeafood.setChecked(true);
                    break;
                case NUT_FREE:
                    cbNutFree.setChecked(true);
                    break;
                case GLUTEN_FREE:
                    cbGlutenFree.setChecked(true);
                    break;
                case EGGLESS:
                    cbEggless.setChecked(true);
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {}

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {}

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}
}