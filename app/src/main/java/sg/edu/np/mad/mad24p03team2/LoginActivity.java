package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.GetCurrentUserProfile;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.LoginUser;


/**
 * LoginActivity
 * UI-Activity User login page
 */
public class LoginActivity extends AppCompatActivity implements IDBProcessListener {
    EditText emailComponent, passwordComponent;
    String email, password;
    Button loginBtn;
    Button cancelBtn;
    TextView forgotPasswordPrompt;
    CheckBox remMeCheckBox;

    LoginUser loginUser = null;
    GetCurrentUserProfile getCurrentUserProfile = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loading), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailComponent = findViewById(R.id.email);
        passwordComponent = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        cancelBtn = findViewById(R.id.cancelBtn);
        remMeCheckBox = findViewById(R.id.checkBox);
        forgotPasswordPrompt = findViewById(R.id.textViewForgotPswd);

        loginUser = new LoginUser(getApplicationContext(), this);
        getCurrentUserProfile = new GetCurrentUserProfile(getApplicationContext(), this);

        loginBtn.setOnClickListener(v -> {
            email = emailComponent.getText().toString();
            password = passwordComponent.getText().toString();
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Please fill in the fields", Toast.LENGTH_SHORT).show();
            } else {
                loginUser.execute(email, password);
            }
        });

        cancelBtn.setOnClickListener(v -> {
            //finish current activity to go back -> Homepage
            this.finish();
        });

        forgotPasswordPrompt.setOnClickListener(v -> {
            //prompt user for particulars to
            Intent resetPswd = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(resetPswd);
        });
    }


    //Methods from IDBProcessListener
    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {
        // User Login process will return 2 boolean flag to indicate whether its wrong username or
        // wrong password that caused LOGIN UNSUCCESSFUL
        // Please update your UI here
         if(!isValidPwd ||!isValidUser) {
             Toast.makeText(LoginActivity.this, "Incorrect email or password entered.", Toast.LENGTH_SHORT).show();
             //successfully login
         }else {
            //write to sharedpref if RememberMe is checked
            if (remMeCheckBox.isChecked()) {
                EncryptedSharedPreferences sharedPreferences = GlobalUtil.getEncryptedSharedPreference(getApplicationContext());
                if (sharedPreferences != null) {
                    //save email and password login
                    sharedPreferences.edit().putString(GlobalUtil.SHARED_PREFS_LOGIN_KEY, email).apply();
                    sharedPreferences.edit().putString(GlobalUtil.SHARED_PREFS_LOGIN_PSWD, password).apply();
                }
            }

            // Grab current user profile and store into SingletonSession
            runOnUiThread(() -> getCurrentUserProfile.execute(email));

             Intent loginAnimate = new Intent(LoginActivity.this, LoginAnimate.class);
             startActivity(loginAnimate);
             finish();  //offload login page
        }
    }

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends
            AsyncTaskExecutorService> returnClass) {}

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends
            AsyncTaskExecutorService> returnClass) {}
}