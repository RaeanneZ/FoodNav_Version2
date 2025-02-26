package sg.edu.np.mad.mad24p03team2;

import static sg.edu.np.mad.mad24p03team2.Dashboard.Cal;
import static sg.edu.np.mad.mad24p03team2.Dashboard.CalLeft;
import static sg.edu.np.mad.mad24p03team2.Dashboard.Carb;
import static sg.edu.np.mad.mad24p03team2.Dashboard.Fat;
import static sg.edu.np.mad.mad24p03team2.Dashboard.Rcal;
import static sg.edu.np.mad.mad24p03team2.Dashboard.Rcarb;
import static sg.edu.np.mad.mad24p03team2.Dashboard.Rfat;
import static sg.edu.np.mad.mad24p03team2.Dashboard.Rsugar;
import static sg.edu.np.mad.mad24p03team2.Dashboard.sugarn;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "daily_notification_channel";
    private static final int NOTIFICATION_ID = 9;

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        // Create notification channel if necessary
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Daily Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

        // Create an intent to dismiss the notification
        Intent dismissIntent = new Intent(context, NotificationDismissedReceiver2.class);
        PendingIntent dismissPendingIntent = PendingIntent.getBroadcast(
                context,
                5,
                dismissIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE // Use FLAG_IMMUTABLE
        );

        // Create initial loading notification
        RemoteViews loadingLayout = new RemoteViews(context.getPackageName(), R.layout.notification_loading);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_notifications_active_24)
                .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                .setCustomContentView(loadingLayout)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOngoing(true)  // Keeps the notification visible until loading is complete
                .setProgress(0, 0, true)  // Indeterminate progress bar
                .addAction(R.drawable.baseline_disabled_by_default_24, "Dismiss", dismissPendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());

        // Simulate loading time (e.g., 3 seconds)
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Ensure that the progress is updated after the delay
            RemoteViews notificationLayout = new RemoteViews(context.getPackageName(), R.layout.notification_layout);
            RemoteViews notificationLayout2 = new RemoteViews(context.getPackageName(), R.layout.notification_layout2);

            // Update RemoteViews with actual progress values
            notificationLayout.setProgressBar(R.id.progressBarcarbs, Rcarb, Carb, false);
            notificationLayout.setProgressBar(R.id.progressBarSugar, Rsugar, sugarn, false);
            notificationLayout.setProgressBar(R.id.progressBarfats, Rfat, Fat, false);
            notificationLayout.setProgressBar(R.id.Cbar, Rcal, Cal, false);
            notificationLayout.setTextViewText(R.id.tvProgress, String.valueOf(CalLeft));

            // Create updated notification with dismiss button
            NotificationCompat.Builder updatedBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                    .setSmallIcon(R.drawable.baseline_notifications_active_24)
                    .setStyle(new NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(notificationLayout2)
                    .setCustomBigContentView(notificationLayout)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(false)  // Allows user interaction
                    .setAutoCancel(true)
                    .addAction(R.drawable.baseline_disabled_by_default_24, "Dismiss", dismissPendingIntent); // Add dismiss action

            // Notify with the updated content
            notificationManager.notify(NOTIFICATION_ID, updatedBuilder.build());
        }, 3000); // Delay in milliseconds
    }
}

