package org.peterkwan.udacity.mysupermarket.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.pojo.ShoppingHistory;
import org.peterkwan.udacity.mysupermarket.util.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lombok.AllArgsConstructor;

import static org.peterkwan.udacity.mysupermarket.util.AppConstants.STORE_COLLECTION;

@AllArgsConstructor
public class ShoppingHistoryListAdapter extends RecyclerView.Adapter<ShoppingHistoryListAdapter.ShoppingHistoryViewHolder> {

    private static final String LOG_TAG = ShoppingHistoryListAdapter.class.getSimpleName();

    private Context mContext;
    private String languagePreference;
    private FirebaseDatabase mFireDatabase;

    private final List<ShoppingHistory> shoppingHistoryList = new ArrayList<>();

    @NonNull
    @Override
    public ShoppingHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new ShoppingHistoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_history_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ShoppingHistoryViewHolder holder, int i) {
        final ShoppingHistory history = shoppingHistoryList.get(i);

        holder.purchaseDateView.setText(DateTimeFormatter.formatDateTime(history.getPurchaseDate()));
        holder.totalPriceView.setText(String.format(Locale.getDefault(), "$%.2f", history.getTotalPrice()));

        ShoppingHistoryItemListAdapter mListAdapter = new ShoppingHistoryItemListAdapter(mContext, languagePreference, history.getItemList());
        holder.itemListView.setAdapter(mListAdapter);
        holder.itemListView.setHasFixedSize(true);
        holder.itemListView.setVisibility(View.GONE);
        holder.moreLessButton.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
        holder.moreLessButton.setContentDescription(mContext.getString(R.string.show_more));

        mFireDatabase.getReference(STORE_COLLECTION)
                .orderByChild("name_en")
                .equalTo(history.getStore())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            if (mContext.getResources().getString(R.string.lang_en_value).equals(languagePreference)) {
                                holder.shopNameView.setText(snapshot.child("name_en").getValue(String.class));
                            }
                            else if (mContext.getResources().getString(R.string.lang_zh_value).equals(languagePreference)) {
                                holder.shopNameView.setText(snapshot.child("name_zh").getValue(String.class));
                            }
                            else if (mContext.getResources().getString(R.string.lang_cn_value).equals(languagePreference)) {
                                holder.shopNameView.setText(snapshot.child("name_cn").getValue(String.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(LOG_TAG, "Error", databaseError.toException());
                    }
                });
    }

    @Override
    public int getItemCount() {
        return shoppingHistoryList.size();
    }

    public void setHistoryList(List<ShoppingHistory> historyList) {
        this.shoppingHistoryList.clear();
        this.shoppingHistoryList.addAll(historyList);
        notifyDataSetChanged();
    }

    class ShoppingHistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.purchase_date_view)
        TextView purchaseDateView;

        @BindView(R.id.shop_name_view)
        TextView shopNameView;

        @BindView(R.id.total_price_view)
        TextView totalPriceView;

        @BindView(R.id.shopping_history_item_detail_list_view)
        RecyclerView itemListView;

        @BindView(R.id.more_less_button)
        ImageView moreLessButton;

        ShoppingHistoryViewHolder(View rootView) {
            super(rootView);

            ButterKnife.bind(this, rootView);
        }

        @OnClick(R.id.more_less_button)
        public void showHideDetails() {
            if (itemListView.getVisibility() == View.GONE) {
               itemListView.setVisibility(View.VISIBLE);
               moreLessButton.setImageResource(R.drawable.ic_arrow_drop_up_black_24dp);
               moreLessButton.setContentDescription(mContext.getString(R.string.show_less));
            }
            else {
                itemListView.setVisibility(View.GONE);
                moreLessButton.setImageResource(R.drawable.ic_arrow_drop_down_black_24dp);
                moreLessButton.setContentDescription(mContext.getString(R.string.show_more));
            }
        }
    }
}
