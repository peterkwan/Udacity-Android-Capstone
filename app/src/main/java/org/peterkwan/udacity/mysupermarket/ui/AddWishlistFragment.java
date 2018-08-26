package org.peterkwan.udacity.mysupermarket.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import org.peterkwan.udacity.mysupermarket.AppExecutors;
import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.entity.WishlistItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

import static org.peterkwan.udacity.mysupermarket.util.AppConstants.ADDED_WISHLIST_ITEM_ACTION;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class AddWishlistFragment extends BaseFragment {

    private Unbinder unbinder;

    @BindView(R.id.edit_wishlist_item_textview)
    EditText wishlistItemTextView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_add_wishlist, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.add_button)
    public void addButtonClicked() {
        // add item
        final WishlistItem item = new WishlistItem();
        item.setName(wishlistItemTextView.getText().toString());

        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.shoppingDao().insertWishListItem(item);

                if (mCallbackListener != null)
                    mCallbackListener.onCallback(ADDED_WISHLIST_ITEM_ACTION);
            }
        });
    }

}
