package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietConstraints;

public class DietConstraintSetup extends AppCompatActivity {

    private CheckBox cbVegan;
    private CheckBox cbVegeterian;
    private CheckBox cbDiaryFree;
    private CheckBox cbSoyFree;
    private CheckBox cbSugarFree;
    private CheckBox cbNoSeafood;
    private CheckBox cbNutFree;
    private CheckBox cbEggless;
    private CheckBox cbGlutenFree;

    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_diet_constraint_setup);

        //this is to disable backButton
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {} //do nothing
        });

        cbVegan = findViewById(R.id.checkBoxVegan);
        cbVegeterian= findViewById(R.id.checkBoxVegeterian);
        cbDiaryFree= findViewById(R.id.checkBoxDairy);
        cbSoyFree= findViewById(R.id.checkBoxSoy);
        cbSugarFree= findViewById(R.id.checkBoxSugar);
        cbNoSeafood= findViewById(R.id.checkBoxSeafood);
        cbNutFree= findViewById(R.id.checkBoxNuts);
        cbEggless= findViewById(R.id.checkBoxEgg);
        cbGlutenFree= findViewById(R.id.checkBoxGluten);

        save = findViewById(R.id.saveBtn);
        save.setOnClickListener(v -> {
            SingletonDietConstraints.getInstance().setDietProfile(getUpdatedDietPreference());

            Intent login = new Intent(DietConstraintSetup.this, SelectionActivity2.class);
            startActivity(login);
            finish();
        });
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

}