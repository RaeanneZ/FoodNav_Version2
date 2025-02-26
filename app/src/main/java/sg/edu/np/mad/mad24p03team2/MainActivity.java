package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.sql.Connection;
import java.sql.SQLException;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.ApplicationSetUp.StartUp;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.GetCurrentUserProfile;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.LoginUser;

/**
 * MainActivity
 */
public class MainActivity extends AppCompatActivity implements IDBProcessListener {

    private LoginUser loginUser = null;
    private GetCurrentUserProfile getCurrentUserProfile = null;

    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Disable dark mode - write above Super.oncreate to avoid multiple call
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button login = findViewById(R.id.login);
        TextView signup = findViewById(R.id.signup);

        //Accessing SharedPreference for auto-login if RememberMe is clicked.
        if (!handleRememberMe()) {
            login.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, LoginActivity.class)));
            signup.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, DisclaimerActivity.class)));
        } else {
            //allow auto-login to take place
            login.setVisibility(View.GONE);
            signup.setVisibility(View.GONE);
        }
    }

    private boolean handleRememberMe() {

        //Read shared preference for auto login if REMEMBER ME was checked
        EncryptedSharedPreferences sharedPreferences = GlobalUtil.getEncryptedSharedPreference(getApplicationContext());
        if (sharedPreferences == null)
            return false;

        email = sharedPreferences.getString(GlobalUtil.SHARED_PREFS_LOGIN_KEY, "");
        String password = sharedPreferences.getString(GlobalUtil.SHARED_PREFS_LOGIN_PSWD, "");

        if (email.isEmpty() || password.isEmpty()) {
            return false; //do nothing and return to load landing page
        }

        //proceed to auto login
        loginUser = new LoginUser(getApplicationContext(), this);
        loginUser.execute(email, password);

        return true;
    }


    @Override
    public void onPause() {
        super.onPause();

        Log.d("Dashboard", "Pause");

    }

    @Override
    public void onResume() {
        super.onResume();
        final StartUp app = (StartUp)this.getApplicationContext();
        Connection dbCon = app.getConnection();
        try {
            if(dbCon.isClosed())
                Log.d("MainActivity","onResume - Database closed!");
        } catch (SQLException e) {
            Log.d("MainActivity","Error testing dbCon = "+e.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        final StartUp app = (StartUp)this.getApplicationContext();
        Connection dbCon = app.getConnection();
        try {
            if(dbCon.isClosed())
                Log.d("MainActivity","onStart - Database closed!");
        } catch (SQLException e) {
            Log.d("MainActivity","onStart Error testing dbCon = "+e.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("MainActivity", "OnStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("MainActivity", "onDestroy");
    }

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {
        if (isValidPwd && isValidUser) {
            //moving on to next page
            //Intent login = new Intent(MainActivity.this, MainActivity2.class);
           // startActivity(login);
           // this.finish();  //offload page

            getCurrentUserProfile = new GetCurrentUserProfile(getApplicationContext(), this);
            // Grab current user profile and store into SingletonSession
            runOnUiThread(() -> getCurrentUserProfile.execute(email));

            Intent loginAnimate = new Intent(MainActivity.this, LoginAnimate.class);
            startActivity(loginAnimate);
            this.finish();  //offload login page
        }
    }


    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {}

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}
}