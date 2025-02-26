package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

/**
 * SelectionActivity
 * UI-Activity Diet Plan selection page
 */
public class SelectionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.setup_selectplan);

        //this is to disable backButton
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {} //do nothing
        });

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setOnClickListener(v -> {
            Intent intent = new Intent(SelectionActivity.this, DietConstraintSetup.class);
            startActivity(intent);
            finish();
        });
    }
}

