package org.peterkwan.udacity.mysupermarket.ui;

import android.os.Bundle;

import org.peterkwan.udacity.mysupermarket.R;

public class SettingsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings);
    }

    @Override
    public void onCallback(int actionType, Object... args) {
        recreate();
    }
}
