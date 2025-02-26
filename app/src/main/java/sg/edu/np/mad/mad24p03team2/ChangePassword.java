package sg.edu.np.mad.mad24p03team2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.LoginUser;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.UpdateUserPassword;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

/**
 * ChangePassword
 * UI-Activity to allow current login user to change password and update Model
 */
public class ChangePassword extends AppCompatActivity implements IDBProcessListener {

    private LoginUser verifyOldPassword;
    private UpdateUserPassword updateUserPassword;
    private EditText newPasswordEditText;
    private EditText oldPasswordEditText;
    private EditText confirmPasswordEditText;

    private String newPassword;
    private String oldPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_change_password);

        // Find UI elements for password input based on IDs from your layout
        oldPasswordEditText = findViewById(R.id.oldPassword);
        newPasswordEditText = findViewById(R.id.password); // New password field as per your layout
        confirmPasswordEditText = findViewById(R.id.confirmPassword); // Confirm password field as per your layout

        verifyOldPassword = new LoginUser(getApplicationContext(), this);
        updateUserPassword = new UpdateUserPassword(getApplicationContext(), this);

        Button cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> {
            this.finish();
        });

        // Button click listener to update password
        Button updatePasswordButton = findViewById(R.id.confirmPasswordButton); // Update password button ID
        updatePasswordButton.setOnClickListener(v -> {
            newPassword = newPasswordEditText.getText().toString();
            oldPassword = oldPasswordEditText.getText().toString();
            String confirmPassword = confirmPasswordEditText.getText().toString();

            // Input validation
            if (oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(ChangePassword.this, "Please fill in all fields!", Toast.LENGTH_SHORT).show();
            } else if (newPassword.equals(confirmPassword)) {
                verifyOldPassword.execute(SingletonSession.getInstance().GetAccount().getEmail(), oldPassword);
            } else {
                Toast.makeText(ChangePassword.this, "New Password and Confirm Password do not match!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processPasswordChange(){
        // verified old password is correct, can update database with new password
        updateUserPassword.execute(SingletonSession.getInstance().GetAccount().getEmail(), newPassword);
    }

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {
        if (executeStatus) {
            Toast.makeText(ChangePassword.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
            //goes back to prev Page after changing password
            this.finish();

        } else {
            Toast.makeText(ChangePassword.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {
        //check that old password is correct
        if(isValidPwd){
            runOnUiThread(()->processPasswordChange());
        }else{
            Toast.makeText(ChangePassword.this, "Old password is incorrect", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {}
}