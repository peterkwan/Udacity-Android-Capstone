package org.peterkwan.udacity.mysupermarket.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.entity.Notification;
import org.peterkwan.udacity.mysupermarket.viewmodel.NotificationViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.VIEW_NOTIFICATION_ACTION;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class NotificationFragment extends BaseFragment implements OnListItemClickListener {

    private NotificationListAdapter mListAdapter;
    private Unbinder unbinder;

    @BindView(R.id.notification_list_view)
    RecyclerView mNotificationListView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        mListAdapter = new NotificationListAdapter(this);
        mNotificationListView.setAdapter(mListAdapter);
        mNotificationListView.setHasFixedSize(true);
        mNotificationListView.addItemDecoration(new DividerItemDecoration(mNotificationListView.getContext(), VERTICAL));

        final NotificationViewModel viewModel = ViewModelProviders.of(this).get(NotificationViewModel.class);
        viewModel.retrieveNotificationList().observe(this, new Observer<List<Notification>>() {
            @Override
            public void onChanged(@Nullable List<Notification> notificationList) {
                mListAdapter.updateNotificationList(notificationList);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onListItemClicked(int itemId) {
        if (mCallbackListener != null)
            mCallbackListener.onCallback(VIEW_NOTIFICATION_ACTION, itemId);
    }
}
