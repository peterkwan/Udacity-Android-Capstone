package org.peterkwan.udacity.mysupermarket.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import org.peterkwan.udacity.mysupermarket.data.ShoppingDatabase;
import org.peterkwan.udacity.mysupermarket.data.dao.ShoppingDao;

import lombok.Getter;

abstract class BaseViewModel extends AndroidViewModel {

    @Getter
    protected final ShoppingDao shoppingDao;

    BaseViewModel(final Application app) {
        super(app);
        shoppingDao = ShoppingDatabase.getInstance(app).shoppingDao();
    }
}
