package org.peterkwan.udacity.mysupermarket.ui;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.entity.ShoppingCartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ShoppingCartListAdapter extends RecyclerView.Adapter<ShoppingCartListAdapter.ShoppingCartViewHolder> {

    private Context context;
    private OnPickItemClickListener mListener;
    private String languagePreference;

    @Getter
    private final List<ShoppingCartItem> itemList = new ArrayList<>();
    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    @NonNull
    @Override
    public ShoppingCartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ShoppingCartViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_cart_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ShoppingCartViewHolder holder, int i) {
        ShoppingCartItem item = itemList.get(i);

        String itemName = item.getItemNameEng();

        if (context.getResources().getString(R.string.lang_en_value).equals(languagePreference)) {
            itemName = item.getItemNameEng();
        }
        else if (context.getResources().getString(R.string.lang_zh_value).equals(languagePreference)) {
            itemName = item.getItemNameTradChi();
        }
        else if (context.getResources().getString(R.string.lang_cn_value).equals(languagePreference)) {
            itemName = item.getItemNameSimpChi();
        }

        holder.itemNameView.setText(itemName);
        holder.itemQuantityView.setText(String.valueOf(item.getQuantity()));
        holder.totalPriceView.setText(String.format(Locale.getDefault(), "$%.2f", item.getQuantity() * item.getUnitPrice()));
        holder.imageView.setContentDescription(itemName);
        holder.itemStatusView.setText(item.getStatus());

        if (item.getStatus() == R.string.status_picked_up)
            holder.pickButton.setText(R.string.put_down);
        else if (item.getStatus() == R.string.status_in_cart)
            holder.pickButton.setText(R.string.pick_up);

        storage.getReference().child(item.getImagePath()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

    public void setItemList(List<ShoppingCartItem> itemList) {
        this.itemList.clear();
        this.itemList.addAll(itemList);
        notifyDataSetChanged();
    }

    public interface OnPickItemClickListener {
        void onItemPicked(boolean isPickedUp, ShoppingCartItem item);
    }

    class ShoppingCartViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_image_view)
        ImageView imageView;

        @BindView(R.id.item_name_view)
        TextView itemNameView;

        @BindView(R.id.item_quantity_view)
        TextView itemQuantityView;

        @BindView(R.id.total_price_view)
        TextView totalPriceView;

        @BindView(R.id.item_status_view)
        TextView itemStatusView;

        @BindView(R.id.pick_button)
        Button pickButton;

        private boolean isPickedUp = false;

        ShoppingCartViewHolder(View rootView) {
            super(rootView);

            ButterKnife.bind(this, rootView);
        }

        @OnClick(R.id.pick_button)
        public void onItemPicked() {
            if (isPickedUp) {
                pickButton.setText(R.string.pick_up);
                isPickedUp = false;
            }
            else {
                pickButton.setText(R.string.put_down);
                isPickedUp = true;
            }

            if (mListener != null) {
                mListener.onItemPicked(isPickedUp, itemList.get(getAdapterPosition()));
            }
        }
    }
}
