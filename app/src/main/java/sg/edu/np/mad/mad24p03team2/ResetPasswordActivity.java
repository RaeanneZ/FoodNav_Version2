package sg.edu.np.mad.mad24p03team2;

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
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.UpdateUserPassword;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSecurityInfoResult;

public class ResetPasswordActivity extends AppCompatActivity implements IDBProcessListener {

    UpdateUserPassword updateUserPassword;
    private Button cancelBtn;
    private Button resetBtn;

    private EditText pswdEditText;
    private EditText cfmPswdEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_reset_password);
        pswdEditText = findViewById(R.id.password);
        cfmPswdEditText = findViewById(R.id.confirmPassword);
        updateUserPassword = new UpdateUserPassword(getApplicationContext(), this);

        cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> {
            finish();
        });

        resetBtn = findViewById(R.id.resetPswdButton);
        resetBtn.setOnClickListener(v -> {
            String newPswd = pswdEditText.getText().toString();
            String cfmPswd = cfmPswdEditText.getText().toString();

            if(newPswd.isEmpty() || cfmPswd.isEmpty()){
                Toast.makeText(this, "Please fill in all the fields", Toast.LENGTH_SHORT).show();
            }else if(newPswd.compareTo(cfmPswd) != 0){
                Toast.makeText(this, "The passwords do not match", Toast.LENGTH_SHORT).show();
            }else{
                Log.d("RESETPASSWORDACTIVITY", "Update database");
                updateUserPassword.execute(SingletonSecurityInfoResult.getInstance().getSecurityInfo().getEmail(), newPswd);
            }
        });
    }

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {
        Log.d("RESETPASSWORDACTIVITY", "After Process");
        if(executeStatus){
            Toast.makeText(this, "Password Changed Successfully", Toast.LENGTH_SHORT).show();
            finish();
        }else{
            Toast.makeText(this, "Password change not successfully,\nPlease try again later", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {}

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}
}