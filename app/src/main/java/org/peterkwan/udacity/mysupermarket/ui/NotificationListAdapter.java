package org.peterkwan.udacity.mysupermarket.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.entity.Notification;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static org.peterkwan.udacity.mysupermarket.util.DateTimeFormatter.formatDate;

@AllArgsConstructor
public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.NotificationViewHolder> {

    private final OnListItemClickListener mListener;

    @Getter
    private final List<Notification> mNotificationList = new ArrayList<>();

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NotificationViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification notification = mNotificationList.get(position);

        holder.notificationTitleTextView.setText(notification.getTitle());
        holder.notificationTextView.setText(notification.getMessage());
        holder.notificationDateTextView.setText(formatDate(notification.getNotificationDate()));
    }

    @Override
    public int getItemCount() {
        return mNotificationList.size();
    }

    public void updateNotificationList(List<Notification> notificationList) {
        mNotificationList.clear();
        mNotificationList.addAll(notificationList);
        notifyDataSetChanged();
    }

    class NotificationViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.notification_title_view)
        TextView notificationTitleTextView;

        @BindView(R.id.notification_view)
        TextView notificationTextView;

        @BindView(R.id.notification_date_view)
        TextView notificationDateTextView;

        NotificationViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                Notification notification = mNotificationList.get(getAdapterPosition());
                mListener.onListItemClicked(notification.getId());
            }
        }
    }

}
