package org.peterkwan.udacity.mysupermarket.service;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.peterkwan.udacity.mysupermarket.AppExecutors;
import org.peterkwan.udacity.mysupermarket.data.ShoppingDatabase;
import org.peterkwan.udacity.mysupermarket.data.entity.Notification;

import java.util.Date;

public class NotificationService extends FirebaseMessagingService {

    private static final String LOG_TAG = NotificationService.class.getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        RemoteMessage.Notification remoteNotification = remoteMessage.getNotification();
        final Notification notification = new Notification();
        notification.setNotificationDate(new Date(remoteMessage.getSentTime()));

        if (remoteMessage.getData().size() > 0) {
            Log.d(LOG_TAG, "remoteMessage data" + remoteMessage.getData());
            notification.setTitle(remoteMessage.getData().get("title"));
            notification.setMessage(remoteMessage.getData().get("body"));
        }

        if (remoteNotification != null) {
            notification.setTitle(remoteNotification.getTitle());
            notification.setMessage(remoteNotification.getBody());
        }

        final ShoppingDatabase database = ShoppingDatabase.getInstance(getApplicationContext());

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                database.shoppingDao().insertNotification(notification);
            }
        });

    }
}
