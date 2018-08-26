package org.peterkwan.udacity.mysupermarket.ui;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.peterkwan.udacity.mysupermarket.AppExecutors;
import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.entity.WishlistItem;
import org.peterkwan.udacity.mysupermarket.viewmodel.WishListItemViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.ADD_WISHLIST_ITEM_ACTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.SEARCH_WISHLIST_ITEM_ACTION;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class WishlistFragment extends BaseFragment implements OnListItemClickListener {

    private Unbinder unbinder;
    private WishlistItemListAdapter mListAdapter;

    @BindView(R.id.wishlist_list_view)
    RecyclerView wishListView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_wishlist, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        mListAdapter = new WishlistItemListAdapter(this);
        wishListView.setAdapter(mListAdapter);
        wishListView.setHasFixedSize(true);
        wishListView.addItemDecoration(new DividerItemDecoration(wishListView.getContext(), VERTICAL));

        WishListItemViewModel viewModel = ViewModelProviders.of(this).get(WishListItemViewModel.class);
        viewModel.retrieveWishListItemList().observe(this, new Observer<List<WishlistItem>>() {
            @Override
            public void onChanged(@Nullable List<WishlistItem> wishlistItems) {
                mListAdapter.setWishlistItemList(wishlistItems);
            }
        });

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.LEFT);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull final RecyclerView.ViewHolder viewHolder, int i) {
                AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDatabase.shoppingDao().deleteWishListItem(mListAdapter.getWishlistItemList().get(viewHolder.getAdapterPosition()));
                    }
                });
            }
        });
        itemTouchHelper.attachToRecyclerView(wishListView);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.add_item_button)
    public void addItemClick() {
        if (mCallbackListener != null)
            mCallbackListener.onCallback(ADD_WISHLIST_ITEM_ACTION);
    }

    @Override
    public void onListItemClicked(int itemId) {
        if (mCallbackListener != null)
            mCallbackListener.onCallback(SEARCH_WISHLIST_ITEM_ACTION, itemId);
    }
}
