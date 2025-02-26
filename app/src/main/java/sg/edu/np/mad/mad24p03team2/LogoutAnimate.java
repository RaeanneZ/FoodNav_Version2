package sg.edu.np.mad.mad24p03team2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;

/*
 * LogoutAnimate
 * To display Lottie animation to send off user
 */
public class LogoutAnimate extends AppCompatActivity {

    TextView textViewMsg;
    LottieAnimationView lottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout_animate);

        textViewMsg = findViewById(R.id.message);
        textViewMsg.setText("Goodbye "+ SingletonSession.getInstance().GetAccount().getName());
        lottie = findViewById(R.id.animationView);

        textViewMsg.animate().translationY(-700).setDuration(700).setStartDelay(0);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            //moving on to next page
            Intent intent = new Intent(LogoutAnimate.this, MainActivity.class);
            startActivity(intent);
            finish();
        },3000); //delay 3 seconds
    }
}