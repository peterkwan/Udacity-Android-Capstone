package org.peterkwan.udacity.mysupermarket.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.pojo.Store;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class StoreListAdapter extends RecyclerView.Adapter<StoreListAdapter.StoreViewHolder> {

    private OnListItemClickListener mListener;

    @Getter
    private final List<Store> storeList = new ArrayList<>();

    @NonNull
    @Override
    public StoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new StoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_store_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull StoreViewHolder storeViewHolder, int i) {
        Store store = storeList.get(i);

        storeViewHolder.shopNameView.setText(store.getStoreName());
        storeViewHolder.shopAddressView.setText(store.getStoreAddress());
        storeViewHolder.openHourView.setText(store.getOpeningHours());
    }

    @Override
    public int getItemCount() {
        return storeList.size();
    }

    public void setStoreList(List<Store> storeList) {
        this.storeList.clear();
        this.storeList.addAll(storeList);
        notifyDataSetChanged();
    }

    class StoreViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.shop_name_view)
        TextView shopNameView;

        @BindView(R.id.shop_address_view)
        TextView shopAddressView;

        @BindView(R.id.open_hour_view)
        TextView openHourView;

        StoreViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (mListener != null)
                mListener.onListItemClicked(storeList.get(getAdapterPosition()).getStoreId());
        }
    }
}
