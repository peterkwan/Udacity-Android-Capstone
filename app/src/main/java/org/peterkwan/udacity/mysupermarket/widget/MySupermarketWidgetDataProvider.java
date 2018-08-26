package org.peterkwan.udacity.mysupermarket.widget;

import android.content.Context;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.ShoppingDatabase;
import org.peterkwan.udacity.mysupermarket.data.entity.ShoppingCartItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lombok.AllArgsConstructor;

@AllArgsConstructor
class MySupermarketWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    private static final String LOG_TAG = MySupermarketWidgetDataProvider.class.getSimpleName();

    private Context mContext;
    private final List<ShoppingCartItem> itemList = new ArrayList<>();

    @Override
    public void onCreate() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void onDataSetChanged() {
        Log.d(LOG_TAG, "Setting Data");
        itemList.clear();
        itemList.addAll(ShoppingDatabase.getInstance(mContext).shoppingDao().findShoppingCartItemList());
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public RemoteViews getViewAt(int i) {
        if (itemList.isEmpty())
            return null;

        RemoteViews view = new RemoteViews(mContext.getPackageName(), R.layout.widget_item);

        ShoppingCartItem item = itemList.get(i);

        view.setTextViewText(R.id.item_name_view, item.getItemNameEng());
        view.setTextViewText(R.id.item_quantity_view, String.valueOf(item.getQuantity()));
        view.setTextViewText(R.id.total_price_view, String.format(Locale.getDefault(), "$%.2f", item.getQuantity() * item.getUnitPrice()));

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
