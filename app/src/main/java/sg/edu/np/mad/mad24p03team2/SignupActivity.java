package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.UiThread;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;

import java.util.regex.Pattern;

import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.CheckEmailExist;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.RegisterUser;
import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSignUp;

/**
 * SignupActivity
 * UI-Activity signup page
 */
public class SignupActivity extends AppCompatActivity implements IDBProcessListener {

    private EditText nameComponent, emailComponent, pwdComponent, cfpwdComponent;
    private String name, email, password, confirm;
    private Button signUpBtn;
    private Button cancelBtn;
    private CheckBox remMeCheckBox;
    private RegisterUser registerUser = null;
    private CheckEmailExist checkEmailExist = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loading), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //this is to disable backButton
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {} //do nothing
        });

        nameComponent = findViewById(R.id.Name);
        emailComponent = findViewById(R.id.email);
        pwdComponent = findViewById(R.id.password);
        cfpwdComponent = findViewById(R.id.confirmPassword);
        remMeCheckBox = findViewById(R.id.checkBox);
        signUpBtn = findViewById(R.id.signUpBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        // Initialize database interaction object
        checkEmailExist = new CheckEmailExist(getApplicationContext(), this);
        registerUser = new RegisterUser(getApplicationContext(), this);

        cancelBtn.setOnClickListener(v -> {
            //finish current activity to go back -> Homepage
            this.finish();
        });

        // Set click listener for sign up button
        signUpBtn.setOnClickListener(v -> {
            name = nameComponent.getText().toString();
            email = emailComponent.getText().toString();
            password = pwdComponent.getText().toString();
            confirm = cfpwdComponent.getText().toString();

            // Input validation
            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Please fill in the fields!", Toast.LENGTH_SHORT).show();
            } else if (!Pattern.matches("[a-zA-Z0-9 ]+", name) || name.length() > 100) {
                // Check if username only contains letters
                Toast.makeText(SignupActivity.this, "Invalid Username!", Toast.LENGTH_SHORT).show();
            }
            else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Log.d("SIGNUP", "INVALID EMAIL");
                Toast.makeText(SignupActivity.this, "Invalid Email!", Toast.LENGTH_SHORT).show();
            }else if (!password.equals(confirm)) {
                // Display error message - passwords don't match
                Toast.makeText(SignupActivity.this, "Passwords do not match!", Toast.LENGTH_SHORT).show();
            } else if (password.isEmpty()) {
                Toast.makeText(SignupActivity.this, "Password should not be empty!", Toast.LENGTH_SHORT).show();

            } else {
                //check that email entered is not already in DB because it has to be unique
                checkEmailExist.execute(email);
            }
        });
    }

    private void processSignup(){
        // Attempt to register user in database
        SingletonSignUp.getInstance().initSignUp(name, email, password, getApplicationContext());
        SingletonSignUp.getInstance().setRememberMe(remMeCheckBox.isChecked());

        // Navigate to ProfileActivity
        Intent profileIntent = new Intent(SignupActivity.this, SecuritySetupActivity.class);
        startActivity(profileIntent);
        finish();
    }

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {
    }

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {
        if(!isValidUser){
            runOnUiThread(()->processSignup());
        }else{
            runOnUiThread(()->Toast.makeText(this, "Email Already Used, Please use an alternate email.", Toast.LENGTH_SHORT).show());
        }
    }

}