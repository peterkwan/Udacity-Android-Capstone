package org.peterkwan.udacity.mysupermarket.viewmodel;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import org.peterkwan.udacity.mysupermarket.data.entity.ShoppingCartItem;

import java.util.List;

public class ShoppingCartViewModel extends BaseViewModel {

    private LiveData<List<ShoppingCartItem>> shoppingCartItemList;

    public ShoppingCartViewModel(final Application app) {
        super(app);
    }

    public LiveData<List<ShoppingCartItem>> retrieveShoppingCart() {
        if (shoppingCartItemList == null)
            shoppingCartItemList = shoppingDao.findAllShoppingCartItems();

        return shoppingCartItemList;
    }
}
