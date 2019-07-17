package com.mobile.proisa.fotosvehiculoslibrary.Actividades;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mobile.proisa.fotosvehiculoslibrary.Clases.Constantes;
import com.mobile.proisa.fotosvehiculoslibrary.Fragmentos.GalleryFragment;
import com.mobile.proisa.fotosvehiculoslibrary.Procesos.ProcesoDescargarImagen;
import com.mobile.proisa.fotosvehiculoslibrary.Procesos.TareaAsincrona;
import com.mobile.proisa.fotosvehiculoslibrary.R;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Vehiculo;

import java.util.Stack;

public class VerDocumentosActivity extends AppCompatActivity implements View.OnClickListener, TareaAsincrona.OnFinishedProcess{
    private static final int REQUEST_CODE_MEMORY = 101;
    private static final int ID_VEHICULO_IMAGENES = 1;

    private FloatingActionButton fabCamera;

    private Vehiculo mVehiculo;
    private GalleryFragment galleryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_fotos);

        if(savedInstanceState == null){
            mVehiculo = getVehiculo();
            mVehiculo.getFotos().clear();
        } else{
            if(savedInstanceState.containsKey(Constantes.VEHICULO)){
                mVehiculo = savedInstanceState.getParcelable(Constantes.VEHICULO);
            }
        }

        setTitle(Vehiculo.getShortTitle(mVehiculo));
        getSupportActionBar().setSubtitle(R.string.title_documento);
        fabCamera = findViewById(R.id.fab_camera);
        fabCamera.hide();
        fabCamera.setOnClickListener(this);

        galleryFragment = GalleryFragment.newInstance(mVehiculo.getFotos());
        setFragmenToContainer(galleryFragment);

        requestPermisosMemoria();
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

    private void setFragmenToContainer(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();
    }

    private void requestPermisosMemoria(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_MEMORY);
        }else{
            searchFotos();
        }
    }


    private void searchFotos(){
        downloadPhotos(mVehiculo.getFotos().size() + 1);
    }

    private void downloadPhotos(int next) {
        new ProcesoDescargarImagen(ID_VEHICULO_IMAGENES, this, this, mVehiculo,getQueryPhoto()).execute(next);
    }

    private String getQueryPhoto() {
        return "SELECT IMAGEN FROM PRBDVEHIDOCUM WHERE COD_EMPR=1 AND NO_DOC=? AND CONTADOR=?";
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_CODE_MEMORY:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    searchFotos();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.permiso_memoria_denegado,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();

        if (i == R.id.fab_camera) {
            //requestCameraPermission();
        }
    }

    @Override
    public void onFinishedProcess(TareaAsincrona task) {
        if(!task.hasErrors()){
            switch (task.getId()){
                case ID_VEHICULO_IMAGENES:
                    Bundle data = task.getData();
                    if(data.containsKey("foto")){
                        mVehiculo.getFotos().add((Uri) data.getParcelable("foto"));
                        downloadPhotos(data.getInt("next"));
                    }
                    galleryFragment.refresh();
                    break;
            }
        }
    }

    @Override
    public void onErrorOccurred(int id, Stack<Exception> exceptions) {
        Exception e = exceptions.pop();
        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(Constantes.VEHICULO,mVehiculo);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gallery_photos_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        if(i == R.id.action_details){
            Intent detailsActivity = new Intent(getApplicationContext(), VehiculoDetalleActivity.class);
            detailsActivity.putExtra(Constantes.VEHICULO,mVehiculo);
            startActivity(detailsActivity);
        }

        return super.onOptionsItemSelected(item);
    }}
