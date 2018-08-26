package org.peterkwan.udacity.mysupermarket.ui;


import android.Manifest;
import android.appwidget.AppWidgetManager;
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
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.peterkwan.udacity.mysupermarket.AppExecutors;
import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.entity.ShoppingCartItem;
import org.peterkwan.udacity.mysupermarket.data.pojo.Store;
import org.peterkwan.udacity.mysupermarket.util.DateTimeFormatter;
import org.peterkwan.udacity.mysupermarket.util.LanguageHelper;
import org.peterkwan.udacity.mysupermarket.util.LocationHelper;
import org.peterkwan.udacity.mysupermarket.util.NotificationHelper;
import org.peterkwan.udacity.mysupermarket.util.SharedPreferenceHelper;
import org.peterkwan.udacity.mysupermarket.viewmodel.ShoppingCartViewModel;
import org.peterkwan.udacity.mysupermarket.widget.MySupermarketWidgetProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.CHECKOUT_SUMMARY_NOTIFICATION_ID;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.MESSAGE_DATA;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.PURCHASE_HISTORY_COLLECTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.SHOPPING_CART;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.STORE_COLLECTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.USER_ID_PREF_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class ShoppingCartFragment extends BaseFragment implements ShoppingCartListAdapter.OnPickItemClickListener {

    private static final int PERMISSION_REQUEST_CODE = 100;
    private static final String LOG_TAG = ShoppingCartFragment.class.getSimpleName();

    private Unbinder unbinder;
    private ShoppingCartListAdapter mListAdapter;
    private FirebaseDatabase mFireDatabase;
    private FirebaseAuth mAuth;
    private Store store;
    private FusedLocationProviderClient client;
    private Context mContext;
    private LocationCallback mLocationCallback;
    private String languagePreference;

    @BindView(R.id.shopping_cart_list_view)
    RecyclerView shoppingCartListView;

    @Nullable
    @BindView(R.id.shop_name_view)
    TextView shopNameView;

    @BindView(R.id.checkout_button)
    Button checkoutButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        languagePreference = LanguageHelper.getLanguage(getActivity());
        mFireDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        mContext = getActivity().getApplicationContext();

        mListAdapter = new ShoppingCartListAdapter(mContext, this, languagePreference);
        shoppingCartListView.setAdapter(mListAdapter);
        shoppingCartListView.setHasFixedSize(true);
        shoppingCartListView.addItemDecoration(new DividerItemDecoration(shoppingCartListView.getContext(), VERTICAL));

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.Callback() {
            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return makeMovementFlags(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
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
                        mDatabase.shoppingDao().deleteShoppingCartItem(mListAdapter.getItemList().get(viewHolder.getAdapterPosition()));
                    }
                });
            }
        });
        itemTouchHelper.attachToRecyclerView(shoppingCartListView);

        checkoutButton.setEnabled(canCheckoutButtonEnabled());

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);

        client = LocationServices.getFusedLocationProviderClient(mContext);
        mLocationCallback = new LocationCallback();
        LocationHelper.requestLocationUpdates(client, mLocationCallback);

        constructStoreGeofences();

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();

        LocationHelper.removeLocationUpdates(client, mLocationCallback);
        LocationHelper.removeGeofences(mContext);
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
    public void onItemPicked(final boolean isPickedUp, final ShoppingCartItem item) {
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (isPickedUp)
                    item.setStatus(R.string.status_picked_up);
                else
                    item.setStatus(R.string.status_in_cart);
                mDatabase.shoppingDao().updateShoppingCartItem(item);

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        checkoutButton.setEnabled(canCheckoutButtonEnabled());
                    }
                });
            }
        });
    }

    @OnClick(R.id.checkout_button)
    public void completeCheckout() {
        // Complete checkout
        String guid = SharedPreferenceHelper.getPreference(getContext(), USER_ID_PREF_KEY, UUID.randomUUID().toString());
        SharedPreferenceHelper.savePreference(getContext(), USER_ID_PREF_KEY, guid);

        Date purchaseDate = new Date();
        Map<String, Object> itemMap = new HashMap<>();

        final List<ShoppingCartItem> shoppingCartItemList = mListAdapter.getItemList();
        final List<ShoppingCartItem> itemList = new ArrayList<>();
        for (ShoppingCartItem item : shoppingCartItemList) {
            if (item.getStatus() == R.string.status_picked_up)
                itemList.add(item);
        }

        if (!itemList.isEmpty()) {
            Log.d(LOG_TAG, "User ID = " + guid);
            itemMap.put("user_id", guid);
            itemMap.put("purchase_date", purchaseDate.getTime());
            itemMap.put("store", store.getStoreNameEng());

            List<Map<String, Object>> cartItemList = constructItemMaps(itemList);
            itemMap.put("items", cartItemList);

            final StringBuilder sb = new StringBuilder();
            double totalPrice = 0.0;
            for (Map<String, Object> cartItemMap : cartItemList) {
                sb.append(String.format(Locale.getDefault(), "%s (%s %d)\n", cartItemMap.get("name_en"), getString(R.string.quantity), cartItemMap.get("quantity")));
                totalPrice += ((Double)cartItemMap.get("unit_price")) * ((Integer)cartItemMap.get("quantity"));
            }
            itemMap.put("total_price", totalPrice);

            mFireDatabase.getReference(PURCHASE_HISTORY_COLLECTION)
                    .child(String.format("%s_%s", guid, DateTimeFormatter.formatDateTimeShort(purchaseDate)))
                    .setValue(itemMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Notification
                            NotificationHelper.showNotification(getContext(), CHECKOUT_SUMMARY_NOTIFICATION_ID, getString(R.string.checkout_summary), sb.toString());

                            AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                                @Override
                                public void run() {
                                    mDatabase.shoppingDao().clearShoppingCart();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), R.string.system_error, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateUI() {
        ShoppingCartViewModel viewModel = ViewModelProviders.of(this).get(ShoppingCartViewModel.class);
        viewModel.retrieveShoppingCart().observe(this, new Observer<List<ShoppingCartItem>>() {
            @Override
            public void onChanged(@Nullable List<ShoppingCartItem> shoppingCartItems) {
                mListAdapter.setItemList(shoppingCartItems);
                updateAppWidgets(getContext(), shoppingCartItems);
                }
        });
    }

    private List<Map<String, Object>> constructItemMaps(List<ShoppingCartItem> itemList) {
        List<Map<String, Object>> itemMapList = new ArrayList<>();
        Map<String, ShoppingCartItem> cartItemMap = new HashMap<>();

        for (ShoppingCartItem item : itemList) {
            ShoppingCartItem item1 = cartItemMap.get(item.getItemNameEng());
            if (item1 != null)
                item.setQuantity(item1.getQuantity() + item.getQuantity());
            cartItemMap.put(item.getItemNameEng(), item);
        }

        for (ShoppingCartItem item : cartItemMap.values()) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("id", item.getId());
            itemMap.put("name_en", item.getItemNameEng());
            itemMap.put("name_zh", item.getItemNameTradChi());
            itemMap.put("name_cn", item.getItemNameSimpChi());
            itemMap.put("quantity", item.getQuantity());
            itemMap.put("unit_price", item.getUnitPrice());

            itemMapList.add(itemMap);
        }

        return itemMapList;
    }

    private void updateAppWidgets(Context context, List<ShoppingCartItem> itemList) {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(SHOPPING_CART, (ArrayList<ShoppingCartItem>) itemList);

        Intent intent = new Intent(context, MySupermarketWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.putExtras(bundle);
        context.sendBroadcast(intent);
    }

    private void constructStoreGeofences() {
        mFireDatabase.getReference(STORE_COLLECTION)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final List<Store> storeList = new ArrayList<>();
                        int i = 1;

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Store store = new Store();
                            store.setStoreId(i++);
                            store.setStoreNameEng(snapshot.getKey());

                            if (getString(R.string.lang_en_value).equals(languagePreference)) {
                                store.setStoreName(snapshot.child("name_en").getValue(String.class));
                            }
                            else if (getString(R.string.lang_zh_value).equals(languagePreference)) {
                                store.setStoreName(snapshot.child("name_zh").getValue(String.class));
                            }
                            else if (getString(R.string.lang_cn_value).equals(languagePreference)) {
                                store.setStoreName(snapshot.child("name_cn").getValue(String.class));
                            }

                            store.setLatitude(snapshot.child("latitude").getValue(Double.class));
                            store.setLongitude(snapshot.child("longitude").getValue(Double.class));

                            storeList.add(store);
                        }

                        LocationHelper.constructGeofences(mContext, new Handler(Looper.getMainLooper()) {

                            @Override
                            public void handleMessage(Message msg) {
                                if (msg != null && msg.getData() != null) {
                                    String storeIdString = msg.getData().getString(MESSAGE_DATA);
                                    if (storeIdString != null && !storeIdString.isEmpty()) {
                                        try {
                                            int storeId = Integer.parseInt(storeIdString);
                                            store = storeList.get(storeId - 1);
                                            shopNameView.setText(store.getStoreName());
                                        } catch (NumberFormatException e) {
                                            Log.e(LOG_TAG, "Error parsing data", e);
                                        }
                                    }
                                }
                            }

                        }, storeList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(LOG_TAG, "Database error", databaseError.toException());
                    }
                });
    }

    private boolean canCheckoutButtonEnabled() {
        boolean hasItemCheckedOut = false;
        for (ShoppingCartItem item : mListAdapter.getItemList()) {
            if (item.getStatus() == R.string.status_picked_up) {
                hasItemCheckedOut = true;
                break;
            }
        }

        return hasItemCheckedOut && !shopNameView.getText().toString().isEmpty();
    }
}
