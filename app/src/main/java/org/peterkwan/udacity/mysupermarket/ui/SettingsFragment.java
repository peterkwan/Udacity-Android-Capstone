package org.peterkwan.udacity.mysupermarket.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;
import android.widget.Toast;

import org.peterkwan.udacity.mysupermarket.R;
import org.peterkwan.udacity.mysupermarket.util.SharedPreferenceHelper;

import static org.peterkwan.udacity.mysupermarket.util.AppConstants.SETTING_ACTION;

public class SettingsFragment extends PreferenceFragmentCompat implements
        Preference.OnPreferenceChangeListener, SharedPreferences.OnSharedPreferenceChangeListener {

    private OnFragmentCallbackListener mCallback;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentCallbackListener)
            mCallback = (OnFragmentCallbackListener)context;
        else
            throw new RuntimeException(context.toString() + " must implement OnFragmentCallbackListener");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.preferences);

        PreferenceScreen prefScreen = getPreferenceScreen();
        int prefCount = prefScreen.getPreferenceCount();

        for (int i = 0; i < prefCount; i++)
            setPreferenceSummary(prefScreen.getSharedPreferences(), prefScreen.getPreference(i));

        Preference numRecordsPreference = findPreference(getString(R.string.pref_recent_purchase_key));
        numRecordsPreference.setOnPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (preference != null) {
            setPreferenceSummary(sharedPreferences, preference);

            if (mCallback != null)
                mCallback.onCallback(SETTING_ACTION);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference.getKey().equals(getString(R.string.pref_recent_purchase_key))) {
            Toast errorToast = Toast.makeText(getContext(), R.string.pref_recent_purchase_validation_error, Toast.LENGTH_SHORT);

            try {
                int num = Integer.parseInt((String)newValue);
                if (num < 1 || num > 30) {
                    errorToast.show();
                    return false;
                }
            } catch (NumberFormatException e) {
                errorToast.show();
                return false;
            }
        }

        return true;
    }

    private void setPreferenceSummary(SharedPreferences sharedPreferences, Preference preference) {
        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference)preference;
            String value = SharedPreferenceHelper.getPreference(sharedPreferences, preference.getKey(), getString(R.string.lang_en_value));
            int prefIndex = listPreference.findIndexOfValue(value);
            if (prefIndex >= 0)
                listPreference.setSummary(listPreference.getEntries()[prefIndex]);
        }
        else if (preference instanceof EditTextPreference)
            preference.setSummary(SharedPreferenceHelper.getPreference(sharedPreferences, preference.getKey(), "10"));
    }
}
