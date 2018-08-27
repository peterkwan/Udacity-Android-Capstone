package org.peterkwan.udacity.mysupermarket.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import org.peterkwan.udacity.mysupermarket.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ShoppingHistoryActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_history);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.shopping_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

}
