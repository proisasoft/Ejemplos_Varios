package com.example.dionicio.appcotizacion.Actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dionicio.appcotizacion.Adaptadores.AdaptadorArticulosBuscados;
import com.example.dionicio.appcotizacion.BaseDeDatos.DbData;
import com.example.dionicio.appcotizacion.BaseDeDatos.SqlConnection;
import com.example.dionicio.appcotizacion.Clases.Articulo;
import com.example.dionicio.appcotizacion.Clases.Constantes;
import com.example.dionicio.appcotizacion.Clases.MetodosEstaticos;
import com.example.dionicio.appcotizacion.R;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class SeleccionarItem extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private ArrayList<Articulo> articulos;

    private String[] filtros = new String[]{"ar_descri","ar_codigo", "ar_refer"};

    //UI
    private ListView listaItems;
    private AdaptadorArticulosBuscados adaptador;
    private RadioGroup rgFiltro;
    private ImageButton btnBuscar;
    private EditText txtBusqueda;

    //Datos de la BD
    private DbData datosBd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_item);

        setTitle("Buscar Articulo");

        enlazarUI();
        obtenerPreferenciasBaseDeDatos();


        //buscarArticulos("");
        //new BuscarArticulos().execute("SELECT TOP(100) * FROM IVBDARTI");
    }

    private void enlazarUI(){
        listaItems = (ListView)findViewById(R.id.lista_de_items);
        rgFiltro = (RadioGroup)findViewById(R.id.rg_filtro_items);
        txtBusqueda = (EditText)findViewById(R.id.txt_busqueda_articulo);
        btnBuscar = (ImageButton)findViewById(R.id.btn_busqueda);

        btnBuscar.setOnClickListener(this);

        //Recordar: implementar la interfaz
        rgFiltro.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                switch (id){
                    case R.id.rb_codigo_item:
                        txtBusqueda.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;

                    default:
                        txtBusqueda.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        txtBusqueda.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i){
                    case EditorInfo.IME_ACTION_DONE:
                        buscarArticulos(txtBusqueda.getText().toString());
                        MetodosEstaticos.ocultarTeclado(txtBusqueda,SeleccionarItem.this);
                        return  true;
                }

                return false;
            }
        });
        listaItems.setOnItemClickListener(this);
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
            case R.id.btn_busqueda:
                String valorBusqueda  = txtBusqueda.getText().toString();
                buscarArticulos(valorBusqueda);
                break;

        }
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int position, long l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(SeleccionarItem.this);
        Articulo articulo = (Articulo)adapterView.getItemAtPosition(position);


        builder.setTitle(articulo.getNombre());

        builder.setItems(new String[]{"Seleccionar", "Ver foto"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
             switch (i){
                 case 0:
                     dialogInterface.dismiss();
                     mostrarDialogoIngresarCantidad(position);
                     break;

                 case 1:
                     dialogInterface.dismiss();
                     new DownLoadImage(articulos.get(position).getCodigo()).execute(String.format("SELECT ar_imagen2 FROM ivbdarti WHERE ar_codigo='%s'",articulos.get(position).getCodigo()),"ar_imagen2");
             }
            }
        });


        builder.create().show();
    }

    private void mostrarDialogoIngresarCantidad(final int position) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(SeleccionarItem.this);
        View vista;
        final TextInputEditText txtCantidad;

        builder.setTitle(articulos.get(position).getNombre());

        vista = LayoutInflater.from(SeleccionarItem.this).inflate(R.layout.input_dialog,null);
        txtCantidad = (TextInputEditText)vista.findViewById(R.id.txt_cantidad_dialog);
        builder.setView(vista);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String cantidad = txtCantidad.getText().toString();
                double nuevaCantidad;

                if(!TextUtils.isEmpty(cantidad)){
                    nuevaCantidad = Double.parseDouble(cantidad);
                }else{
                    nuevaCantidad = 1.0;
                }

                articulos.get(position).setCantidad(nuevaCantidad);
                Intent data = new Intent();
                data.putExtra("articulo",articulos.get(position));
                setResult(RESULT_OK,data);
                finish();
            }
        });

        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                txtCantidad.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        switch (actionId){
                            case EditorInfo.IME_ACTION_DONE:
                                String cantidad = txtCantidad.getText().toString();
                                double nuevaCantidad;

                                if(!TextUtils.isEmpty(cantidad)){
                                    nuevaCantidad = Double.parseDouble(cantidad);
                                }else{
                                    nuevaCantidad = 1.0;
                                }

                                articulos.get(position).setCantidad(nuevaCantidad);
                                Intent data = new Intent();
                                data.putExtra("articulo",articulos.get(position));
                                setResult(RESULT_OK,data);
                                finish();
                                return  true;

                        }

                        return false;
                    }
                });
            }
        });

        dialog.show();




    }

    private void buscarArticulos(String valorBusqueda) {
        String campo, query = "";

        switch (rgFiltro.getCheckedRadioButtonId()){
            case R.id.rb_codigo_item:
                campo = filtros[1];
                break;
            case R.id.rb_referencia_item:
                campo = filtros[2];
                break;

            default:
                campo = filtros[0];
        }

        if(TextUtils.isEmpty(valorBusqueda)){
            //query = "SELECT TOP(200) ar_codigo, ar_descri, ar_predet, AR_ITBIS FROM IVBDARTI";
            Toast.makeText(SeleccionarItem.this,"Debe escribir el articulo que desea buscar",Toast.LENGTH_SHORT).show();
            return;
        }else{
            query = String.format("SELECT ar_codigo, ar_descri, ar_predet, AR_ITBIS FROM ivbdarti WHERE %s LIKE '%%%s%%'",campo,valorBusqueda);
        }

        new BuscarArticulos().execute(query);
    }



    private class BuscarArticulos extends AsyncTask<String,Void,Boolean>{
        ProgressBar barProgreso;
        AlertDialog dialog;
        int p;

        @Override
        protected void onPreExecute() {
            if(articulos == null){
                articulos = new ArrayList<>();
            }else {
                adaptador.notifyDataSetInvalidated();
                articulos.clear();
            }

            p = 0;

            AlertDialog.Builder builder = new AlertDialog.Builder(SeleccionarItem.this);

            builder.setTitle("Buscando...");
            View vista = LayoutInflater.from(SeleccionarItem.this).inflate(R.layout.progress_dialog_layout,null);
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
            double itbisValue;
            char itbisType;
            String campoItbis;

            if(connection.isConnected()){
                ResultSet rs = connection.consulta(strings[0]);
                ResultSet rsItbis;
                Articulo tempItem;

                try {
                    while (rs.next()){
                        tempItem = new Articulo();
                        itbisValue = 0.0;
                        campoItbis = "";

                        tempItem.setCodigo(rs.getString("ar_codigo"));
                        tempItem.setNombre(rs.getString("ar_descri"));
                        tempItem.setPrecio(rs.getDouble("ar_predet"));

                        itbisType = rs.getString("ar_itbis").charAt(0);

                        if(itbisType == Articulo.ITBIS_NORMAL){
                            campoItbis = "itbis";
                        }else if(itbisType == Articulo.ITBIS_TRANSITORIO){
                            campoItbis = "itbis1";
                        }


                        //Consultar el itbis para el articulo actual dependiendo de su tipo
                        rsItbis = connection.consulta(String.format("SELECT %s FROM fabdproc",campoItbis));
                        if(rsItbis != null){
                            if ((rsItbis.next())){
                                itbisValue = rsItbis.getDouble(1);
                            }
                        }

                        tempItem.setItbis(itbisValue);

                        articulos.add(tempItem);//Añadir el articulo a la lista

                    }
                } catch (SQLException e) {
                    return  false;
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
                if(adaptador == null){
                    adaptador = new AdaptadorArticulosBuscados(SeleccionarItem.this,R.layout.list_articulos_layout,articulos);
                    listaItems.setAdapter(adaptador);
                }

                adaptador.notifyDataSetChanged();
            }else{
                Toast.makeText(SeleccionarItem.this,"Ha ocurrido un error. Verifique su conexion a Internet",Toast.LENGTH_SHORT).show();
            }


            if(articulos.size() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(SeleccionarItem.this);

                builder.setTitle("Articulo no Encontrado");
                builder.setIcon(R.drawable.ic_info);


                builder.setMessage(String.format("No se ha encontrado ningún artículo con el valor \"%s\"",txtBusqueda.getText().toString()));
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                builder.create().show();
            }

        }
    }

    public class DownLoadImage extends AsyncTask<String,Void,Boolean> {
        private Bitmap bm;
        private String nombre;
        ProgressBar barProgreso;
        AlertDialog dialog;
        int p;

        public DownLoadImage(String nombre) {
            this.nombre = nombre;
        }

        @Override
        protected void onPreExecute() {
            p = 0;

            AlertDialog.Builder builder = new AlertDialog.Builder(SeleccionarItem.this);

            builder.setTitle("Espere por favor...");
            View vista = LayoutInflater.from(SeleccionarItem.this).inflate(R.layout.progress_dialog_layout,null);
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
            File f = new File(Constantes.DIRECTORIO_FOTOS_ARTICULOS,nombre.contains(".png")?(nombre):(nombre+".png"));
            String query = strings[0];
            String campo = strings[1];

            if(f.isFile()){
                return  true;
            }

            SqlConnection connection = new SqlConnection(datosBd);

            if(connection != null){
                if(connection.isConnected()){
                    ResultSet rs  = connection.consulta(query);

                    try {
                        if(rs.next()){
                            bm = MetodosEstaticos.decodeBase64(rs.getString(campo));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        return false;
                    }
                }
            }else{
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            dialog.dismiss();

            if(result && bm != null){
                if(MetodosEstaticos.guardarImagen(Constantes.DIRECTORIO_FOTOS_ARTICULOS,nombre,bm)){
                    Intent verFoto = new Intent(SeleccionarItem.this,VerFoto.class);
                    verFoto.putExtra(VerFoto.KEY_NOMBRE,nombre+".png");
                    verFoto.putExtra(VerFoto.KEY_DIRECTORIO,Constantes.DIRECTORIO_FOTOS_ARTICULOS);
                    startActivity(verFoto);
                }
            }else if(bm == null && result){
                Intent verFoto = new Intent(SeleccionarItem.this,VerFoto.class);
                verFoto.putExtra(VerFoto.KEY_NOMBRE,nombre+".png");
                verFoto.putExtra(VerFoto.KEY_DIRECTORIO, Constantes.DIRECTORIO_FOTOS_ARTICULOS);
                startActivity(verFoto);
            } else {
                Toast.makeText(SeleccionarItem.this,"No hay imagen disponible",Toast.LENGTH_SHORT).show();
            }

        }
    }
}
