package com.mobile.proisa.fotosvehiculoslibrary.Actividades;

import android.Manifest;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.mobile.proisa.fotosvehiculoslibrary.Clases.CameraUtils;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.Constantes;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.MetodosEstaticos;
import com.mobile.proisa.fotosvehiculoslibrary.Fragmentos.GalleryFragment;
import com.mobile.proisa.fotosvehiculoslibrary.Procesos.ProcesoDescargarImagen;
import com.mobile.proisa.fotosvehiculoslibrary.Procesos.ProcesoSubirImagenes;
import com.mobile.proisa.fotosvehiculoslibrary.Procesos.TareaAsincrona;
import com.mobile.proisa.fotosvehiculoslibrary.R;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Vehiculo;

import java.io.File;
import java.io.IOException;
import java.util.Stack;

public class AgregarFotosActivity extends AppCompatActivity implements View.OnClickListener, TareaAsincrona.OnFinishedProcess{
    private static final int REQUEST_CODE_CAMERA = 100;
    private static final int REQUEST_CODE_MEMORY = 101;
    private static final int ID_VEHICULO_IMAGENES = 1;
    private static final int ID_VEHICULO_IMAGENES_SUBIR = 2;

    private FloatingActionButton fabCamera;

    private Vehiculo mVehiculo;
    private String currentPath;
    private GalleryFragment galleryFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_fotos);

        if(savedInstanceState == null)
            mVehiculo = getVehiculo();
        else{
            if(savedInstanceState.containsKey(Constantes.VEHICULO)){
                mVehiculo = savedInstanceState.getParcelable(Constantes.VEHICULO);
            }
        }

        setTitle(Vehiculo.getShortTitle(mVehiculo));
        getSupportActionBar().setSubtitle(R.string.title_gallery);
        fabCamera = findViewById(R.id.fab_camera);
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

    private String getProvaider(){
        Intent intent = getIntent();
        try{
            Bundle extras = intent.getExtras();
            return  extras.getString("provaider");
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

    private void requestCameraPermission(){
        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,  new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_CAMERA);
        }else{
            takePicture();
        }
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

    private void takePicture(){
        Intent intent;
        try {
            intent = CameraUtils.prepareIntentCamera(new File(Constantes.TEMP_IMAGENES), true);
            currentPath = intent.getStringExtra("path");
            File f = new File(currentPath);


            Uri uri = FileProvider.getUriForFile(getApplicationContext(),getProvaider(), f);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.putExtra(MediaStore.EXTRA_OUTPUT,uri);

            if(intent.resolveActivity(getPackageManager()) != null){
                startActivityForResult(intent,Constantes.CODE_FOTO_CAMERA_ACTIVITY);
            }
        } catch (IOException e) {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    private Uri getRealUriFromGallery(Uri selectedImage){
        String[] projection = { android.provider.MediaStore.Images.Media.DATA };
        Cursor cursor;

        cursor = managedQuery(selectedImage, projection, null, null, null);

        int column_index = cursor.getColumnIndexOrThrow(projection[0]);
        cursor.moveToFirst();

        String path = cursor.getString(column_index);

        return Uri.fromFile(new File(path));
    }

    private void searchFotos(){
        downloadPhotos(mVehiculo.getFotos().size() + 1);
    }

    private void downloadPhotos(int next) {
        new ProcesoDescargarImagen(ID_VEHICULO_IMAGENES, this, this, mVehiculo,getQueryPhoto()).execute(next);
    }

    private String getQueryPhoto() {
        return "SELECT IMAGEN FROM PRBDVEHIG WHERE COD_EMPR=1 AND NO_DOC=? AND CONTADOR=?";
    }

    private void getFotosFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        if(intent.resolveActivity(getPackageManager())!= null){
            startActivityForResult(intent, Constantes.CODE_FOTO_GALLERY_ACTIVITY);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_CODE_CAMERA:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePicture();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.permiso_camara_denegado,Toast.LENGTH_SHORT).show();
                }
                break;

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
            requestCameraPermission();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constantes.CODE_FOTO_CAMERA_ACTIVITY:
                if(resultCode == RESULT_OK){
                    Uri uri = Uri.fromFile(new File(currentPath));

                    //La ruta de la imagen puede cambiar por eso obtenemos el nuevo uri
                    uri = MetodosEstaticos.compress(uri);

                    mVehiculo.getFotos().add(uri);

                    galleryFragment.refresh();

                    //Toast.makeText(getApplicationContext(),String.format(Locale.getDefault(),"%d MB now is %d Kb in %s",now, file.length() /1024, uri.getPath()),Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(),"Foto Añadida Correctamente",Toast.LENGTH_SHORT).show();
                }
                break;

            case Constantes.CODE_FOTO_GALLERY_ACTIVITY:
                if(resultCode == RESULT_OK){
                    int count = 1;

                    ClipData clipData = null;

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {

                        clipData = data.getClipData();

                        if(clipData != null){
                            count = clipData.getItemCount();

                            for(int i = 0; i < clipData.getItemCount(); i++){
                                ClipData.Item item = clipData.getItemAt(i);
                                Uri uri = getRealUriFromGallery(item.getUri());
                                //La ruta de la imagen puede cambiar por eso obtenemos el nuevo uri
                                uri = MetodosEstaticos.compress(uri);
                                mVehiculo.getFotos().add(uri);
                            }
                        }
                    }

                    if(clipData == null){
                        Uri selectedImage = data.getData();

                        selectedImage = getRealUriFromGallery(selectedImage);
                        //La ruta de la imagen puede cambiar por eso obtenemos el nuevo uri
                        selectedImage = MetodosEstaticos.compress(selectedImage);
                        mVehiculo.getFotos().add(selectedImage);
                    }

                    galleryFragment.refresh();

                    Toast.makeText(getApplicationContext(),String.format("Fotos Añadidas (%d)",count),Toast.LENGTH_SHORT).show();
                }
                break;
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

                case ID_VEHICULO_IMAGENES_SUBIR:
                    Toast.makeText(getApplicationContext(),"Fotos Guardadas Correctamente!",Toast.LENGTH_SHORT).show();
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
        getMenuInflater().inflate(R.menu.add_photos_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        if (i == R.id.action_add) {
            getFotosFromGallery();
        }else if(i == R.id.action_details){
            Intent detailsActivity = new Intent(getApplicationContext(), VehiculoDetalleActivity.class);
            detailsActivity.putExtra(Constantes.VEHICULO,mVehiculo);
            startActivity(detailsActivity);
        }else if(i == R.id.action_upload){
            if(mVehiculo.hasFotos()){
                new ProcesoSubirImagenes(ID_VEHICULO_IMAGENES_SUBIR,this,this, mVehiculo).execute();
            }
        }

        return super.onOptionsItemSelected(item);
    }
}
