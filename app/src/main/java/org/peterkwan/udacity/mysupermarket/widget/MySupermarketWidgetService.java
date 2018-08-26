package org.peterkwan.udacity.mysupermarket.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

public class MySupermarketWidgetService extends RemoteViewsService {

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new MySupermarketWidgetDataProvider(this.getApplicationContext());
    }
}
