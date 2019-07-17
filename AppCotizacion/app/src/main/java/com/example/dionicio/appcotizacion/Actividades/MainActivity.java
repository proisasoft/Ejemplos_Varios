package com.example.dionicio.appcotizacion.Actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.dionicio.appcotizacion.Adaptadores.AdaptadorPrecotizaciones;
import com.example.dionicio.appcotizacion.BaseDeDatos.DbData;
import com.example.dionicio.appcotizacion.BaseDeDatos.SqlConnection;
import com.example.dionicio.appcotizacion.Clases.Constantes;
import com.example.dionicio.appcotizacion.Clases.Precotizacion;
import com.example.dionicio.appcotizacion.R;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private static final int REQUEST_LOGIN = 2;
    private static final int REQUEST_AJUSTES = 3;

    private ArrayList<Precotizacion> precotizaciones;
    private String[] filtros = new String[]{"HE_NOMBRE","HE_DOCUM"};

    //Intermediarios
    private DbData datosBd;
    private AdaptadorPrecotizaciones adapter;

    //Elementos de la UI
    private ListView lsPrecotizaciones;
    private FloatingActionButton fabNueva;

    //Proceso
    private ProcessPreCotizaciones proceso;

    private boolean logeado = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enlazarUI();
        obtenerPreferenciasBaseDeDatos();


        if(savedInstanceState != null){
            logeado = savedInstanceState.getBoolean("logeado");
        }

        if(!logeado)
            callLogin();
        else{
            proceso = new ProcessPreCotizaciones();
            proceso.execute(construirQuery());
        }

        crearDirectoriosBases();
    }

    private void crearDirectoriosBases(){
        File f = new File(Constantes.DIRECTORIO_FOTOS_ARTICULOS);

        if(!f.exists()){
            f.mkdirs();
        }

        f = new File(Constantes.DIRECTORIO_PDF_PRECOTIZACION);

        if(!f.exists()){
            f.mkdirs();
        }
    }

    private void callLogin() {
        Intent intent = new Intent(MainActivity.this,ActivityLogin.class);
        startActivityForResult(intent,REQUEST_LOGIN);
    }

    private void enlazarUI(){
        lsPrecotizaciones = (ListView)findViewById(R.id.lista_de_precotizaciones); //Lista que muestra las precotizaciones
        fabNueva = (FloatingActionButton)findViewById(R.id.fab_nueva);//Boton que crea nueva precotizacion

        fabNueva.setOnClickListener(this);

        lsPrecotizaciones.setOnItemClickListener(this);
    }

    public String construirQuery(){
        String query;

        query = "SELECT * FROM IVBDHEPRECOTIZACION ORDER BY HE_ID DESC";

        return query;
    }

    private void obtenerPreferenciasBaseDeDatos(){
        SharedPreferences mPreferencias = getSharedPreferences("base_de_datos",MODE_PRIVATE);

        if (!mPreferencias.contains("server")){
            SharedPreferences.Editor editor = mPreferencias.edit();

            editor.putString("server","10.0.0.224");
            editor.putString("database","FACFOXSQL");
            editor.putString("user","sa");
            editor.putString("password","pr0i$$a");
            editor.putInt("port",1433);

            if(editor.commit()){
                obtenerPreferenciasBaseDeDatos();
            }
        }else{
            datosBd = new DbData();

            datosBd.setDatabase(mPreferencias.getString("database","nada"));
            datosBd.setServer(mPreferencias.getString("server","nada"));
            datosBd.setUser(mPreferencias.getString("user","nadie"));
            datosBd.setPassword(mPreferencias.getString("password","nada"));
            datosBd.setPort(mPreferencias.getInt("port",0));
        }
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.fab_nueva:
                Intent intent = new Intent();
                intent.putExtra("modo",CreacionPrecotizacion.MODO_NUEVO);
                startActivity(crearNuevaPrecotizacion());
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode){
            case REQUEST_AJUSTES:
                if(resultCode == RESULT_OK){
                    obtenerPreferenciasBaseDeDatos();
                }
                break;
            case REQUEST_LOGIN:
                if(resultCode == RESULT_OK){
                    logeado = data.getExtras().getBoolean("logeado");

                    proceso = new ProcessPreCotizaciones();
                    proceso.execute(construirQuery());
                }else{
                    finish();
                }
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setItems(new String[]{"Modificar"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                switch (id){
                    case 0:
                        startActivity(modificarPrecotizacion(precotizaciones.get(i).getDocumento()));
                        break;
                }
            }
        });

        builder.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_cotizacion,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.boton_refrescar:
                if(proceso != null){
                    if(proceso.getStatus() == AsyncTask.Status.FINISHED){
                        proceso = new ProcessPreCotizaciones();
                        proceso.execute(construirQuery());
                    }
                }else{
                    proceso = new ProcessPreCotizaciones();
                    proceso.execute(construirQuery());
                }
                return true;

            case R.id.boton_ajustes:
                Intent ajustes = new Intent(MainActivity.this,ActivityConfiguracion.class);
                startActivityForResult(ajustes, REQUEST_AJUSTES);
                //Toast.makeText(MainActivity.this,"Ir a los ajustes",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.boton_buscar_precotizacion:
                dialogBusqueda();
                return  true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void dialogBusqueda(){
        final String[] campo = {filtros[0]};
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        final TextInputEditText txtBusqueda;
        final RadioGroup rgFilter;
        View vista;

        vista = LayoutInflater.from(MainActivity.this).inflate(R.layout.busqueda_precortizacion_layout,null);

        txtBusqueda = vista.findViewById(R.id.txt_busqueda_precotizacion);
        rgFilter = vista.findViewById(R.id.rg_filtro_precotizaciones);



        builder.setView(vista);

        builder.setPositiveButton("Buscar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(proceso != null){
                    if(proceso.getStatus() == AsyncTask.Status.FINISHED){
                        proceso = new ProcessPreCotizaciones();
                        proceso.execute(String.format("SELECT * FROM IVBDHEPRECOTIZACION WHERE %s LIKE '%%%s%%' ORDER BY HE_ID DESC",campo[0],txtBusqueda.getText().toString()));
                    }
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });


        AlertDialog dialog =  builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                rgFilter.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int i) {
                        switch (i){
                            case R.id.rb_documento_pre:
                                campo[0] = filtros[1];
                                break;
                            default:
                                campo[0] = filtros[0];
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("logeado",logeado);
    }

    public Intent crearNuevaPrecotizacion(){
        Intent nuevaPreCotizacion;

        nuevaPreCotizacion = new Intent(MainActivity.this,CreacionPrecotizacion.class);
        nuevaPreCotizacion.putExtra("modo",CreacionPrecotizacion.MODO_NUEVO);
        return nuevaPreCotizacion;
    }
    public Intent modificarPrecotizacion(String documento){
        Intent modificarPrecotizacion;
        modificarPrecotizacion = new Intent(MainActivity.this,CreacionPrecotizacion.class);
        modificarPrecotizacion.putExtra("modo",CreacionPrecotizacion.MODO_MODIFICAR);
        modificarPrecotizacion.putExtra("documento",documento);
        return modificarPrecotizacion;
    }



    private class ProcessPreCotizaciones extends AsyncTask<String,Void,Boolean>{
       private String empresa = "No disponible";
        ProgressBar barProgreso;
        AlertDialog dialog;
        int p;

        @Override
        protected void onPreExecute() {
            if(precotizaciones != null){
                adapter.notifyDataSetInvalidated();
                precotizaciones.clear();
            }else{
                precotizaciones = new ArrayList<>();
            }

            p = 0;

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            builder.setTitle("Buscando...");
            View vista = LayoutInflater.from(MainActivity.this).inflate(R.layout.progress_dialog_layout,null);
            builder.setView(vista);
            barProgreso = vista.findViewById(R.id.barra_de_progreso);
            dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);

            dialog.show();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            barProgreso.setProgress(++p);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            SqlConnection connection = new SqlConnection(datosBd);

            if(connection.isConnected()){
                ResultSet rs = connection.consulta(strings[0]);
                ResultSet rs2 = connection.consulta("SELECT nombre FROM CONTAEMP");

                try {
                    Precotizacion nuevaPrecotizacion;
                    while (rs.next()){
                        nuevaPrecotizacion = new Precotizacion();

                        nuevaPrecotizacion.setDocumento(rs.getString(2));
                        nuevaPrecotizacion.setFecha(rs.getDate(3));
                        nuevaPrecotizacion.setCodigoCliente(rs.getString(4));
                        nuevaPrecotizacion.setNombreCliente(rs.getString(5));
                        nuevaPrecotizacion.setTotal(rs.getDouble(6));

                        precotizaciones.add(nuevaPrecotizacion);

                    }

                    if(rs2.next()){
                        empresa = rs2.getString(1).trim();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }else{
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            dialog.dismiss();
            if(result){
                if(adapter == null){
                    adapter = new AdaptadorPrecotizaciones(MainActivity.this,R.layout.lista_precotizaciones_layout,precotizaciones);
                    lsPrecotizaciones.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }else{
                    adapter.notifyDataSetChanged();
                }

                if(precotizaciones.size() == 0){
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                    builder.setTitle("Precotización no Encontrada");
                    builder.setIcon(R.drawable.ic_info);


                    builder.setMessage("No se ha encontrado ninguna precotización con el filtro especificado");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });

                    builder.create().show();
                }

            }else{
                Toast.makeText(MainActivity.this,"Ha ocurrido un error",Toast.LENGTH_SHORT).show();

            }
            getSupportActionBar().setSubtitle(empresa);
        }
    }




}
