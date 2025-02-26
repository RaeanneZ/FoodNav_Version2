package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DisclaimerActivity extends AppCompatActivity {

    private Button cancelBtn;
    private Button nextBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_disclaimer);

        cancelBtn = findViewById(R.id.cancelBtn);
        cancelBtn.setOnClickListener(v -> finish());

        nextBtn = findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(v -> {
            Intent resetPswd = new Intent(DisclaimerActivity.this, SignupActivity.class);
            startActivity(resetPswd);
        });
    }
}