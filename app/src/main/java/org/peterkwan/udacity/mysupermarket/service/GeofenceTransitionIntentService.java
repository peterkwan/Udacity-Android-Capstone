package org.peterkwan.udacity.mysupermarket.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingEvent;

import java.util.List;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.MESSAGE_DATA;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.MESSENGER;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 */
public class GeofenceTransitionIntentService extends IntentService {

    private static final String LOG_TAG = GeofenceTransitionIntentService.class.getSimpleName();

    public GeofenceTransitionIntentService() {
        super("GeofenceTransitionIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null && intent.getExtras() != null) {
            GeofencingEvent event = GeofencingEvent.fromIntent(intent);

            if (event.hasError()) {
                Log.e(LOG_TAG, "Geofence Error =" + event.getErrorCode());
                return;
            }

            int transition = event.getGeofenceTransition();
            if (transition == GEOFENCE_TRANSITION_ENTER) {
                List<Geofence> geofenceList = event.getTriggeringGeofences();

                String geofenceId = "";
                for (Geofence geofence : geofenceList) {
                    geofenceId = geofence.getRequestId();
                }

                Log.d(LOG_TAG, "Geofence ID =" + geofenceId);

                Messenger messenger = (Messenger) intent.getExtras().get(MESSENGER);
                Bundle bundle = new Bundle();
                bundle.putString(MESSAGE_DATA, geofenceId);

                Message message = Message.obtain();
                message.setData(bundle);

                try {
                    messenger.send(message);
                } catch (RemoteException e) {
                    Log.e(LOG_TAG, "Error", e);
                }
            }
        }
    }
}
