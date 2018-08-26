package org.peterkwan.udacity.mysupermarket.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import org.peterkwan.udacity.mysupermarket.R;

import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.O;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.DEFAULT_NOTIFICATION_CHANNEL_ID;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.DEFAULT_NOTIFICATION_CHANNEL_NAME;

public class NotificationHelper {

    public static void showNotification(final Context context, int notificationId, String title, String contentText) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (notificationManager != null) {
            if (SDK_INT >= O) {
                NotificationChannel notificationChannel = new NotificationChannel(DEFAULT_NOTIFICATION_CHANNEL_ID, DEFAULT_NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
                notificationChannel.setDescription(DEFAULT_NOTIFICATION_CHANNEL_NAME);
                notificationManager.createNotificationChannel(notificationChannel);
            }

            Notification notification = new NotificationCompat.Builder(context, DEFAULT_NOTIFICATION_CHANNEL_ID)
                    .setContentText(contentText)
                    .setSmallIcon(R.drawable.ic_cart)
                    .setContentTitle(title)
                    .build();

            notificationManager.notify(notificationId, notification);
        }
    }
}
