package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.DietPlanClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.GetDietPlanOption;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietPlanResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

/**
 * SelectionActivity3
 * UI-Activity page showed during sign up to display recommended daily macros and calories intake based
 * on input gender and diet plan selected
 */
public class SelectionActivity3 extends AppCompatActivity implements IDBProcessListener {
    GetDietPlanOption getDietPlanOption = null;

    TextView carbs;
    TextView sugar;
    TextView fats;
    TextView cal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup_option_lsugar2);
        getDietPlanOption = new GetDietPlanOption(getApplicationContext(), this);

        //this is to disable backButton
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {} //do nothing
        });

        carbs = findViewById(R.id.tvc1);
        sugar = findViewById(R.id.tvp1);
        fats = findViewById(R.id.tvf1);
        cal = findViewById(R.id.tvk1);

        //Get Diet plan from DB ready for display
        getDietPlanOption.execute("Diabetic Friendly", SingletonSession.getInstance().GetAccount().getGender());

        Button nextBtn = findViewById(R.id.button);
        nextBtn.setOnClickListener(v -> {
            // Move to next page
            Intent intent = new Intent(SelectionActivity3.this, MainActivity2.class);
            finishAffinity(); ///sign-up progess ended, clear all ACTIVITIES involved
            startActivity(intent);
        });
    }

    private void updateUI() {
        DietPlanClass dietPlanInfo = SingletonDietPlanResult.getInstance().getDietPlan();
        // for Carbs
        carbs.setText(String.valueOf(dietPlanInfo.getReccCarbIntake()));
        // for Protein
        sugar.setText(String.valueOf(dietPlanInfo.getReccSugarIntake()));
        // for Fats
        fats.setText(String.valueOf(dietPlanInfo.getReccFatsIntake()));
        // for cal
        String kCalAmt = String.valueOf(dietPlanInfo.getReccCaloriesIntake());
        cal.setText(kCalAmt + "\nKcal");
    }


    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {
        if (executeStatus)
            updateUI();
    }

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {}
}
