package org.peterkwan.udacity.mysupermarket.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import org.peterkwan.udacity.mysupermarket.data.ShoppingDatabase;

public abstract class BaseFragment extends Fragment {

    ShoppingDatabase mDatabase;
    OnFragmentCallbackListener mCallbackListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDatabase = ShoppingDatabase.getInstance(getActivity() == null ? null : getActivity().getApplicationContext());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnFragmentCallbackListener)
            mCallbackListener = (OnFragmentCallbackListener) context;
        else
            throw new RuntimeException(context.toString() + " must implement OnFragmentCallbackListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbackListener = null;
    }
}
