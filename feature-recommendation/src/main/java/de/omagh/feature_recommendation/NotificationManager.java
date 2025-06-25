package de.omagh.feature_recommendation;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;

import androidx.annotation.RequiresPermission;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import de.omagh.core_domain.model.Plant;

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
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Care Reminders",
                android.app.NotificationManager.IMPORTANCE_DEFAULT);
        android.app.NotificationManager nm =
                context.getSystemService(android.app.NotificationManager.class);
        if (nm != null) {
            nm.createNotificationChannel(channel);
        }
    }

    /**
     * Shows a simple notification reminding the user to water a plant.
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void notifyWateringNeeded(Plant plant, int daysSince) {
        String text = "Water " + plant.getName() + " today – it's been " + daysSince
                + " days since last watering!";

        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        if (intent == null) {
            intent = new Intent(); // fallback to empty intent
        }
        intent.putExtra("openPlantId", plant.getId());
        intent.putExtra("openPlantName", plant.getName());
        intent.putExtra("openPlantType", plant.getType());
        intent.putExtra("openPlantImageUri", plant.getImageUri());
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                plant.getId().hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_eco)
                .setContentTitle("Watering reminder")
                .setContentText(text)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (plant.getImageUri() != null && !plant.getImageUri().isEmpty()) {
            try {
                Uri uri = Uri.parse(plant.getImageUri());
                builder.setLargeIcon(BitmapFactory.decodeStream(
                        context.getContentResolver().openInputStream(uri)));
            } catch (Exception ignored) {
            }
        }

        NotificationManagerCompat.from(context)
                .notify(plant.getId().hashCode(), builder.build());
    }

    // Backwards compatible helper
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void notifyWateringNeeded(String plantName, int daysSince) {
        notifyWateringNeeded(new Plant("temp", plantName, "", ""), daysSince);
    }

    /**
     * Sends a notification with a light-related recommendation.
     */
    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    public void notifyLightRecommendation(Plant plant, String message) {
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(context.getPackageName());
        if (intent == null) {
            intent = new Intent(); // fallback to empty intent
        }
        intent.putExtra("openPlantId", plant.getId());
        intent.putExtra("openPlantName", plant.getName());
        intent.putExtra("openPlantType", plant.getType());
        intent.putExtra("openPlantImageUri", plant.getImageUri());
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                (plant.getId() + "light").hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_eco)
                .setContentTitle("Light recommendation")
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        if (plant.getImageUri() != null && !plant.getImageUri().isEmpty()) {
            try {
                Uri uri = Uri.parse(plant.getImageUri());
                builder.setLargeIcon(BitmapFactory.decodeStream(
                        context.getContentResolver().openInputStream(uri)));
            } catch (Exception ignored) {
            }
        }

        NotificationManagerCompat.from(context)
                .notify((plant.getId() + "light").hashCode(), builder.build());
    }
}