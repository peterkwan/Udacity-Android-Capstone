package org.peterkwan.udacity.mysupermarket.ui;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

class MainTabViewPagerAdapter extends FragmentStatePagerAdapter {

    private static final int WISHLIST_TAB = 0;
    private static final int SHOPPING_CART_TAB = 1;

    private final int numOfTabs;

    public MainTabViewPagerAdapter(FragmentManager fragmentManager, int numOfTabs) {
        super(fragmentManager);
        this.numOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case WISHLIST_TAB:
                return new WishlistFragment();
            case SHOPPING_CART_TAB:
                return new ShoppingCartFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
