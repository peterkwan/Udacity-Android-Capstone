package org.peterkwan.udacity.mysupermarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.peterkwan.udacity.mysupermarket.R;

import butterknife.BindBool;
import butterknife.BindView;
import butterknife.ButterKnife;

import static org.peterkwan.udacity.mysupermarket.util.AppConstants.NOTIFICATION_ID;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.VIEW_NOTIFICATION_ACTION;

public class NotificationActivity extends BaseActivity {

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.notifications);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCallback(int actionType, Object... args) {
        switch (actionType) {
            case VIEW_NOTIFICATION_ACTION:
                int notificationId = (Integer)args[0];

                if (isTwoPaneLayout) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(NOTIFICATION_ID, notificationId);
                    NotificationDetailFragment fragment = new NotificationDetailFragment();
                    fragment.setArguments(bundle);

                    replaceFragment(R.id.notificationDetailFragment, fragment);
                }
                else {
                    Intent intent = new Intent(this, NotificationDetailActivity.class);
                    intent.putExtra(NOTIFICATION_ID, notificationId);
                    startActivity(intent);
                }

                break;
        }
    }
}
