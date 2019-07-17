package com.mobile.proisa.agregarfotosvehiculo.Fragmentos;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.widget.Toast;

import com.mobile.proisa.agregarfotosvehiculo.PreferencesActivity;
import com.mobile.proisa.agregarfotosvehiculo.R;

public class MyPreferenceFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final int NOTIFICATION_ID = 300;

    private SharedPreferences first;
    private SharedPreferences second;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.configuraciones_generales);
        //getPreferenceManager().
    }

    @Override
    public void setPreferenceScreen(PreferenceScreen preferenceScreen) {
        super.setPreferenceScreen(preferenceScreen);

        first = getActivity().getSharedPreferences("base_de_datos", Context.MODE_PRIVATE);
        setSumarryForPreferences(first);
        second = getActivity().getSharedPreferences("base_de_datos2", Context.MODE_PRIVATE);
        setSumarryForPreferences2(second);
    }



    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        Toast.makeText(getActivity(), preferenceScreen.toString(), Toast.LENGTH_SHORT).show();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void setSumarryForPreferences(SharedPreferences sharedPreferences) {
       // Toast.makeText(getActivity(), sharedPreferences.getAll().toString() + "--- ummm", Toast.LENGTH_SHORT).show();
        setSummaryNoSpecial(PreferencesActivity.PREF_SERVER, sharedPreferences.getString(PreferencesActivity.PREF_SERVER, ""));
        setSummaryNoSpecial(PreferencesActivity.PREF_BDNAME, sharedPreferences.getString(PreferencesActivity.PREF_BDNAME, ""));
        setSummaryNoSpecial(PreferencesActivity.PREF_USER, sharedPreferences.getString(PreferencesActivity.PREF_USER, ""));

        /*Contraseña*/
        boolean isEmpty = TextUtils.isEmpty(sharedPreferences.getString(PreferencesActivity.PREF_PASSWORD, ""));
        String setOrNOt = isEmpty ? getString(R.string.noSet) : getString(R.string.set);
        setSummaryNoSpecial(PreferencesActivity.PREF_PASSWORD, setOrNOt);
        /*Fin Contraseña*/

        setSummaryNoSpecial(PreferencesActivity.PREF_PORT, String.valueOf(sharedPreferences.getInt(PreferencesActivity.PREF_PORT, 0)));
    }

    private void setSumarryForPreferences2(SharedPreferences sharedPreferences) {
        // Toast.makeText(getActivity(), sharedPreferences.getAll().toString() + "--- ummm", Toast.LENGTH_SHORT).show();
        setSummaryNoSpecial(PreferencesActivity.PREF_SERVER + "_2", sharedPreferences.getString(PreferencesActivity.PREF_SERVER+ "_2", ""));
        setSummaryNoSpecial(PreferencesActivity.PREF_BDNAME+ "_2", sharedPreferences.getString(PreferencesActivity.PREF_BDNAME+ "_2", ""));
        setSummaryNoSpecial(PreferencesActivity.PREF_USER+ "_2", sharedPreferences.getString(PreferencesActivity.PREF_USER+ "_2", ""));

        /*Contraseña*/
        boolean isEmpty = TextUtils.isEmpty(sharedPreferences.getString(PreferencesActivity.PREF_PASSWORD+ "_2", ""));
        String setOrNOt = isEmpty ? getString(R.string.noSet) : getString(R.string.set);
        setSummaryNoSpecial(PreferencesActivity.PREF_PASSWORD+ "_2", setOrNOt);
        /*Fin Contraseña*/

        setSummaryNoSpecial(PreferencesActivity.PREF_PORT+ "_2", String.valueOf(sharedPreferences.getInt(PreferencesActivity.PREF_PORT+ "_2", 0)));
    }



    private void setSummaryNoSpecial(String key, String value) {
        Preference preference = findPreference(key);
        preference.setSummary(value);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        if(key.contains("_2")){
            setSumarryForPreferences2(second);
        }else{
            setSumarryForPreferences(first);
        }


    }


    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


}