package com.mobile.proisa.agregarfotosvehiculo;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.mobile.proisa.agregarfotosvehiculo.Actividades.ConfigurationActivity2;
import com.mobile.proisa.fotosvehiculoslibrary.Actividades.AgregarFotosActivity;
import com.mobile.proisa.fotosvehiculoslibrary.Actividades.ConfigurationActivity;
import com.mobile.proisa.fotosvehiculoslibrary.Actividades.VehiculoDetalleActivity;
import com.mobile.proisa.fotosvehiculoslibrary.Adaptadores.VehiculoViewAdapter;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.Constantes;
import com.mobile.proisa.fotosvehiculoslibrary.Fragmentos.FiltroFragment;
import com.mobile.proisa.fotosvehiculoslibrary.Fragmentos.VehiculoListFragment;
import com.mobile.proisa.fotosvehiculoslibrary.Interfaces.OnTapListener;
import com.mobile.proisa.fotosvehiculoslibrary.Procesos.ProcesoBuscarVehiculos;
import com.mobile.proisa.fotosvehiculoslibrary.Procesos.ProcesoFiltroDatos;
import com.mobile.proisa.fotosvehiculoslibrary.Procesos.ProcesoFotoPrincipal;
import com.mobile.proisa.fotosvehiculoslibrary.Procesos.TareaAsincrona;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Marca;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Tipo;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Vehiculo;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;

import static com.mobile.proisa.fotosvehiculoslibrary.R.string;

