package org.peterkwan.udacity.mysupermarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import org.peterkwan.udacity.mysupermarket.R;

import butterknife.BindBool;
import butterknife.BindView;

import static org.peterkwan.udacity.mysupermarket.util.AppConstants.NOTIFICATION_ID;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.VIEW_NOTIFICATION_ACTION;

public class NotificationActivity extends BaseActivity {

    @Nullable
    @BindView(R.id.notificationDetailFragment)
    View detailFragment;

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        setTitle(R.string.notifications);
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
