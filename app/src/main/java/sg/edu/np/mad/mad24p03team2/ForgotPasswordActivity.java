package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.GetSecurityInfo;

public class ForgotPasswordActivity extends AppCompatActivity  implements IDBProcessListener {

    private GetSecurityInfo getSecurityInfo;
    private EditText editTxtEmail;
    private Button nextBtn;
    private Button cancelBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password);

        getSecurityInfo = new GetSecurityInfo(getApplicationContext(), this);

        editTxtEmail = findViewById(R.id.email);

        nextBtn = findViewById(R.id.nextBtn);
        cancelBtn = findViewById(R.id.cancelBtn);


        cancelBtn.setOnClickListener(v -> {
            //goes back to previous page
            this.finish();
        });

        nextBtn.setOnClickListener(v -> {
            //check all info are correct
            String email = editTxtEmail.getText().toString();
            // Check if all fields are filled
            if (email.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            Log.d("ForgotPasswordPrompt","Get Security Info for "+email);
            getSecurityInfo.execute(email);
        });
    }

    private void processChangePassword() {
        Intent changePasswordIntent = new Intent(this, ForgotPswdSecurityActivity.class);
        startActivity(changePasswordIntent);

        //remove this page
        this.finish();
    }

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {
        if(executeStatus){
            //that means account exist
            runOnUiThread(()->processChangePassword());
        }
    }

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {}

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}
}