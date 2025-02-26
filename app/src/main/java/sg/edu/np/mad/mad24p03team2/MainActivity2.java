package sg.edu.np.mad.mad24p03team2;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;


import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Calendar;

/**
 * MainActivity2
 * UI-Activity Main page to display all the different fragments after user login,
 */
public class MainActivity2 extends AppCompatActivity {



    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        // Set the content view to the specified layout
        setContentView(R.layout.activity_main2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(MainActivity2.this,
                    Manifest.permission.POST_NOTIFICATIONS) !=
                    PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity2.this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            }
        }

        //this is to disable backButton
        getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {} //do nothing
        });

        registerCallback();
        // Set default fragment to Dashboard on activity launch
        replaceFragment(new Dashboard(), "dashboard", false);
        // Get reference to the BottomNavigationView from the layout
        bottomNavigationView = findViewById(R.id.nav);
        // Set listener for bottom navigation item selection
        bottomNavigationView.setOnItemSelectedListener(menuItem -> {
            // Get the ID of the selected menu item
            int itemId = menuItem.getItemId();
            // Replace the fragment based on the selected menu item
            if (itemId == R.id.dashboard) {
                replaceFragment(new Dashboard(), "dashboard", false);
                return true;// Indicate that the event was handled
            }
            if (itemId == R.id.logfood) {
                replaceFragment(new LogFoodProduct(), "logfood", false);
                return true;// Indicate that the event was handled
            }
            if (itemId == R.id.account) {
                replaceFragment(new AccountPage(), "account", false);
                return true;// Indicate that the event was handled
            }
            if (itemId == R.id.food2Nom) {
                replaceFragment(new FoodToNom(), "food2Nom", false);
                return true;
            }
            if (itemId == R.id.nomNotion) {
                replaceFragment(new NomNotion(), "nomNotion", false);
                return true;
            }
            return false; // Indicate that the event was not handled
        });

        scheduleDailyNotification();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });


    }

    public void removeFragment(Fragment fragment) {

        FragmentManager manager = getSupportFragmentManager();

        FragmentTransaction trans = manager.beginTransaction();
        trans.remove(fragment);
        trans.commit();
        manager.popBackStack();

    }
    public void scheduleDailyNotification() {
        Context context =getApplicationContext();
        if (context == null) return;

        Intent intent = new Intent(context, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                9,
                intent,
                PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 15); // Set the time you want the notification to be triggered
        calendar.set(Calendar.MINUTE, 30);      // Set the minute
        calendar.set(Calendar.SECOND, 0);       // Optional: Set seconds to zero

        // If the set time has already passed for today, schedule for the next day
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1); // Move to the next day
            Log.d("DailyNotification", "Scheduled for next day as the time has already passed for today.");
        }

        if (alarmManager != null) {
            alarmManager.setInexactRepeating(
                    AlarmManager.RTC_WAKEUP,
                    calendar.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pendingIntent
            );
            Log.d("DailyNotification", "Daily notification scheduled.");
        } else {
            Log.e("DailyNotification", "Failed to retrieve AlarmManager service.");
        }
    }

    // Method to replace the current fragment with a new one
    public void replaceFragment(Fragment fragment, String fragName, boolean pushToStack) {
        // Get the fragment manager to handle fragment transactions
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Get current fragment
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.container2);
        // Check if the current fragment is different from the target fragment
        if (currentFragment != null && currentFragment.getClass().equals(fragment.getClass())) {
            return; // Do nothing if the current fragment is the same as the target fragment
        }
        // Begin a new fragment transaction
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Replace the current fragment in the container with the new fragment
        fragmentTransaction.replace(R.id.container2, fragment);
        // Manually adding previous fragment to history stack
        if (pushToStack) {
            // Manually adding previous fragment to history stack
            fragmentTransaction.addToBackStack(fragName);
        }
        // Commit the transaction to make the change
        fragmentTransaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==101){
            if (grantResults.length>0&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(getApplicationContext(),
                        "permission granted",Toast.LENGTH_SHORT).show();
            } else if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.POST_NOTIFICATIONS)) {
                AlertDialog.Builder builder=new AlertDialog.Builder(this);
                builder.setMessage("Permission to post notification is  required ");
                builder.setTitle("Permission Required").setCancelable(false)
                        .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri =Uri.fromParts("package",getPackageName(),null);
                                intent.setData(uri);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
            }
            else {
                RequestRunTimePermissions();
            }
        }
    }

    private void RequestRunTimePermissions(){

        if (ContextCompat.checkSelfPermission(MainActivity2.this,
                Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(getApplicationContext(),
                    "permission granted",Toast.LENGTH_SHORT).show();

        }else if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.POST_NOTIFICATIONS)){
            AlertDialog.Builder builder=new AlertDialog.Builder(this);
            builder.setMessage("Permission to post notification is  required ");
            builder.setTitle("Permission Required").setCancelable(false)
                    .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity2.this,
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS},101);
                            dialog.dismiss();
                        }
                    });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.show();
        }
        else {
            ActivityCompat.requestPermissions(MainActivity2.this,
                    new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, 101);
        }


    }



    public void registerCallback() {

        //If fragment change is invoked via backbutton
        //change in fragment should be updated on Bottom Navigation Bar
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.registerFragmentLifecycleCallbacks(new FragmentManager.FragmentLifecycleCallbacks() {
            @Override
            public void onFragmentResumed(@NonNull FragmentManager fm, @NonNull Fragment f) {
                super.onFragmentResumed(fm, f);

                int selItemID = bottomNavigationView.getSelectedItemId();
                if (f.getClass().getSimpleName().contains("Dashboard") && selItemID != R.id.dashboard) {
                    bottomNavigationView.setSelectedItemId(R.id.dashboard);
                } else if (f.getClass().getSimpleName().contains("LogFoodProduct") && selItemID != R.id.logfood) {
                    bottomNavigationView.setSelectedItemId(R.id.logfood);
                } else if (f.getClass().getSimpleName().contains("AccountPage") && selItemID != R.id.account) {
                    bottomNavigationView.setSelectedItemId(R.id.account);
                } else if (f.getClass().getSimpleName().contains("FoodToNom") && selItemID != R.id.food2Nom) {
                    bottomNavigationView.setSelectedItemId(R.id.food2Nom);
                } else if (f.getClass().getSimpleName().contains("NomNotion") && selItemID != R.id.nomNotion) {
                    bottomNavigationView.setSelectedItemId(R.id.nomNotion);
                }
            }

        }, true);
    }

}