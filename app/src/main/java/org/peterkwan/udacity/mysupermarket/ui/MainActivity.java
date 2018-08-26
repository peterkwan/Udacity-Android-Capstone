package org.peterkwan.udacity.mysupermarket.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import org.peterkwan.udacity.mysupermarket.R;

import butterknife.BindBool;
import butterknife.ButterKnife;

import static org.peterkwan.udacity.mysupermarket.util.AppConstants.ADD_WISHLIST_ITEM_ACTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.REMOVE_FRAGMENT_ACTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.SEARCH_ITEM_ACTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.SEARCH_STORE_ACTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.SEARCH_TERM;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.SEARCH_WISHLIST_ITEM_ACTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.STORE_NAME;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.WISH_LIST_ITEM_ID;

public class MainActivity extends BaseActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @BindBool(R.bool.two_pane_layout)
    boolean isTwoPaneLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle(R.string.app_name);

        ButterKnife.bind(this);

        if (!isTwoPaneLayout && savedInstanceState == null)
            replaceFragment(R.id.mainFragment, new MainFragment());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_setting:
                startActivity(new Intent(this, SettingsActivity.class));
                return true;
            case R.id.menu_item_notifications:
                startActivity(new Intent(this, NotificationActivity.class));
                return true;
            case R.id.menu_item_shopping_history:
                startActivity(new Intent(this, ShoppingHistoryActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCallback(int actionType, Object... args) {
        switch (actionType) {
            case ADD_WISHLIST_ITEM_ACTION:
                if (isTwoPaneLayout)
                    replaceFragment(R.id.mainDetailFragment, new AddWishlistFragment());
                else
                    startActivity(new Intent(this, AddWishlistItemActivity.class));
                break;
            case SEARCH_WISHLIST_ITEM_ACTION:
                Bundle bundle = new Bundle();
                bundle.putInt(WISH_LIST_ITEM_ID, (Integer)args[0]);
                SearchItemFragment fragment = new SearchItemFragment();
                fragment.setArguments(bundle);

                if (isTwoPaneLayout)
                    replaceFragment(R.id.mainDetailFragment, fragment);
                else
                    replaceFragmentWithBackstack(R.id.mainFragment, fragment);

                break;
            case SEARCH_STORE_ACTION:
                Bundle bundle2 = new Bundle();
                bundle2.putString(SEARCH_TERM, (String)args[0]);
                SearchStoreFragment fragment2 = new SearchStoreFragment();
                fragment2.setArguments(bundle2);

                if (isTwoPaneLayout)
                    replaceFragment(R.id.mainDetailFragment, fragment2);
                else
                    replaceFragmentWithBackstack(R.id.mainFragment, fragment2);
                break;
            case SEARCH_ITEM_ACTION:
                Log.d(LOG_TAG, "Search Item Action with search term = " + args[0]);
                Bundle bundle1 = new Bundle();
                bundle1.putString(SEARCH_TERM, (String)args[0]);
                bundle1.putString(STORE_NAME, (String)args[1]);
                SearchItemFragment fragment1 = new SearchItemFragment();
                fragment1.setArguments(bundle1);

                if (isTwoPaneLayout)
                    replaceFragment(R.id.mainDetailFragment, fragment1);
                else
                    replaceFragmentWithBackstack(R.id.mainFragment, fragment1);
                break;
            case REMOVE_FRAGMENT_ACTION:
                Fragment f =  getSupportFragmentManager().findFragmentById(R.id.mainDetailFragment);
                if (f != null)
                    getSupportFragmentManager().beginTransaction()
                            .remove(f)
                            .commit();
                break;
        }
    }

}
