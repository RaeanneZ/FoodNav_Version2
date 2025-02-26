package sg.edu.np.mad.mad24p03team2;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.AccountClass;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSignUp;

/**
 * ProfileActivity
 * UI-Activity allowing user to enter profile during sign-up
 */
public class ProfileActivity extends AppCompatActivity {

    private EditText birthdateText;
    private EditText weightText;
    private EditText heightText;
    private Button male;
    private Button female;
    private ImageView femaleIconView;
    private ImageView maleIconView;
    private String selGender = "F";

    private final Calendar cal = Calendar.getInstance();
    private int calYear = 1;
    private int calMonth = 1;
    private int calDay = 1;

    private ImageView calendarIconView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        //this is to disable backButton
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {} //do nothing
        });

        // Find Buttons from the layout
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        maleIconView = findViewById(R.id.male_icon);
        femaleIconView = findViewById(R.id.female_icon);

        calendarIconView = findViewById(R.id.imageView6);
        Button saveBtn = findViewById(R.id.save);

        // All texts
        weightText = findViewById(R.id.weight);
        heightText = findViewById(R.id.height);

        birthdateText = findViewById(R.id.birthdate);
        //get an instance of our calendar
        calYear = cal.get(Calendar.YEAR);
        calMonth = cal.get(Calendar.MONTH);
        calDay = cal.get(Calendar.DAY_OF_MONTH);
        birthdateText.setText(calYear+"-"+(calMonth+1)+"-"+calDay);

        //set to default at start
        setGenderSelection(selGender);

        // Set click listeners for Buttons
        male.setOnClickListener(view -> {
            setGenderSelection("M");
        });

        female.setOnClickListener(view -> {
            setGenderSelection("F");
        });

        //build a date picker
        calendarIconView.setOnClickListener(v-> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(ProfileActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            birthdateText.setText(GlobalUtil.formatBirthDatesForUIDisplay(dayOfMonth, month+1, year));
                            //update to the already selected time and date
                            calYear = year;
                            calMonth = month;
                            calDay=dayOfMonth;
                        }
                    }, calYear, calMonth, calDay);
            datePickerDialog.show();
        });

        // Save the inputs by user to database
        saveBtn.setOnClickListener(v-> {
            try {
                updateProfile();
                Intent intent = new Intent(ProfileActivity.this, SelectionActivity.class);
                startActivity(intent);
                finish();
            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setGenderSelection(String gender) {
        selGender = gender;//store selection

        ColorStateList maleDefaultColorStateList = getResources().getColorStateList(R.color.lavender, this.getTheme());
        ColorStateList femaleDefaultColorStateList = getResources().getColorStateList(R.color.lightpink, this.getTheme());

        if (gender.equalsIgnoreCase("M")) {
            ColorStateList newColorStateList = getResources().getColorStateList(R.color.purple, this.getTheme());
            male.setBackgroundTintList(newColorStateList);
            female.setBackgroundTintList(femaleDefaultColorStateList);
            maleIconView.setVisibility(View.VISIBLE);
            femaleIconView.setVisibility(View.GONE);
        } else if (gender.equalsIgnoreCase("F")) {
            ColorStateList newColorStateList = getResources().getColorStateList(R.color.altpink, this.getTheme());
            male.setBackgroundTintList(maleDefaultColorStateList);
            female.setBackgroundTintList(newColorStateList);
            maleIconView.setVisibility(View.GONE);
            femaleIconView.setVisibility(View.VISIBLE);
        } else {
            male.setBackgroundTintList(maleDefaultColorStateList);
            female.setBackgroundTintList(femaleDefaultColorStateList);
            maleIconView.setVisibility(View.GONE);
            femaleIconView.setVisibility(View.GONE);
        }
    }
    private void updateProfile() throws ParseException, NumberFormatException {

        float weight = Float.parseFloat(weightText.getText().toString());
        float height = Float.parseFloat(heightText.getText().toString());

        String birthdate = birthdateText.getText().toString().trim();
        Date birthDate = GlobalUtil.DateFormatter.parse(birthdate);

        if (birthdate.isEmpty() || weightText.getText().toString().isEmpty() || heightText.getText().toString().isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        //part of the signup process, so save profile locally till end of process then commit to DB
        SingletonSignUp.getInstance().setProfile(birthDate, selGender, height, weight);
    }
}

