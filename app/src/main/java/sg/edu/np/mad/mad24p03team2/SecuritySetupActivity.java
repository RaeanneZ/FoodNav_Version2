package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.SaveSecurityInfo;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSignUp;


public class SecuritySetupActivity extends AppCompatActivity {

    private Spinner spinnerQns;
    private Button saveSecurityBtn;
    private String selectedQns= "";
    private EditText securityAns;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_security_setup);

        //this is to disable backButton
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {} //do nothing
        });

        securityAns = findViewById(R.id.securityAnswer);

        spinnerQns = findViewById(R.id.spinnerSecurityQns);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.security_qns_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Set the adapter to the Spinner
        spinnerQns.setAdapter(adapter);
        selectedQns = adapter.getItem(0).toString();    //default to first item

        spinnerQns.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedQns = adapter.getItem(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        saveSecurityBtn = findViewById(R.id.saveSecurityBtn);
        saveSecurityBtn.setOnClickListener(v -> {

            //check that all fields are filled in
            String answer = securityAns.getText().toString();
            if(selectedQns.isEmpty() || answer.isEmpty()){
                Toast.makeText(this, "Please fill in the answer", Toast.LENGTH_SHORT).show();
                return;
            }

            //Save Security setup to singleton
            SingletonSignUp.getInstance().setSecurityInfo(selectedQns, answer);

            //move to next page
            Intent login = new Intent(SecuritySetupActivity.this, ProfileActivity.class);
            startActivity(login);
            finish();

        });
    }
}