package org.peterkwan.udacity.mysupermarket.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.peterkwan.udacity.mysupermarket.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingsActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCallback(int actionType, Object... args) {
        recreate();
    }
}
