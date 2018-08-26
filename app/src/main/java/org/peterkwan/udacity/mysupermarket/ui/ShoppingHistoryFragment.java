package org.peterkwan.udacity.mysupermarket.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.entity.ShoppingCartItem;
import org.peterkwan.udacity.mysupermarket.data.pojo.ShoppingHistory;
import org.peterkwan.udacity.mysupermarket.util.LanguageHelper;
import org.peterkwan.udacity.mysupermarket.util.SharedPreferenceHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

import static org.peterkwan.udacity.mysupermarket.util.AppConstants.PURCHASE_HISTORY_COLLECTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.USER_ID_PREF_KEY;

/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class ShoppingHistoryFragment extends BaseFragment {

    private static final String LOG_TAG = ShoppingHistoryFragment.class.getSimpleName();

    private Unbinder unbinder;
    private FirebaseDatabase mFireDatabase;
    private FirebaseAuth mAuth;
    private ShoppingHistoryListAdapter mListAdapter;

    @BindView(R.id.shopping_history_list_view)
    RecyclerView shoppingHistoryListView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_shopping_history, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        mFireDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        String languagePreference = LanguageHelper.getLanguage(getActivity());

        mListAdapter = new ShoppingHistoryListAdapter(getContext(), languagePreference, mFireDatabase);
        shoppingHistoryListView.setAdapter(mListAdapter);
        shoppingHistoryListView.setHasFixedSize(true);
        shoppingHistoryListView.addItemDecoration(new DividerItemDecoration(shoppingHistoryListView.getContext(), DividerItemDecoration.VERTICAL));

        return rootView;
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
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void updateUI() {
        String guid = SharedPreferenceHelper.getPreference(getContext(), USER_ID_PREF_KEY, "");
        Log.d(LOG_TAG, "User ID = " + guid);
        final int maxRecords = Integer.valueOf(SharedPreferenceHelper.getPreference(getContext(), getString(R.string.pref_recent_purchase_key), "10"));
        mFireDatabase.getReference(PURCHASE_HISTORY_COLLECTION)
                .orderByChild("user_id")
                .equalTo(guid)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressWarnings("unchecked")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<ShoppingHistory> historyList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ShoppingHistory history = new ShoppingHistory();
                            history.setPurchaseDate(new Date(snapshot.child("purchase_date").getValue(Long.class)));
                            history.setTotalPrice(snapshot.child("total_price").getValue() instanceof Double ?
                                    snapshot.child("total_price").getValue(Double.class) : snapshot.child("total_price").getValue(Long.class));
                            history.setStore(snapshot.child("store").getValue(String.class));
                            history.setItemList(constructItemList((List<Map<String, Object>>)snapshot.child("items").getValue()));
                            historyList.add(history);
                        }

                        Collections.sort(historyList, new Comparator<ShoppingHistory>() {
                            @Override
                            public int compare(ShoppingHistory o1, ShoppingHistory o2) {
                                return o2.getPurchaseDate().compareTo(o1.getPurchaseDate());
                            }
                        });

                        if (historyList.size() > maxRecords)
                            historyList = historyList.subList(0, maxRecords);

                        mListAdapter.setHistoryList(historyList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(LOG_TAG, "Database Error", databaseError.toException());
                    }
                });
    }

    private List<ShoppingCartItem> constructItemList(List<Map<String, Object>> itemMapList) {
        List<ShoppingCartItem> itemList = new ArrayList<>();

        for (Map<String, Object> itemMap : itemMapList) {
            ShoppingCartItem item = new ShoppingCartItem();
            item.setItemNameEng((String)itemMap.get("name_en"));
            item.setItemNameTradChi((String)itemMap.get("name_zh"));
            item.setItemNameSimpChi((String)itemMap.get("name_cn"));
            item.setQuantity(((Long)itemMap.get("quantity")).intValue());
            item.setUnitPrice(itemMap.get("unit_price") instanceof Double ? (Double) itemMap.get("unit_price") : (Long) itemMap.get("unit_price"));
            item.setId(((Long)itemMap.get("id")).intValue());

            itemList.add(item);
        }

        return itemList;
    }
}
