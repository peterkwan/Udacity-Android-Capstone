package org.peterkwan.udacity.mysupermarket.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import org.peterkwan.udacity.mysupermarket.data.entity.WishlistItem;

import java.util.List;

public class WishListItemViewModel extends BaseViewModel {

    private LiveData<WishlistItem> wishListItemLiveData;
    private LiveData<List<WishlistItem>> wishListLiveData;

    public WishListItemViewModel(final Application app) {
        super(app);
    }

    public LiveData<WishlistItem> retrieveWishListItem(int itemId) {
        if (wishListItemLiveData == null)
            wishListItemLiveData = shoppingDao.findWishListItemById(itemId);

        return wishListItemLiveData;
    }

    public LiveData<List<WishlistItem>> retrieveWishListItemList() {
        if (wishListLiveData == null)
            wishListLiveData = shoppingDao.findAllWishlistItems();

        return wishListLiveData;
    }
}
