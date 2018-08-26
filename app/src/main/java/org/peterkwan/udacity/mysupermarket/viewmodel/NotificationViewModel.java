package org.peterkwan.udacity.mysupermarket.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import org.peterkwan.udacity.mysupermarket.data.entity.Notification;

import java.util.List;

public class NotificationViewModel extends BaseViewModel {

    private LiveData<Notification> notificationLiveData;
    private LiveData<List<Notification>> notificationListViewData;

    public NotificationViewModel(final Application app) {
        super(app);
    }

    public LiveData<Notification> retrieveNotification(int notificationId) {
        if (notificationLiveData == null)
            notificationLiveData = shoppingDao.findNotificationById(notificationId);

        return notificationLiveData;
    }

    public LiveData<List<Notification>> retrieveNotificationList() {
        if (notificationListViewData == null)
            notificationListViewData = shoppingDao.findAllNotifications();

        return notificationListViewData;
    }
}
