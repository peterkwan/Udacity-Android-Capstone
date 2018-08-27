package org.peterkwan.udacity.mysupermarket.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.util.AppConstants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddWishlistItemActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wishlist_item);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.add_wishlist_item);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCallback(int actionType, Object... args) {
        if (actionType == AppConstants.ADDED_WISHLIST_ITEM_ACTION)
            finish();
    }
}
