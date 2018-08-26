package org.peterkwan.udacity.mysupermarket.ui;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.pojo.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class StoreItemListAdapter extends RecyclerView.Adapter<StoreItemListAdapter.StoreItemViewHolder> {

    private final OnAddItemButtonClickListener mListener;

    private final List<Item> itemList = new ArrayList<>();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    @NonNull
    @Override
    public StoreItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new StoreItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final StoreItemViewHolder holder, int i) {
        Item item = itemList.get(i);

        holder.itemNameView.setText(item.getName());
        holder.unitPriceView.setText(String.format(Locale.getDefault(), "$%.2f", item.getUnitPrice()));
        holder.imageView.setContentDescription(item.getName());

        // Image view
        storage.getReference().child(item.getImageFilename()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get()
                        .load(uri)
                        .into(holder.imageView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<Item> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    public interface OnAddItemButtonClickListener {
        void onButtonClicked(Item item);
    }

    class StoreItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_image_view)
        ImageView imageView;

        @BindView(R.id.item_name_view)
        TextView itemNameView;

        @BindView(R.id.unit_price_view)
        TextView unitPriceView;

        StoreItemViewHolder(View rootView) {
            super(rootView);

            ButterKnife.bind(this, rootView);
        }

        @OnClick(R.id.add_item_button)
        public void onAddButtonClicked() {
            if (mListener != null)
                mListener.onButtonClicked(itemList.get(getAdapterPosition()));
        }
    }
}
