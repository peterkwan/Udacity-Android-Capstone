package org.peterkwan.udacity.mysupermarket.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.entity.WishlistItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class WishlistItemListAdapter extends RecyclerView.Adapter<WishlistItemListAdapter.WishlistItemViewHolder> {

    private OnListItemClickListener mListener;

    @Getter
    private final List<WishlistItem> wishlistItemList = new ArrayList<>();

    @NonNull
    @Override
    public WishlistItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new WishlistItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.wishlist_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistItemViewHolder holder, int position) {
        holder.wishlistItemTextView.setText(wishlistItemList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return wishlistItemList.size();
    }

    public void setWishlistItemList(List<WishlistItem> wishlistItemList) {
        this.wishlistItemList.clear();
        this.wishlistItemList.addAll(wishlistItemList);
        notifyDataSetChanged();
    }

    class WishlistItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.wishlist_item_view)
        TextView wishlistItemTextView;

        WishlistItemViewHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (mListener != null)
                mListener.onListItemClicked(wishlistItemList.get(getAdapterPosition()).getId());
        }
    }

}
