package org.peterkwan.udacity.mysupermarket.ui;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.data.pojo.District;
import org.peterkwan.udacity.mysupermarket.data.pojo.Store;
import org.peterkwan.udacity.mysupermarket.util.LanguageHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lombok.NoArgsConstructor;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.DISTRICT_COLLECTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.SEARCH_ITEM_ACTION;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.SEARCH_TERM;
import static org.peterkwan.udacity.mysupermarket.util.AppConstants.STORE_COLLECTION;


/**
 * A simple {@link Fragment} subclass.
 */
@NoArgsConstructor
public class SearchStoreFragment extends BaseFragment implements OnListItemClickListener, OnMapReadyCallback {

    private static final String LOG_TAG = SearchStoreFragment.class.getSimpleName();

    private Unbinder unbinder;
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFireDatabase;
    private StoreListAdapter storeListAdapter;
    private final Map<String, District> districtMap = new HashMap<>();
    private final SparseArray<String> storeSparseArray = new SparseArray<>();
    private String languagePreference;
    private String searchTerm;
    private GoogleMap googleMap;

    @BindView(R.id.district_spinner)
    Spinner districtSpinner;

    @BindView(R.id.store_list_view)
    RecyclerView storeRecyclerView;

    @BindView(R.id.store_map_view)
    MapView storeMapView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle args = getArguments();
        if (args != null && args.containsKey(SEARCH_TERM))
            searchTerm = args.getString(SEARCH_TERM);

        View rootView = inflater.inflate(R.layout.fragment_search_store, container, false);

        unbinder = ButterKnife.bind(this, rootView);

        mAuth = FirebaseAuth.getInstance();
        mFireDatabase = FirebaseDatabase.getInstance();

        storeListAdapter = new StoreListAdapter(this);
        storeRecyclerView.setAdapter(storeListAdapter);
        storeRecyclerView.setHasFixedSize(true);
        storeRecyclerView.addItemDecoration(new DividerItemDecoration(storeRecyclerView.getContext(), VERTICAL));

        languagePreference = LanguageHelper.getLanguage(getActivity());

        districtSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                District selectedDistrict = districtMap.get(adapterView.getItemAtPosition(i));

                // search store
                searchStore(selectedDistrict);

                // show map
                showMap(selectedDistrict);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        storeMapView.onCreate(savedInstanceState);
        storeMapView.getMapAsync(this);

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

        storeMapView.onStart();
        updateUI();
    }

    @Override
    public void onResume() {
        super.onResume();
        storeMapView.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        storeMapView.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (storeMapView != null)
            storeMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        storeMapView.onLowMemory();
    }

    @Override
    public void onListItemClicked(int itemId) {
        searchAtStore(storeSparseArray.get(itemId));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMinZoomPreference(14.5f);
    }

    private void updateUI() {
        mFireDatabase.getReference(DISTRICT_COLLECTION)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<String> districtList = new ArrayList<>();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            String districtEn = snapshot.child("district_en").getValue(String.class);
                            District district = new District();

                            district.setName(districtEn);

                            district.setLatitude(snapshot.child("latitude").getValue(Double.class));
                            district.setLongitude(snapshot.child("longitude").getValue(Double.class));

                            if (getString(R.string.lang_en_value).equals(languagePreference)) {
                                districtMap.put(districtEn, district);
                                districtList.add(districtEn);
                            }
                            else if (getString(R.string.lang_zh_value).equals(languagePreference)) {
                                districtMap.put(snapshot.child("district_zh").getValue(String.class), district);
                                districtList.add(snapshot.child("district_zh").getValue(String.class));
                            }
                            else if (getString(R.string.lang_cn_value).equals(languagePreference)) {
                                districtMap.put(snapshot.child("district_cn").getValue(String.class), district);
                                districtList.add(snapshot.child("district_cn").getValue(String.class));
                            }

                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                                    getActivity().getApplicationContext(),
                                    android.R.layout.simple_list_item_1,
                                    districtList
                            );
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            districtSpinner.setAdapter(spinnerAdapter);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(LOG_TAG, "Database Error", databaseError.toException());
                    }
                });
    }

    private void searchStore(District district) {
        // DatabaseReference districtRef = mFirestore.collection(DISTRICT_COLLECTION).document(district.getName());

        mFireDatabase.getReference(STORE_COLLECTION)
                .orderByChild("district")
                .equalTo(district.getName())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        List<Store> storeList = new ArrayList<>();
                        int i = 1;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Store store = new Store();
                            store.setStoreId(i);

                            String nameEn = snapshot.child("name_en").getValue(String.class);
                            storeSparseArray.put(i, nameEn);

                            store.setStoreNameEng(nameEn);

                            if (getString(R.string.lang_en_value).equals(languagePreference)) {
                                store.setStoreName(nameEn);
                                store.setStoreAddress(snapshot.child("address_en").getValue(String.class));
                            }
                            else if (getString(R.string.lang_zh_value).equals(languagePreference)) {
                                store.setStoreName(snapshot.child("name_zh").getValue(String.class));
                                store.setStoreAddress(snapshot.child("address_zh").getValue(String.class));
                            }
                            else if (getString(R.string.lang_cn_value).equals(languagePreference)) {
                                store.setStoreName(snapshot.child("name_cn").getValue(String.class));
                                store.setStoreAddress(snapshot.child("address_cn").getValue(String.class));
                            }
                            store.setOpeningHours(snapshot.child("opening_hours").getValue(String.class));

                            // add store to map
                            double latitude = snapshot.child("latitude").getValue(Double.class);
                            double longitude = snapshot.child("longitude").getValue(Double.class);
                            addPointToMap(store.getStoreName(), latitude, longitude);

                            storeList.add(store);

                            i++;
                        }

                        storeListAdapter.setStoreList(storeList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e(LOG_TAG, "Database Error", databaseError.toException());
                    }
                });

    }

    private void addPointToMap(String storeName, double latitude, double longitude) {
        Log.d(LOG_TAG, "Adding Marker for " + storeName);
        googleMap.addMarker(new MarkerOptions()
            .position(new LatLng(latitude, longitude))
            .title(storeName));
    }

    private void showMap(District district) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(district.getLatitude(), district.getLongitude())));
    }

    private void searchAtStore(String storeName) {
        if (mCallbackListener != null)
            mCallbackListener.onCallback(SEARCH_ITEM_ACTION, searchTerm, storeName);
    }
}
