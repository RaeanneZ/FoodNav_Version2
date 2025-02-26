package sg.edu.np.mad.mad24p03team2;


import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import sg.edu.np.mad.mad24p03team2.Abstract_Interfaces.IDBProcessListener;
import sg.edu.np.mad.mad24p03team2.ApplicationSetUp.StartUp;
import sg.edu.np.mad.mad24p03team2.AsyncTaskExecutorService.AsyncTaskExecutorService;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.DietPlanClass;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.GetLoggedMealsByDate;
import sg.edu.np.mad.mad24p03team2.DatabaseFunctions.MealClass;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonBloodSugarResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonDietPlanResult;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonSession;
import sg.edu.np.mad.mad24p03team2.SingletonClasses.SingletonTodayMeal;

public class Dashboard extends Fragment implements IDBProcessListener {

    GetLoggedMealsByDate getLoggedMealsByDate;

    TextView title;
    TextView sugarBox;
    TextView carbsBox;
    TextView fatsBox;
    TextView calBox;

    TextView bsugarBox;
    TextView bcarbsBox;
    TextView bfatsBox;
    TextView bcalBox;

    TextView dSugarBox;
    TextView dcarbsBox;
    TextView dfatsBox;
    TextView dcalBox;

    ProgressBar carbBar;
    ProgressBar fatBar;
    ProgressBar sugarBar;
    ProgressBar cbar;
    TextView calProgressText;

    Button calBtn;
    int tmpYear = 1;
    int tmpMth = 1;
    int tmpDay = 1;
    String todayTitle = "";
    Calendar todayDate = Calendar.getInstance();

    DatePickerDialog datePickerDialog = null;
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd MMM");
    boolean setupProgressBarMax = false;

    //**Hong Rong
    public static int Carb = 0;
    public static int Fat = 0;
    public static int sugarn = 0;
    public static int Cal = 0;
    public static int CalLeft = 0;

    public static int Rcarb = 0;
    public static int Rfat = 0;
    public static int Rsugar = 0;
    public static int Rcal = 0;
    //**

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        getLoggedMealsByDate = new GetLoggedMealsByDate(requireActivity().getApplicationContext(), this);
        title = view.findViewById(R.id.tvdate);
        todayTitle = "Today, " + sdf.format(todayDate.getTime());

        Calendar tmp = SingletonSession.getInstance().getMealDate();
        title.setText(formatTitleDate(tmp.get(Calendar.YEAR), tmp.get(Calendar.MONTH), tmp.get(Calendar.DAY_OF_MONTH)));

        sugarBox = view.findViewById(R.id.tvp1);
        carbsBox = view.findViewById(R.id.tvc1);
        fatsBox = view.findViewById(R.id.tvf1);
        calBox = view.findViewById(R.id.kcalnum);

        bsugarBox = view.findViewById(R.id.tvp1_b);
        bcarbsBox = view.findViewById(R.id.tvc1_b);
        bfatsBox = view.findViewById(R.id.tvf1_b);
        bcalBox = view.findViewById(R.id.kcalnum_b);

        dSugarBox = view.findViewById(R.id.tvp1_d);
        dcarbsBox = view.findViewById(R.id.tvc1_d);
        dfatsBox = view.findViewById(R.id.tvf1_d);
        dcalBox = view.findViewById(R.id.kcalnum_d);

        calProgressText = view.findViewById(R.id.tvProgress);
        carbBar = view.findViewById(R.id.progressBarcarbs);
        fatBar = view.findViewById(R.id.progressBarfats);
        sugarBar = view.findViewById(R.id.progressBarSugar);
        cbar = view.findViewById(R.id.Cbar);


        //calendar button to switch day
        calBtn = view.findViewById(R.id.calendar_button);
        initDatePickerDialog();
        calBtn.setOnClickListener(v -> datePickerDialog.show());

        //Jovan shared button
        FloatingActionButton shareButton = view.findViewById(R.id.share_button);
        shareButton.setOnClickListener(v -> shareImage());

