package org.peterkwan.udacity.mysupermarket.ui;

import android.os.Bundle;

import org.peterkwan.udacity.mysupermarket.R;

public class ShoppingHistoryActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_history);
        setTitle(R.string.shopping_history);
    }

}
