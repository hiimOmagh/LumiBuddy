package de.omagh.lumibuddy.feature_recommendation;

import android.app.NotificationChannel;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import de.omagh.lumibuddy.R;

/**
 * Handles sending of care reminders to the user.
 */
public class NotificationManager {

    private static final String CHANNEL_ID = "care_reminders";
    private final Context context;

    public NotificationManager(Context context) {
        this.context = context.getApplicationContext();
        createChannel();
    }

    private void createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Care Reminders",
                    android.app.NotificationManager.IMPORTANCE_DEFAULT);
            android.app.NotificationManager nm =
                    (android.app.NotificationManager) context.getSystemService(android.app.NotificationManager.class);
            if (nm != null) {
                nm.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Shows a simple notification reminding the user to water a plant.
     */
    public void notifyWateringNeeded(String plantName, int daysSince) {
        String text = "Water " + plantName + " today – it's been " + daysSince
                + " days since last watering!";
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_eco)
                .setContentTitle("Watering reminder")
                .setContentText(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat.from(context)
                .notify(plantName.hashCode(), builder.build());
    }
}