package org.peterkwan.udacity.mysupermarket;

import android.support.multidex.MultiDexApplication;

import com.facebook.stetho.Stetho;
import com.google.firebase.database.FirebaseDatabase;

public class MySupermarketApp extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Stetho.initializeWithDefaults(this);
    }
}
