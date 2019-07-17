package com.mobile.proisa.fotosvehiculoslibrary.Actividades;


import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.mobile.proisa.fotosvehiculoslibrary.Clases.Constantes;
import com.mobile.proisa.fotosvehiculoslibrary.Fragmentos.VehiculoDetalleFragment;
import com.mobile.proisa.fotosvehiculoslibrary.R;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Vehiculo;

public class VehiculoDetalleActivity extends AppCompatActivity {

    private Vehiculo vehiculo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehiculo_detalle);

        vehiculo = getVehiculo();


        setTitle(Vehiculo.getShortTitle(vehiculo));

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, VehiculoDetalleFragment.newInstance(vehiculo));
        fragmentTransaction.commit();
    }


    private Vehiculo getVehiculo(){
        Intent intent = getIntent();
        try{
            Bundle extras = intent.getExtras();
            return  extras.getParcelable(Constantes.VEHICULO);
        }catch (NullPointerException e){
            e.printStackTrace();
            finish();
        }
        return null;
    }
}
