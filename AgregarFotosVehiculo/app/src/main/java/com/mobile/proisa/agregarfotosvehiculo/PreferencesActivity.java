package com.mobile.proisa.agregarfotosvehiculo;


import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mobile.proisa.agregarfotosvehiculo.Fragmentos.MyPreferenceFragment;

public class PreferencesActivity extends AppCompatActivity {
    public static final String PREF_SERVER = "server";
    public static final String PREF_PORT = "port";
    public static final String PREF_PASSWORD = "password";
    public static final String PREF_BDNAME = "database";
    public static final String PREF_USER = "user";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);
        setTitle("Ajustes");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setFragment();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setFragment(){
        MyPreferenceFragment fragment = new MyPreferenceFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

}