        return view;
    }

    private void initDatePickerDialog() {

        datePickerDialog = new DatePickerDialog(requireContext(),
                (view1, year, month, dayOfMonth) -> {

                    String sTitle = formatTitleDate(year, month, dayOfMonth);
                    if (!sTitle.isEmpty()) {
                        title.setText(sTitle);
                        datePickerDialog.getDatePicker().updateDate(year, month, dayOfMonth);

                        //save selected dates
                        SingletonSession.getInstance().setMealDate(year, month, dayOfMonth);
                        //reset prev records
                        SingletonTodayMeal.getInstance().onDestroy();
                        SingletonBloodSugarResult.getInstance().onDestroy();

                        //refresh all data by date
                        String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                        getLoggedMealsByDate.execute(date);
                    }

                }, tmpYear, tmpMth, tmpDay);

        //set the max and min date
        Calendar cal = Calendar.getInstance();
        datePickerDialog.getDatePicker().setMaxDate(cal.getTimeInMillis());

        cal.add(Calendar.DATE, -2);
        datePickerDialog.getDatePicker().setMinDate(cal.getTimeInMillis());
    }

    private String formatTitleDate(int year, int month, int dayOfMonth) {

        String newTitle = "";
        if (tmpYear == year && tmpMth == month && tmpDay == dayOfMonth) {
            newTitle = ""; //no change in date selection, dun refresh
        } else if (todayDate.get(Calendar.YEAR) == year
                && todayDate.get(Calendar.MONTH) == month && todayDate.get(Calendar.DAY_OF_MONTH) == dayOfMonth) {
            //change to TODAY's date
            newTitle = todayTitle;
        } else {

            //selected yesterday or the day before
            Calendar tmpCal = Calendar.getInstance();
            tmpCal.set(year, month, dayOfMonth);
            int week_number = tmpCal.get(Calendar.DAY_OF_WEEK);

            switch (week_number) {
                case Calendar.MONDAY:
                    newTitle += "Mon, ";
                    break;
                case Calendar.TUESDAY:
                    newTitle += "Tues, ";
                    break;
                case Calendar.WEDNESDAY:
                    newTitle += "Wed, ";
                    break;
                case Calendar.THURSDAY:
                    newTitle += "Thurs, ";
                    break;
                case Calendar.FRIDAY:
                    newTitle += "Fri, ";
                    break;
                case Calendar.SATURDAY:
                    newTitle += "Sat, ";
                    break;
                case Calendar.SUNDAY:
                    newTitle += "Sun, ";
                    break;
                default:
                    newTitle += "Today, ";
                    break;
            }

            newTitle += sdf.format(tmpCal.getTime());
        }

        //update
        tmpYear = year;
        tmpMth = month;
        tmpDay = dayOfMonth;

        return newTitle;

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d("Dashboard", "Pause");

    }

    @Override
    public void onResume() {
        super.onResume();
        final StartUp app = (StartUp)requireActivity().getApplicationContext();
        Connection dbCon = app.getConnection();
        try {
            if(dbCon.isClosed())
                Log.d("Dashboard","onResume - Database closed!");
        } catch (SQLException e) {
            Log.d("Dashboard","Error testing dbCon = "+e.getMessage());
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        final StartUp app = (StartUp)requireActivity().getApplicationContext();
        Connection dbCon = app.getConnection();
        try {
            if(dbCon.isClosed())
                Log.d("Dashboard","onStart - Database closed!");
        } catch (SQLException e) {
            Log.d("Dashboard","onStart Error testing dbCon = "+e.getMessage());
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d("Dashboard", "OnStop");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d("Dashboard", "onDestroy");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d("Dashboard", "onDestroyView");
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);

        Log.d("Dashboard", "OnViewStateRestored");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*Diet plan doesnt change, hence no need to reset progress bar limits when date change*/

        //to ensure max setup only done once when view is created
        if (!setupProgressBarMax) {
            DietPlanClass dietPlan = SingletonDietPlanResult.getInstance().getDietPlan();
            if (dietPlan != null) {
                carbBar.setMax(dietPlan.getReccCarbIntake() + 10);
                fatBar.setMax(dietPlan.getReccFatsIntake() + 10);
                sugarBar.setMax(dietPlan.getReccSugarIntake() + 10);

                int calories = dietPlan.getReccCaloriesIntake();
                cbar.setMax(calories);
                calProgressText.setText(String.valueOf(calories));

                //setupcomplete
                setupProgressBarMax = true;

            } else {
                //hardcode value if Model is not ready
                carbBar.setMax(200);
                fatBar.setMax(200);
                sugarBar.setMax(200);
                cbar.setMax(1550);
            }
        }

        //Hong Rong : init Notication variable
        Rcal = cbar.getMax();
        Rcarb = carbBar.getMax();
        Rsugar = sugarBar.getMax();
        Rfat = fatBar.getMax();

        refreshUI();
    }

    private void refreshUI() {
        updateBreakfastCard(SingletonTodayMeal.getInstance().GetMeal("Breakfast"));
        updateLunchCard(SingletonTodayMeal.getInstance().GetMeal("Lunch"));
        updateDinnerCard(SingletonTodayMeal.getInstance().GetMeal("Dinner"));
        updateTodayMacros();
    }

    private void updateBreakfastCard(MealClass meal) {
        MealMacros mMacros = GlobalUtil.getMealTotalMacros(meal);
        bsugarBox.setText(String.format("%.1f", mMacros.gettSugar()));
        bcarbsBox.setText(String.format("%.1f", mMacros.gettCarbs()));
        bfatsBox.setText(String.format("%.1f", mMacros.gettFats()));
        bcalBox.setText(String.format("%.1f", mMacros.gettCalories()));
    }

    private void updateLunchCard(MealClass meal) {
        MealMacros mMacros = GlobalUtil.getMealTotalMacros(meal);
        sugarBox.setText(String.format("%.1f", mMacros.gettSugar()));
        carbsBox.setText(String.format("%.1f", mMacros.gettCarbs()));
        fatsBox.setText(String.format("%.1f", mMacros.gettFats()));
        calBox.setText(String.format("%.1f", mMacros.gettCalories()));
    }

    private void updateDinnerCard(MealClass meal) {
        MealMacros mMacros = GlobalUtil.getMealTotalMacros(meal);
        dSugarBox.setText(String.format("%.1f", mMacros.gettSugar()));
        dcarbsBox.setText(String.format("%.1f", mMacros.gettCarbs()));
        dfatsBox.setText(String.format("%.1f", mMacros.gettFats()));
        dcalBox.setText(String.format("%.1f", mMacros.gettCalories()));
    }

    private void updateTodayMacros() {
        //calculate total of 3 meals macros for the day
        float tSugar = Float.parseFloat(sugarBox.getText().toString()) +
                Float.parseFloat(bsugarBox.getText().toString()) +
                Float.parseFloat(dSugarBox.getText().toString());

        float tFats = Float.parseFloat(fatsBox.getText().toString()) +
                Float.parseFloat(bfatsBox.getText().toString()) +
                Float.parseFloat(dfatsBox.getText().toString());

        float tCarbs = Float.parseFloat(carbsBox.getText().toString()) +
                Float.parseFloat(bcarbsBox.getText().toString()) +
                Float.parseFloat(dcarbsBox.getText().toString());

        float tCal = Float.parseFloat(calBox.getText().toString()) +
                Float.parseFloat(bcalBox.getText().toString()) +
                Float.parseFloat(dcalBox.getText().toString());

        carbBar.setProgress((int) tCarbs);
        fatBar.setProgress((int) tFats);
        sugarBar.setProgress((int) tSugar);

        cbar.setProgress((int) tCal);
        int calLeft = cbar.getMax() - (int) tCal;
        calProgressText.setText(String.valueOf(calLeft));

        //** HongRong
        Carb = (int) tCarbs;
        Fat = (int) tFats;
        sugarn = (int) tSugar;
        Cal = (int) tCal;

        CalLeft = (int) (Rcal - tCal);

        //hongrong
        double c = Rcarb * 0.75;
        double s = Rsugar * 0.75;
        double f = Rfat * 0.75;
        double kcal = Rcal * 0.75;
        if (Carb >= c || sugarn >= s || Fat >= f || Cal >= kcal) {
            makeNotification();
        }
        //**
    }

    //Jovan
    private void shareImage() {
        View view = getView().findViewById(R.id.constraintLayout); // Adjust this to the correct view id
        if (view != null) {
            Bitmap bitmap = getBitmapFromView(view);

            // Save bitmap to file
            File file = new File(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image.png");
            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                out.flush();

                // Get the URI of the image
                Uri uri = FileProvider.getUriForFile(requireActivity(), "sg.edu.np.mad.mad24p03team2.provider", file);

                // Create the share intent
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("image/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                // Ensure Instagram or other apps can read the file
                List<ResolveInfo> resInfoList = requireContext().getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
                for (ResolveInfo resolveInfo : resInfoList) {
                    String packageName = resolveInfo.activityInfo.packageName;
                    requireContext().grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
                }

                try {
                    shareIntent.setPackage("com.instagram.android");
                    shareIntent.putExtra("Instagram_story", uri);
                    startActivity(shareIntent);
                } catch (ActivityNotFoundException actNotFound) {
                    Toast.makeText(getContext(), "Instagram app is not available", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Failed to share image", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getContext(), "View not found", Toast.LENGTH_SHORT).show();
        }
    }

    //Jovan
    private Bitmap getBitmapFromView(View view) {
        // Ensure the view has the correct background color
        view.setBackgroundColor(getResources().getColor(android.R.color.white));
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //** HongRong
    public void makeNotification() {

        String channelID = "CHANNEL_ID_NOTIFICATION";

        NotificationManager notificationManager = (NotificationManager) requireContext().getSystemService(Context.NOTIFICATION_SERVICE);

        // Create notification channel if necessary
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(channelID);
        if (notificationChannel == null) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            notificationChannel = new NotificationChannel(channelID, "Channel Name", importance);
            notificationChannel.setLightColor(android.R.color.darker_gray);
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent intent = new Intent(getContext(), PopupActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP); // Ensure activity is not recreated
        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_MUTABLE);

        // update the notification
        NotificationCompat.Builder updatedBuilder = new NotificationCompat.Builder(requireContext(), channelID)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setContentTitle("Warning")
                .setContentText("You have used over half of your daily limit.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setProgress(0, 0, false)  // Remove the progress bar
                .setOngoing(false)  // Allow user interaction
                .setContentIntent(pendingIntent)  // Set the same PendingIntent
                .setAutoCancel(true);  // Dismiss the notification when clicked

        // Create an Intent for the dismiss action
        Intent dismissIntent = new Intent(getContext(), NotificationDismissedReceiver.class);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(getContext(),
                1, dismissIntent, PendingIntent.FLAG_MUTABLE);

        // Add the dismiss action to the updated notification
        updatedBuilder.addAction(R.drawable.baseline_disabled_by_default_24, "Dismiss", dismissPendingIntent);

        // Notify with the updated content
        notificationManager.notify(0, updatedBuilder.build());

    }

    @Override
    public void afterProcess(Boolean executeStatus, String msg, Class<? extends AsyncTaskExecutorService> returnClass) {
        if (executeStatus) {
            refreshUI();
        } else {
            // Handle failure
            Log.d("Dashboard::afterProcess", "Fail to load meal details");
        }
    }

    @Override
    public void afterProcess(Boolean executeStatus, Class<? extends AsyncTaskExecutorService> returnClass) {
    }

    @Override
    public void afterProcess(Boolean isValidUser, Boolean isValidPwd) {
    }
}
