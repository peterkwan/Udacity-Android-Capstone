package org.peterkwan.udacity.mysupermarket.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.entity.Notification;
import org.peterkwan.udacity.mysupermarket.util.DateTimeFormatter;
import org.peterkwan.udacity.mysupermarket.viewmodel.NotificationViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

import static org.peterkwan.udacity.mysupermarket.util.AppConstants.DEFAULT_NOTIFICATION_ID;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.NOTIFICATION_ID;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class NotificationDetailFragment extends Fragment {

    private Unbinder unbinder;
    private int notificationId = DEFAULT_NOTIFICATION_ID;

    @BindView(R.id.notification_detail_date_view)
    TextView notificationDateTextView;

    @BindView(R.id.notification_detail_view)
    TextView notificationTextView;

    @BindView(R.id.notification_detail_title_view)
    TextView notificationTitleTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_notification_detail, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        Bundle bundle = getArguments();
        if (bundle != null && bundle.containsKey(NOTIFICATION_ID))
            notificationId = bundle.getInt(NOTIFICATION_ID);

        NotificationViewModel viewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        viewModel.retrieveNotification(notificationId).observe(this, new Observer<Notification>() {
            @Override
            public void onChanged(@Nullable Notification notification) {
                notificationTitleTextView.setText(notification.getTitle());
                notificationTextView.setText(notification.getMessage());
                notificationDateTextView.setText(DateTimeFormatter.formatDate(notification.getNotificationDate()));
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
