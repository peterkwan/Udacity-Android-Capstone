package org.peterkwan.udacity.mysupermarket.ui;

import android.content.Intent;
import android.os.Bundle;

import org.peterkwan.udacity.mysupermarket.R;

import static org.peterkwan.udacity.mysupermarket.util.AppConstants.DEFAULT_NOTIFICATION_ID;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.NOTIFICATION_ID;

public class NotificationDetailActivity extends BaseActivity {

    private int notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (intent != null && intent.getExtras() != null)
            notificationId = intent.getIntExtra(NOTIFICATION_ID, DEFAULT_NOTIFICATION_ID);

        setContentView(R.layout.activity_notification_detail);
        setTitle(getString(R.string.notifications));

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
