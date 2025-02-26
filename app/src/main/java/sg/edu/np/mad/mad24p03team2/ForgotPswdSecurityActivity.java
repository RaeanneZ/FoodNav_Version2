package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.SecurityInfoClass;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSecurityInfoResult;

public class ForgotPswdSecurityActivity extends AppCompatActivity {

    private TextView tvSecurityQns;
    private EditText editTxtAnswer;

    private Button resetPswdBtn;
    private Button cancelBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_forgot_password_security);

        tvSecurityQns = findViewById(R.id.tvSecurityQns);

        SecurityInfoClass securityInfo = SingletonSecurityInfoResult.getInstance().getSecurityInfo();
        tvSecurityQns.setText(securityInfo.getQuestion());

        editTxtAnswer = findViewById(R.id.securityAnswer);
        resetPswdBtn = findViewById(R.id.resetPswdBtn);
        cancelBtn = findViewById(R.id.cancelBtn);

        resetPswdBtn.setOnClickListener(v -> {

            String answerString = editTxtAnswer.getText().toString();
            // Check if all fields are filled
            if (answerString.isEmpty()) {
                Toast.makeText(ForgotPswdSecurityActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            if(answerString.compareToIgnoreCase(securityInfo.getAnswer())==0) {
                Intent resetPasswordIntent = new Intent(ForgotPswdSecurityActivity.this, ResetPasswordActivity.class);
                startActivity(resetPasswordIntent);
                finish();
            } else {
                Toast.makeText(ForgotPswdSecurityActivity.this, "Wrong Answer", Toast.LENGTH_SHORT).show();
            }
        });

        cancelBtn.setOnClickListener(v -> finish());
    }
}