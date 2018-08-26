package org.peterkwan.udacity.mysupermarket.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.entity.ShoppingCartItem;

import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ShoppingHistoryItemListAdapter extends RecyclerView.Adapter<ShoppingHistoryItemListAdapter.ShoppingHistoryItemViewHolder> {

    private Context mContext;
    private String languagePreference;
    private List<ShoppingCartItem> itemList;

    @NonNull
    @Override
    public ShoppingHistoryItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ShoppingHistoryItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_history_item_details, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingHistoryItemViewHolder holder, int i) {
        ShoppingCartItem item = itemList.get(i);

        String itemName = item.getItemNameEng();

        if (mContext.getResources().getString(R.string.lang_en_value).equals(languagePreference)) {
            itemName = item.getItemNameEng();
        }
        else if (mContext.getResources().getString(R.string.lang_zh_value).equals(languagePreference)) {
            itemName = item.getItemNameTradChi();
        }
        else if (mContext.getResources().getString(R.string.lang_cn_value).equals(languagePreference)) {
            itemName = item.getItemNameSimpChi();
        }

        holder.itemNameView.setText(itemName);
        holder.itemQuantityView.setText(String.valueOf(item.getQuantity()));
        holder.itemPriceView.setText(String.format(Locale.getDefault(), "$%.2f", item.getQuantity() * item.getUnitPrice()));
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    class ShoppingHistoryItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_name_view)
        TextView itemNameView;

        @BindView(R.id.item_quantity_view)
        TextView itemQuantityView;

        @BindView(R.id.price_view)
        TextView itemPriceView;

        ShoppingHistoryItemViewHolder(View rootView) {
            super(rootView);

            ButterKnife.bind(this, rootView);
        }
    }
}
