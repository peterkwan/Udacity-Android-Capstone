package org.peterkwan.udacity.mysupermarket.ui;

import android.os.Bundle;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.util.AppConstants;

public class AddWishlistItemActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wishlist_item);
        setTitle(R.string.add_wishlist_item);
    }

    @Override
    public void onCallback(int actionType, Object... args) {
        if (actionType == AppConstants.ADDED_WISHLIST_ITEM_ACTION)
            finish();
    }
}
