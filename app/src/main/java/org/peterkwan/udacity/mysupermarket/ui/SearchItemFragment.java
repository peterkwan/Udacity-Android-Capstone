package org.peterkwan.udacity.mysupermarket.ui;


import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.peterkwan.udacity.mysupermarket.AppExecutors;
import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.entity.ShoppingCartItem;
import org.peterkwan.udacity.mysupermarket.data.entity.WishlistItem;
import org.peterkwan.udacity.mysupermarket.data.pojo.Item;
import org.peterkwan.udacity.mysupermarket.data.pojo.Store;
import org.peterkwan.udacity.mysupermarket.util.LanguageHelper;
import org.peterkwan.udacity.mysupermarket.util.LocationHelper;
import org.peterkwan.udacity.mysupermarket.viewmodel.WishListItemViewModel;
import org.peterkwan.udacity.mysupermarket.widget.MySupermarketWidgetProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

import static android.app.Activity.RESULT_OK;
import static android.appwidget.AppWidgetManager.ACTION_APPWIDGET_UPDATE;
import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.CLOSEST_STORE;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.DEFAULT_QUANTITY;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.DEFAULT_WISH_LIST_ITEM_ID;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.ITEM;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.ITEM_COLLECTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.QUANTITY;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.SEARCH_STORE_ACTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.SEARCH_TERM;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.STORE_NAME;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.WISH_LIST_ITEM_ID;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class SearchItemFragment extends BaseFragment implements StoreItemListAdapter.OnAddItemButtonClickListener {

    private static final int DIALOG_FRAGMENT = 1;
    private static final String DIALOG_FRAGMENT_TAG = "input_quantity";
    private static final String LOG_TAG = SearchItemFragment.class.getSimpleName();
    private static final int PERMISSION_REQUEST_CODE = 100;

    private Unbinder unbinder;
    private String searchTerm;
    private String storeName;
    private Integer itemId;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFireDatabase;
    private String languagePreference;
    private StoreItemListAdapter mListAdapter;
    private FusedLocationProviderClient client;
    private Context mContext;
    private Store closestStore;

    @BindView(R.id.search_item_textview)
    EditText searchItemEditText;

    @BindView(R.id.search_item_result_list_view)
    RecyclerView searchItemResultListView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        itemId = DEFAULT_WISH_LIST_ITEM_ID;
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(WISH_LIST_ITEM_ID))
                itemId = args.getInt(WISH_LIST_ITEM_ID);

            if (args.containsKey(SEARCH_TERM))
                searchTerm = args.getString(SEARCH_TERM);

            if (args.containsKey(STORE_NAME))
                storeName = args.getString(STORE_NAME);
        }


        mAuth = FirebaseAuth.getInstance();
        mFireDatabase = FirebaseDatabase.getInstance();

        View rootView = inflater.inflate(R.layout.fragment_search_item, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        mListAdapter = new StoreItemListAdapter(this);
        searchItemResultListView.setAdapter(mListAdapter);
        searchItemResultListView.setHasFixedSize(true);
        searchItemResultListView.addItemDecoration(new DividerItemDecoration(searchItemResultListView.getContext(), VERTICAL));

        mContext = getActivity().getApplicationContext();
        languagePreference = LanguageHelper.getLanguage(mContext);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

        client = LocationServices.getFusedLocationProviderClient(mContext);
        LocationHelper.requestLocationUpdates(client, new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                setNearestStore(mContext);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null)
            mAuth.signInAnonymously();

        updateUI();
    }

    @Override
    public void onButtonClicked(Item item) {
        showInputQuantityDialog(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case DIALOG_FRAGMENT:
                if (resultCode == RESULT_OK && data.getExtras() != null && data.getParcelableExtra(ITEM) != null && data.getIntExtra(QUANTITY, DEFAULT_QUANTITY) != DEFAULT_QUANTITY) {
                    final Item item = data.getParcelableExtra(ITEM);
                    int quantity = data.getIntExtra(QUANTITY, DEFAULT_QUANTITY);

                    final ShoppingCartItem shoppingCartItem = new ShoppingCartItem();
                    shoppingCartItem.setItemNameEng(item.getNameEng());
                    shoppingCartItem.setItemNameTradChi(item.getNameTradChi());
                    shoppingCartItem.setItemNameSimpChi(item.getNameSimpChi());
                    shoppingCartItem.setImagePath(item.getImageFilename());
                    shoppingCartItem.setUnitPrice(item.getUnitPrice());
                    shoppingCartItem.setQuantity(quantity);
                    shoppingCartItem.setStatus(R.string.status_in_cart);

                    AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDatabase.shoppingDao().insertShoppingCartItem(shoppingCartItem);
                            updateAppWidgets();

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    showResult(shoppingCartItem);
                                }
                            });
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            setNearestStore(mContext);
    }

    @OnClick(R.id.search_store_button)
    public void searchStore() {
        if (mCallbackListener != null)
            mCallbackListener.onCallback(SEARCH_STORE_ACTION, searchItemEditText.getText().toString());
    }

    @OnClick(R.id.search_nearby_store_button)
    public void searchItemAtNearbyStore() {
        // Search
        if (closestStore != null) {
            searchTerm = searchItemEditText.getText().toString();
            searchItemInStore(closestStore.getStoreNameEng());
        }
    }

    private void updateUI() {
        if (searchTerm == null) {
            WishListItemViewModel viewModel = ViewModelProviders.of(this).get(WishListItemViewModel.class);
            viewModel.retrieveWishListItem(itemId).observe(this, new Observer<WishlistItem>() {
                @Override
                public void onChanged(@Nullable WishlistItem wishlistItem) {
                    searchItemEditText.setText(wishlistItem == null ? "" : wishlistItem.getName());
                }
            });
        }
        else {
            searchItemEditText.setText(searchTerm);
            searchItemInStore(storeName);
        }
    }

    private void searchItemInStore(final String storeName) {
        if (storeName != null) {
            mFireDatabase.getReference(ITEM_COLLECTION)
                    .addValueEventListener(new ValueEventListener() {
                        @SuppressWarnings("unchecked")
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            List<Item> itemList = new ArrayList<>();
                            int i = 1;

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                if (snapshot.getKey().toLowerCase().replace("_", ".").contains(searchTerm.toLowerCase())
                                        && ((List<String>)snapshot.child("stores").getValue()).contains(storeName)) {
                                    Item item = new Item();
                                    item.setItemId(i);
                                    item.setNameEng(snapshot.child("name_en").getValue(String.class));
                                    item.setNameTradChi(snapshot.child("name_zh").getValue(String.class));
                                    item.setNameSimpChi(snapshot.child("name_cn").getValue(String.class));

                                    if (getString(R.string.lang_en_value).equals(languagePreference)) {
                                        item.setName(snapshot.child("name_en").getValue(String.class));
                                    }
                                    else if (getString(R.string.lang_zh_value).equals(languagePreference)) {
                                        item.setName(snapshot.child("name_zh").getValue(String.class));
                                    }
                                    else if (getString(R.string.lang_cn_value).equals(languagePreference)) {
                                        item.setName(snapshot.child("name_cn").getValue(String.class));
                                    }
                                    item.setUnitPrice(snapshot.child("unit_price").getValue(Double.class));
                                    item.setImageFilename(snapshot.child("image").getValue(String.class));

                                    itemList.add(item);
                                    i++;
                                }
                                mListAdapter.setItemList(itemList);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Log.e(LOG_TAG, "Database error", databaseError.toException());
                        }
                    });
        }
    }

    private void showInputQuantityDialog(Item item) {
        InputQuantityDialogFragment dialogFragment = new InputQuantityDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ITEM, item);
        dialogFragment.setArguments(args);
        dialogFragment.setTargetFragment(this, DIALOG_FRAGMENT);
        dialogFragment.show(getFragmentManager(), DIALOG_FRAGMENT_TAG);
    }

    private void setNearestStore(final Context context) {
        LocationHelper.findNearestStore(context, mFireDatabase, client, new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                if (msg != null) {
                    Bundle bundle = msg.getData();
                    if (bundle != null && bundle.containsKey(CLOSEST_STORE)) {
                        closestStore = bundle.getParcelable(CLOSEST_STORE);
                        if (closestStore != null)
                            Log.d(LOG_TAG, "Closest Store = " + closestStore.getStoreNameEng());
                    }
                }
            }
        });
    }

    private void updateAppWidgets() {
        Intent intent = new Intent(mContext, MySupermarketWidgetProvider.class);
        intent.setAction(ACTION_APPWIDGET_UPDATE);
        mContext.sendBroadcast(intent);
    }

    private void showResult(ShoppingCartItem shoppingCartItem) {
        String itemName = "";
        if (getString(R.string.lang_en_value).equals(languagePreference))
            itemName = shoppingCartItem.getItemNameEng();
        else if (getString(R.string.lang_zh_value).equals(languagePreference))
            itemName = shoppingCartItem.getItemNameTradChi();
        else if (getString(R.string.lang_cn_value).equals(languagePreference))
            itemName = shoppingCartItem.getItemNameSimpChi();

        Toast.makeText(mContext,
                String.format(Locale.getDefault(), "%s (%s %d) %s", itemName, getString(R.string.quantity),
                        shoppingCartItem.getQuantity(),  getString(R.string.added_to_cart)), Toast.LENGTH_SHORT).show();
    }

}
