package sg.edu.np.mad.mad24p03team2;

import android.app.DatePickerDialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.AccountClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.UpdateUserProfile;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

/**
 * EditProfile
 * UI-Fragment that allows editing of current login user profile
 */
public class EditProfile extends AppCompatActivity implements IDBProcessListener {
    UpdateUserProfile updateUserProfile = null;

    private EditText birthdateText;
    private EditText weightText;
    private EditText heightText;
    private Button male;
    private Button female;
    private ImageView femaleIconView;
    private ImageView maleIconView;
    String selGender = "F";

    int year = 1;
    int mth = 1;
    int day = 1;

    private ImageView calendarIconView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        updateUserProfile = new UpdateUserProfile(getApplicationContext(), this);

        // Find Buttons from the layout
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        maleIconView = findViewById(R.id.male_icon);
        femaleIconView = findViewById(R.id.female_icon);
        calendarIconView = findViewById(R.id.imageView6);
        Button saveBtn = findViewById(R.id.save);
        Button cancelBtn = findViewById((R.id.cancelBtn));

        // All texts
        birthdateText = findViewById(R.id.birthdate);
        weightText = findViewById(R.id.weight);
        heightText = findViewById(R.id.height);

        // Populate profile form details
        populateProfileDetails();

        // Set click listeners for Buttons
        male.setOnClickListener(view -> {
            setGenderSelection("M");
        });

        female.setOnClickListener(view -> {
            setGenderSelection("F");
        });

        cancelBtn.setOnClickListener(view -> {
            finish();
        });

        // Save the inputs by user to database
        saveBtn.setOnClickListener(view -> {
            try {
                updateProfile();
                finish(); //ended editing, goes back to Account page

            } catch (NumberFormatException e) {
                e.printStackTrace();
                Toast.makeText(this, "Invalid number format", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "An unexpected error occurred", Toast.LENGTH_SHORT).show();
            }
        });

        //build a date picker
        calendarIconView.setOnClickListener(v -> {
            //get an instance of our calendar
            final Calendar cal = Calendar.getInstance();
            cal.setTime(SingletonSession.getInstance().GetAccount().getBirthDate());

            year = cal.get(Calendar.YEAR);
            mth = cal.get(Calendar.MONTH);
            day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(EditProfile.this,
                    (view, year, month, dayOfMonth) ->{
                            birthdateText.setText(GlobalUtil.formatBirthDatesForUIDisplay(dayOfMonth, month+1, year));
                            this.year = year;
                            this.mth = month;
                            this.day = day;

                    }, year, mth, day);
            datePickerDialog.show();
        });
    }


    private void populateProfileDetails() {
        AccountClass currentUserProfile = SingletonSession.getInstance().GetAccount();
        if (currentUserProfile != null) {
            birthdateText.setText(GlobalUtil.DateFormatter.format(currentUserProfile.getBirthDate()));
            weightText.setText(String.valueOf(currentUserProfile.getWeight()));
            heightText.setText(String.valueOf(currentUserProfile.getHeight()));
            // Set the gender selection based on the profile
            setGenderSelection(currentUserProfile.getGender());
        } else {
            Toast.makeText(this, "Failed to load profile details", Toast.LENGTH_SHORT).show();
        }
    }

    private void setGenderSelection(String gender) {
        ColorStateList maleDefaultColorStateList = getResources().getColorStateList(R.color.lavender, this.getTheme());
        ColorStateList femaleDefaultColorStateList = getResources().getColorStateList(R.color.lightpink, this.getTheme());

        //save gender to variable
        selGender = gender;

        if (gender.equalsIgnoreCase("M")) {
            // Set background color and icon visibility for male selection
            male.setBackgroundTintList(getResources().getColorStateList(R.color.purple, this.getTheme()));
            female.setBackgroundTintList(femaleDefaultColorStateList);
            maleIconView.setVisibility(View.VISIBLE);
            femaleIconView.setVisibility(View.GONE);
        } else if (gender.equalsIgnoreCase("F")) {
            // Set background color and icon visibility for female selection
            male.setBackgroundTintList(maleDefaultColorStateList);
            female.setBackgroundTintList(getResources().getColorStateList(R.color.altpink, this.getTheme()));
            maleIconView.setVisibility(View.GONE);
            femaleIconView.setVisibility(View.VISIBLE);
        } else {
            // Set default background colors and hide icons if gender is not set
            male.setBackgroundTintList(maleDefaultColorStateList);
            female.setBackgroundTintList(femaleDefaultColorStateList);
            maleIconView.setVisibility(View.GONE);
            femaleIconView.setVisibility(View.GONE);
        }
    }

    private void updateProfile() throws ParseException, NumberFormatException {
        String birthdate = birthdateText.getText().toString().trim();
        float weight = Float.parseFloat(weightText.getText().toString());
        float height = Float.parseFloat(heightText.getText().toString());

        // Validate birthdate format
        if (!isDateValid(birthdate)) {
            Toast.makeText(this, "Please enter Birthdate in YYYY-MM-DD", Toast.LENGTH_SHORT).show();
            return;
        }

        // Check if all fields are filled
        if (birthdate.isEmpty() || weightText.getText().toString().isEmpty() || heightText.getText().toString().isEmpty()) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        AccountClass account = SingletonSession.getInstance().GetAccount();

        // Update profile details in database using UpdateUserProfile object
        //email, dietPlanOpt, gender, birthDate, height, weight, trackBloodSugar
        updateUserProfile.execute(account.getEmail(), account.getDietPlanOpt(),
                selGender, birthdate, Float.toString(height), Float.toString(weight),
                account.getTrackBloodSugar());
    }

    private boolean isDateValid(String stringDate) {
        // Function to validate birthdate format
        GlobalUtil.DateFormatter.setLenient(false);
        try {
            Date date = GlobalUtil.DateFormatter.parse(stringDate);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {
        runOnUiThread(() -> Toast.makeText(EditProfile.this, "Update Profile " + String.valueOf(executeStatus), Toast.LENGTH_SHORT).show());
    }

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {}

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}
}

