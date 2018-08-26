package org.peterkwan.udacity.mysupermarket.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.pojo.Store;
import org.peterkwan.udacity.mysupermarket.service.GeofenceTransitionIntentService;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.Geofence.GEOFENCE_TRANSITION_ENTER;
import static com.google.android.gms.location.GeofencingRequest.INITIAL_TRIGGER_ENTER;
import static com.google.android.gms.location.GeofencingRequest.INITIAL_TRIGGER_EXIT;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.CLOSEST_STORE;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.MESSENGER;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.STORE_COLLECTION;

public class LocationHelper {

    private static final String LOG_TAG = LocationHelper.class.getSimpleName();

    private static Store closestStore = null;

    public static void findNearestStore(final Context context, final FirebaseDatabase mFireDatabase, final FusedLocationProviderClient client, final Handler handler) throws SecurityException {
        client.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    final Location location = task.getResult();

                    mFireDatabase.getReference(STORE_COLLECTION)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                    Log.d(LOG_TAG, "Calculating min distance");
                                    double minDistance = -1.0;
                                    String languagePreference = LanguageHelper.getLanguage(context);
                                    int id = 1;

                                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                        Store store = new Store();
                                        store.setStoreId(id++);

                                        String nameEn = snapshot.child("name_en").getValue(String.class);
                                        store.setStoreNameEng(nameEn);

                                        if (context.getString(R.string.lang_en_value).equals(languagePreference)) {
                                            store.setStoreName(nameEn);
                                            store.setStoreAddress(snapshot.child("address_en").getValue(String.class));
                                        }
                                        else if (context.getString(R.string.lang_zh_value).equals(languagePreference)) {
                                            store.setStoreName(snapshot.child("name_zh").getValue(String.class));
                                            store.setStoreAddress(snapshot.child("address_zh").getValue(String.class));
                                        }
                                        else if (context.getString(R.string.lang_cn_value).equals(languagePreference)) {
                                            store.setStoreName(snapshot.child("name_cn").getValue(String.class));
                                            store.setStoreAddress(snapshot.child("address_cn").getValue(String.class));
                                        }

                                        Location storeLocation = new Location(nameEn);
                                        storeLocation.setLatitude(snapshot.child("latitude").getValue(Double.class));
                                        storeLocation.setLongitude(snapshot.child("longitude").getValue(Double.class));

                                        double distance =  storeLocation.distanceTo(location);
                                        Log.d(LOG_TAG, "Current Location = " + location.getLatitude() + ", " + location.getLongitude());
                                        Log.d(LOG_TAG, "Store Location = " + storeLocation.getLatitude() + ", " + storeLocation.getLongitude());
                                        Log.d(LOG_TAG, "Distance between store " + store.getStoreName() + " and location is = " + distance);
                                        if (minDistance == -1.0 || minDistance > distance) {
                                            minDistance = distance;
                                            closestStore = store;
                                        }
                                    }

                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable(CLOSEST_STORE, closestStore);
                                    Message message = Message.obtain();
                                    message.setData(bundle);
                                    handler.sendMessage(message);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    Log.e(LOG_TAG, "Databsae Error", databaseError.toException());
                                }
                            });
                }
            }
        });
    }

    public static void requestLocationUpdates(final FusedLocationProviderClient client, final LocationCallback callback) throws SecurityException {
        LocationRequest request = new LocationRequest();
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setInterval(10000);
        request.setFastestInterval(5000);

        client.requestLocationUpdates(request, callback, null);
    }

    public static void removeLocationUpdates(final FusedLocationProviderClient client, final LocationCallback callback) {
        client.removeLocationUpdates(callback);
    }

    public static void constructGeofences(final Context context, final Handler handler, List<Store> storeList) throws SecurityException {
        Intent intent = new Intent(context, GeofenceTransitionIntentService.class);
        intent.putExtra(MESSENGER, new Messenger(handler));
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        List<Geofence> geofenceList = new ArrayList<>();
        for (Store store : storeList) {
            Geofence geofence = new Geofence.Builder()
                    .setRequestId(String.valueOf(store.getStoreId()))
                    .setCircularRegion(store.getLatitude(), store.getLongitude(), 20)
                    .setExpirationDuration(864000)
                    .setTransitionTypes(GEOFENCE_TRANSITION_ENTER)
                    .build();
            geofenceList.add(geofence);
        }

        GeofencingRequest request = new GeofencingRequest.Builder()
                .setInitialTrigger(INITIAL_TRIGGER_ENTER | INITIAL_TRIGGER_EXIT)
                .addGeofences(geofenceList)
                .build();

        GeofencingClient client = LocationServices.getGeofencingClient(context);
        client.addGeofences(request, pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_TAG, "Geofences added");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_TAG, "Geofence add failed", e);
                    }
                });
    }

    public static void removeGeofences(final Context context) {
        Intent intent = new Intent(context, GeofenceTransitionIntentService.class);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        GeofencingClient client = LocationServices.getGeofencingClient(context);
        client.removeGeofences(pendingIntent)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(LOG_TAG, "Geofences removed");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(LOG_TAG, "Failed to remove geofences", e);
                    }
                });
    }


}
