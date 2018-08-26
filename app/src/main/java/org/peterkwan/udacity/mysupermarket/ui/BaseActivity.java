package org.peterkwan.udacity.mysupermarket.ui;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;

import org.peterkwan.udacity.mysupermarket.util.LanguageHelper;

public abstract class BaseActivity extends AppCompatActivity implements OnFragmentCallbackListener {

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(LanguageHelper.onAttach(newBase, LanguageHelper.getLanguage(newBase)));
    }

    @Override
    public void onCallback(int actionType, Object... args) {
        // Empty callback
    }

    void replaceFragment(int id, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment)
                .commit();
    }

    void replaceFragmentWithBackstack(int id, Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment)
                .addToBackStack(null)
                .commit();
    }
}