public class MainActivity extends AppCompatActivity implements  VehiculoViewAdapter.OnItemClickListener,
        OnTapListener<Vehiculo>, TareaAsincrona.OnFinishedProcess, FiltroFragment.SearchListener {
    private static final int ID_VEHICULOS_BUSQUEDA = 1;
    private static final int REQUEST_CODE_MEMORY = 100;
    private static final int ID_VEHICULOS_FOTOS = 2;
    private static final int ID_FILTRO_DATOS = 3;

    private ArrayList<Vehiculo> vehiculos;
    private ProcesoFotoPrincipal fotoPrincipalProcess;
    private String lastFilter;

    private int lastFoto;
    private VehiculoListFragment vehiculosListFragment;
    private FiltroFragment filtroFragment;

    public int busquedas = 0;

    private boolean mDatabaseSelected = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(R.string.vehiculos_disponibles);

        lastFilter = "";

        if(!mDatabaseSelected)
            showDialogToChooseDatabase();
        else
            onDbChoosed();
    }

    private void showDialogToChooseDatabase() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Seleccione la empresa a trabajar");
        builder.setCancelable(false);
        builder.setSingleChoiceItems(new String[]{"Empresa 1", "Empresa 2"}, -1, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case 0:
                        saveChoosedDatabase(Constantes.PREFERENCES_DATA_BASE);
                        break;

                    case 1:
                        saveChoosedDatabase(Constantes.PREFERENCES_DATA_BASE_2);
                        break;
                }

                mDatabaseSelected = true;
                onDbChoosed();
                dialog.dismiss();
            }
        });



        builder.create().show();
    }

    private void onDbChoosed() {
        buscarDatosFiltro();

        setFilterFragment(FiltroFragment.newInstance(this));
    }

    private void saveChoosedDatabase(String dbpref) {
        Log.d("dbPref", dbpref);
        SharedPreferences preferences = getSharedPreferences(Constantes.PREF_CURR_DB, MODE_PRIVATE);
        preferences.edit().putString(Constantes.PREF_CURR_DB_KEY, dbpref).commit();
    }

    private void buscarVehiculos(String filter){
        lastFilter = filter;
        new ProcesoBuscarVehiculos(ID_VEHICULOS_BUSQUEDA,this,this).execute(getQuery().concat(filter));
        Log.i("querySearch",getQuery().concat(filter));
        Toast.makeText(getApplicationContext(),"Buscando Vehículos...",Toast.LENGTH_SHORT).show();
    }

    private void buscarDatosFiltro(){
        new ProcesoFiltroDatos(ID_FILTRO_DATOS,this,this).execute();
    }

    public void showFiltro(){
        android.support.v4.widget.DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if(!drawer.isDrawerOpen(GravityCompat.END)){
            drawer.openDrawer(GravityCompat.END);
        }
    }
    public static String getQuery() {
        String query =       "SELECT VE_CODIGO, TI.TI_CODIGO,  TI.TI_DESCRI, MA.MA_CODIGO, MA.MA_DESCRI,\n";
        query = query.concat("MO.MO_CODIGO, MO.MO_DESCRI,  CO.CO_DESCRI, COM.COM_CODIGO,VEHI.VE_ANO,VE_PREMAY, VE_PREMIN, VE_PREVEN,\n");
        query = query.concat("COM.COM_DESCRI,TRN.TRN_CODIGO, TRN.TRN_DESCRI, TRC.TRC_CODIGO,TRC.TRC_DESCRI, VE_ESTADO,VE_NUMCHA, VE_PLACA\n");
        query = query.concat("FROM PRBDVEHI AS VEHI\n");
        query = query.concat("LEFT JOIN pvbdmodelo   MO  ON VEHI.COD_EMPR = MO.COD_EMPR  AND VEHI.VE_MODELO  = MO.MO_CODIGO\n");
        query = query.concat("LEFT JOIN pvbdmarca    MA  ON VEHI.COD_EMPR = MA.COD_EMPR  AND MO.MA_CODIGO    = MA.MA_CODIGO\n");
        query = query.concat("LEFT JOIN pvbdcolor    CO  ON VEHI.COD_EMPR = CO.COD_EMPR  AND VEHI.VE_COLOR   = CO.CO_CODIGO\n");
        query = query.concat("LEFT JOIN PVBDTIPO     TI  ON VEHI.COD_EMPR = TI.COD_EMPR  AND VEHI.VE_CODTIP  = TI.TI_CODIGO\n");
        query = query.concat("LEFT JOIN PVBDCOMBUS   COM ON VEHI.COD_EMPR = COM.COD_EMPR AND VEHI.COM_CODIGO = COM.COM_CODIGO\n");
        query = query.concat("LEFT JOIN PVBDTRANSMI  TRN ON VEHI.COD_EMPR = TRN.COD_EMPR AND VEHI.TRN_CODIGO = TRN.TRN_CODIGO\n");
        query = query.concat("LEFT JOIN PVBDTRACCION TRC ON VEHI.COD_EMPR = TRC.COD_EMPR AND VEHI.TRC_CODIGO = TRC.TRC_CODIGO\n");
        query = query.concat("WHERE VEHI.COD_EMPR=1 AND VE_ESTATUS='A' ");

        return query;
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

    private void searchFotos() {
        downloadPhoto(0);
    }

    private void downloadPhoto(int index){
        if(index < vehiculos.size()){
            fotoPrincipalProcess = new ProcesoFotoPrincipal(ID_VEHICULOS_FOTOS, this, this);
            fotoPrincipalProcess.execute(vehiculos.get(index).getId());
            lastFoto = index;
        }
    }

    private void deleteFotos(){
        File file = new File(Constantes.TEMP_IMAGENES);
        File[] archivos;

        if(file != null){
            archivos = file.listFiles();

            for(File f : archivos){
                String name = f.getName();

                try{
                    if(f.delete()){
                        Log.i("fotosBorrado","delete: "+name);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
    }


    public void setFilterFragment(Fragment filterFragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        vehiculosListFragment = VehiculoListFragment.newInstance(vehiculos);

        filtroFragment = (FiltroFragment) filterFragment;
        fragmentTransaction.replace(R.id.filter_container, filtroFragment);

        fragmentTransaction.commit();
    }

    public void setVehiculosListFragment(ArrayList<Vehiculo> vehiculos){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        vehiculosListFragment = VehiculoListFragment.newInstance(vehiculos);
        fragmentTransaction.replace(R.id.container, vehiculosListFragment);
        fragmentTransaction.commit();

    }
    @Override
    public void onItemClick(Vehiculo v, int posicion) {
        Intent intent = new Intent(getApplicationContext(), AgregarFotosActivity.class);
        intent.putExtra(Constantes.VEHICULO, v);
        intent.putExtra("provaider",BuildConfig.APPLICATION_ID.concat(".provider"));

        startActivity(intent);
    }

    @Override
    public void OnTapItem(Vehiculo item) {
        Intent detailsActivity = new Intent(getApplicationContext(), VehiculoDetalleActivity.class);
        detailsActivity.putExtra(Constantes.VEHICULO,item);
        startActivity(detailsActivity);
    }

    @Override
    public void onFinishedProcess(TareaAsincrona task) {
        if(!task.hasErrors()){
            switch (task.getId()){
                case ID_VEHICULOS_BUSQUEDA:
                    Bundle data = task.getData();
                    vehiculos = data.getParcelableArrayList(Constantes.VEHICULO_LIST);

                    setVehiculosListFragment(vehiculos);

                    if(vehiculos.size() > 0)
                        requestPermisosMemoria();
                    else
                        Toast.makeText(getApplicationContext(),"No se encontraron Vehículos",Toast.LENGTH_SHORT).show();
                    break;

                case ID_VEHICULOS_FOTOS:
                    if(task.getData().containsKey("foto")){
                        Uri uri = (Uri) task.getData().getParcelable("foto");
                        vehiculos.get(lastFoto).getFotos().clear();
                        vehiculos.get(lastFoto).getFotos().add(uri);
                        downloadPhoto(lastFoto + 1);
                    }

                    if(vehiculosListFragment != null){
                        vehiculosListFragment.refresh();
                    }
                    break;

                case ID_FILTRO_DATOS:
                    ArrayList<Tipo> tipos = (ArrayList<Tipo>) task.getData().get("tipos");
                    ArrayList<Marca> marcas = (ArrayList<Marca>) task.getData().get("marcas");


                    filtroFragment.setTipos(tipos);
                    filtroFragment.setMarcas(marcas);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case REQUEST_CODE_MEMORY:
                if (grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    searchFotos();
                } else {
                    Toast.makeText(getApplicationContext(), string.permiso_memoria_denegado,Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(isFinishing()){
            deleteFotos();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(com.mobile.proisa.fotosvehiculoslibrary.R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent intent = null;

        switch (id){
            case com.mobile.proisa.fotosvehiculoslibrary.R.id.action_settings:
               intent = new Intent(getApplicationContext(), ConfigurationActivity.class);
                startActivityForResult(intent,Constantes.CODE_AJUSTES_ACTIVITY);
                return true;

            case com.mobile.proisa.fotosvehiculoslibrary.R.id.action_settings_2:
               intent = new Intent(getApplicationContext(), ConfigurationActivity2.class);
                startActivityForResult(intent,Constantes.CODE_AJUSTES_ACTIVITY);
                return true;

            case com.mobile.proisa.fotosvehiculoslibrary.R.id.action_filter:
                showFiltro();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
         android.support.v4.widget.DrawerLayout drawer = findViewById(R.id.drawer_layout);

         if(drawer.isDrawerOpen(GravityCompat.END)){
             drawer.closeDrawer(GravityCompat.END);
         }else{
             super.onBackPressed();
         }
    }

    @Override
    public void onSerch(CharSequence searchText) {
        Log.i("filterSearch",searchText.toString().concat(String.valueOf(busquedas++)));
        buscarVehiculos(searchText.toString());

        android.support.v4.widget.DrawerLayout drawer = findViewById(R.id.drawer_layout);

        if(drawer.isDrawerOpen(GravityCompat.END)){
            drawer.closeDrawer(GravityCompat.END);
        }

        if(fotoPrincipalProcess != null){
            if(fotoPrincipalProcess.getStatus() == AsyncTask.Status.RUNNING){
                fotoPrincipalProcess.cancel(true);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case Constantes.CODE_AJUSTES_ACTIVITY:
                if(resultCode == RESULT_OK){
                    if(!lastFilter.equals(""))
                        buscarVehiculos(lastFilter);
                    buscarDatosFiltro();
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelableArrayList("vehiculos",vehiculos);
        outState.putString("lastFilter",lastFilter);
        outState.getBoolean("b1", mDatabaseSelected);

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState.containsKey("vehiculos")){
            vehiculos = savedInstanceState.getParcelableArrayList("vehiculos");
            setVehiculosListFragment(vehiculos);
        }


        lastFilter = savedInstanceState.getString("lastFilter");

        mDatabaseSelected = savedInstanceState.getBoolean("b1");


        try {
            URL url = new URL("http://www.android.com/");

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


            httpURLConnection.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}