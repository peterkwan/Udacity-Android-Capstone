package org.peterkwan.udacity.mysupermarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.peterkwan.udacity.mysupermarket.R;

import butterknife.BindView;
import butterknife.ButterKnife;

import static org.peterkwan.udacity.mysupermarket.util.AppConstants.DEFAULT_NOTIFICATION_ID;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.NOTIFICATION_ID;

public class NotificationDetailActivity extends BaseActivity {

    private int notificationId;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null)
            notificationId = intent.getIntExtra(NOTIFICATION_ID, DEFAULT_NOTIFICATION_ID);

        setContentView(R.layout.activity_notification_detail);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.notifications));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null)
            initDetailFragment();
    }

    private void initDetailFragment() {
        Bundle args = new Bundle();
        args.putInt(NOTIFICATION_ID, notificationId);

        NotificationDetailFragment fragment = new NotificationDetailFragment();
        fragment.setArguments(args);

        replaceFragment(R.id.notificationDetailFragment, fragment);
    }
}
