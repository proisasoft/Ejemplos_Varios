package com.example.dionicio.appcotizacion.Actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dionicio.appcotizacion.Adaptadores.AdaptadorArticulosSeleccionados;
import com.example.dionicio.appcotizacion.BaseDeDatos.DbData;
import com.example.dionicio.appcotizacion.BaseDeDatos.SqlConnection;
import com.example.dionicio.appcotizacion.Clases.Articulo;
import com.example.dionicio.appcotizacion.Clases.Constantes;
import com.example.dionicio.appcotizacion.Clases.MetodosEstaticos;
import com.example.dionicio.appcotizacion.Plantillas.PrecotizacionPDF;
import com.example.dionicio.appcotizacion.R;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class CreacionPrecotizacion extends AppCompatActivity implements AdapterView.OnItemClickListener{

    public static final int MODO_NUEVO = 0;
    public static final int MODO_MODIFICAR = 1;

    //Request code de las actividades de buscar cliente y articulos
    private static final int BUSCAR_ITEM = 6;
    private static final int BUSCAR_CLIENTE = 7;

    //Datos del cliente: Artributos
    private String nombrecliente;
    private String codigocliente;

    //Atributos
    private ArrayList<Articulo> articulos;
    private int modo;
    private String documento;

    //Intermediarios
    private AdaptadorArticulosSeleccionados adaptador;

    //Elementos de la UI
    private ListView lsArticulos;
    private TextView lblTotalBruto, lblTotalItbis, lblTotalNeto;

    //Datos de la base de datos
    private DbData datosBd;

    double totalBruto;
    double totalItbis;
    double total;

    //Datos de la empresa
    private String emp_nombre;
    private String emp_direccion;
    private String emp_telefono;

    private Date fechaActual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(LayoutInflater.from(CreacionPrecotizacion.this).inflate(R.layout.activity_creacion_precotizacion,null));


        enlazarUI();


        if(datosBd == null){
            obtenerPreferenciasBaseDeDatos();
        }


        if(savedInstanceState == null){
            getModo();
        }else{
            modo = savedInstanceState.getInt("modo");

            articulos = (ArrayList<Articulo>) savedInstanceState.get("articulos");
            nombrecliente = savedInstanceState.getString("cliente");
            codigocliente = savedInstanceState.getString("codigo");
            documento = savedInstanceState.getString("documento");

            setTitle(nombrecliente);

            adaptador = new AdaptadorArticulosSeleccionados(CreacionPrecotizacion.this,R.layout.list_articulos_seleccionados_layout,articulos);
            lsArticulos.setAdapter(adaptador);
            adaptador.notifyDataSetChanged();
        }


        if(modo == MODO_NUEVO && savedInstanceState == null){

            if(articulos != null){
                adaptador = new AdaptadorArticulosSeleccionados(CreacionPrecotizacion.this,R.layout.list_articulos_seleccionados_layout,articulos);
                lsArticulos.setAdapter(adaptador);
                adaptador.notifyDataSetChanged();
            }else{
                articulos = new ArrayList<>();
                nombrecliente = "";
                codigocliente = "";
            }


            setTitle("[Sin Cliente]");
            asignarCliente();
        }else if(modo == MODO_MODIFICAR && savedInstanceState == null){
            //Obtener el documento a modificar, seleccionado previamente
            getDocumentoFromIntent();

            articulos = articulos == null? new ArrayList<Articulo>():articulos;

            //Traer datos de la base de datos
            obtenerDatosDelDocumento(documento);

            setTitle(nombrecliente);
            adaptador = new AdaptadorArticulosSeleccionados(CreacionPrecotizacion.this,R.layout.list_articulos_seleccionados_layout,articulos);
            lsArticulos.setAdapter(adaptador);
            adaptador.notifyDataSetChanged();
        }


        recalcularTotales();
        //No es necesario crear el adaptador cuando es una nueva precotizacion ya que al seleccionar un primer articulo se crea el adaptador
       /* adaptador = new AdaptadorArticulosSeleccionados(CreacionPrecotizacion.this,R.layout.list_articulos_seleccionados_layout,articulos);
        lsArticulos.setAdapter(adaptador);
        adaptador.notifyDataSetChanged();*/



    }

    public void guardarPdf(){
        obtenerDatosEmp();
        PrecotizacionPDF pdf = new PrecotizacionPDF(articulos,emp_nombre,emp_direccion,emp_telefono,fechaActual);
        String nombrePdf;
        File archivo;

        pdf.setNoPrecotizacion(documento);
        pdf.setCliente(nombrecliente);
        pdf.setCodigoCliente(codigocliente == null?(""):(codigocliente));

        nombrePdf = documento+nombrecliente.replace(' ','_');

        pdf.initDocument(nombrePdf,Constantes.DIRECTORIO_PDF_PRECOTIZACION);

        pdf.guardar();

        nombrePdf+=".pdf";

        archivo = new File(Constantes.DIRECTORIO_PDF_PRECOTIZACION,nombrePdf);
        Intent verPdf = new Intent(Intent.ACTION_VIEW);
        verPdf.setDataAndType(Uri.fromFile(archivo), "application/pdf");
        startActivity(verPdf);
    }

    public void obtenerDatosEmp(){
        SqlConnection connection = new SqlConnection(datosBd);

        if(connection.isConnected()){
            ResultSet rs = connection.consulta("SELECT * FROM CONTAEMP");
            ResultSet fecha = connection.consulta("SELECT GETDATE() AS FECHA");
            try {
                if(rs.next()){
                    emp_nombre = rs.getString("nombre").trim();
                    emp_direccion = rs.getString("direc1").trim();
                    emp_telefono = rs.getString("telef1").trim();
                }

                if(fecha.next()){
                    fechaActual = fecha.getDate("FECHA");
                }
            } catch (SQLException e) {

            }
        }
    }

    public void recalcularTotales(){
        total = totalBruto = totalItbis = 0.0;

        for(int i = 0; i < articulos.size(); i++){
            totalBruto += articulos.get(i).getTotal();
            totalItbis += articulos.get(i).getCalculoItbis();
        }

        total = totalBruto + totalItbis;

        lblTotalBruto.setText("RD$ "+MetodosEstaticos.formatNumber(totalBruto));
        lblTotalItbis.setText("RD$ "+MetodosEstaticos.formatNumber(totalItbis));
        lblTotalNeto.setText("RD$ "+MetodosEstaticos.formatNumber(total));
    }



    private void obtenerDatosDelDocumento(String documento) {
        String query = String.format("SELECT HE_NOMBRE, CL_CODIGO FROM IVBDHEPRECOTIZACION WHERE HE_DOCUM='%s'",documento);
        String queryArticulos = String.format("SELECT AR_CODIGO, DE_DESCRI, DE_CANTID, DE_PRECIO FROM IVBDDEPRECOTIZACION WHERE DE_DOCUM='%s'",documento);

        new ObtenerCotizacion().execute(query,queryArticulos);

    }

    private void enlazarUI(){
        lsArticulos = (ListView)findViewById(R.id.lista_articulos_seleccionados);
        lblTotalBruto = (TextView)findViewById(R.id.lbl_total_bruto);
        lblTotalItbis = (TextView)findViewById(R.id.lbl_total_itbis);
        lblTotalNeto = (TextView)findViewById(R.id.lbl_total_neto);


        try {
            lsArticulos.setOnItemClickListener(this);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_creacion_cotizacion,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.boton_establecer_cliente:
                //Pautas para establecer el cliente
                asignarCliente();
                // Toast.makeText(CreacionPrecotizacion.this,"Establecerá el cliente",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.boton_guardar:
                //Guardar o actualizar la precotización

                if(articulos != null){
                   if(articulos.size() > 0){
                       if(modo == MODO_NUEVO){
                           mostrarDialogoDeseaGuardarActualizar("¿Desea guardar esta precotización?","Se guardará todo lo de la lista");
                       }else{
                           mostrarDialogoDeseaGuardarActualizar("¿Desea actualizar esta precotización?","Se actualizarán los datos");
                       }
                   }else{
                       Toast.makeText(CreacionPrecotizacion.this,"No ha seleccionado ningún artículo",Toast.LENGTH_SHORT).show();
                   }
                }else{
                    Toast.makeText(CreacionPrecotizacion.this,"No ha seleccionado ningún artículo",Toast.LENGTH_SHORT).show();
                }

                return true;

            case R.id.boton_buscar_articulo:
                //Buscar el articulo
                Intent selectItem = new Intent(CreacionPrecotizacion.this,SeleccionarItem.class);
                startActivityForResult(selectItem,BUSCAR_ITEM);
                //Toast.makeText(CreacionPrecotizacion.this,"Buscar el Articulo",Toast.LENGTH_SHORT).show();
                return true;

            default:

                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        switch (requestCode){
            case BUSCAR_ITEM:
                if(resultCode == RESULT_OK){
                    if(data != null){
                        Articulo articulo = (Articulo)data.getExtras().get("articulo");

                        int index = existeArticulo(articulo);

                        if(index == -1){
                            articulos.add(articulo);
                        }else{
                            double cantidad = articulos.get(index).getCantidad();
                            articulos.get(index).setCantidad(cantidad+articulo.getCantidad());
                        }

                        if(adaptador == null){
                            adaptador = new AdaptadorArticulosSeleccionados(CreacionPrecotizacion.this,R.layout.list_articulos_seleccionados_layout,articulos);
                            lsArticulos.setAdapter(adaptador);
                        }

                        adaptador.notifyDataSetChanged();

                        recalcularTotales();


                    }
                }
                break;

            case BUSCAR_CLIENTE:
                if(resultCode == RESULT_OK){
                    if(data != null){
                       nombrecliente = data.getExtras().getString("nombre");
                       codigocliente = data.getExtras().getString("codigo");

                       setTitle(nombrecliente);
                    }
                }
                break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("cliente",nombrecliente);
        outState.putString("codigo",codigocliente);
        outState.putString("documento",documento);
        outState.putSerializable("articulos",articulos);
        outState.putInt("modo",modo);
        super.onSaveInstanceState(outState);
    }

 /*   @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        if(savedInstanceState != null){
            nombrecliente = savedInstanceState.getString("cliente");
        }
    }*/

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreacionPrecotizacion.this);
        AlertDialog dialog;

        builder.setTitle(articulos.get(i).getNombre());

        builder.setItems(new String[]{"Modificar Cantidad","Ver Foto","Borrar"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                switch (id){
                    case 0:
                        mostrarDialogoEditar(i);
                        break;

                    case 1:
                        new DownLoadImage(articulos.get(i).getCodigo()).execute(String.format("SELECT ar_imagen2 FROM ivbdarti WHERE ar_codigo='%s'",articulos.get(i).getCodigo()),"ar_imagen2");
                        break;
                    case 2:
                        mostrarDialogoBorrar(i);
                }
            }
        });

        dialog = builder.create();


        dialog.show();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreacionPrecotizacion.this);

        builder.setTitle("¿Está seguro que desea abandonar?");
        builder.setMessage("No se guardará ningún dato");

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }

        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    private void mostrarDialogoBorrar(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreacionPrecotizacion.this);

        builder.setTitle(String.format("¿Está seguro que desea borrar %s?",articulos.get(i).getNombre()));
        builder.setMessage("Se borrará este artículo de esta lista.");

        builder.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                articulos.remove(i);
                adaptador.notifyDataSetChanged();
                recalcularTotales();
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        builder.create().show();
    }

    public void mostrarDialogoEditar(final int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreacionPrecotizacion.this);
        final AlertDialog dialog;

        View vista;
        final TextInputEditText txtCantidad;
        vista = LayoutInflater.from(CreacionPrecotizacion.this).inflate(R.layout.input_dialog,null);
        txtCantidad = (TextInputEditText)vista.findViewById(R.id.txt_cantidad_dialog);
        builder.setView(vista);

        builder.setTitle(articulos.get(i).getNombre());


        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                String cantidad = txtCantidad.getText().toString();
                double nuevaCantidad;

                if(!TextUtils.isEmpty(cantidad)){
                    nuevaCantidad = Double.parseDouble(cantidad);
                }else{
                    nuevaCantidad = 1.0;
                }

                articulos.get(i).setCantidad(nuevaCantidad);

                adaptador.notifyDataSetChanged();
                recalcularTotales();
                dialogInterface.dismiss();
            }
        });


        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialogInterface) {
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


                                articulos.get(i).setCantidad(nuevaCantidad);
                                adaptador.notifyDataSetChanged();
                                recalcularTotales();
                                dialogInterface.dismiss();
                                return  true;

                        }

                        return false;
                    }
                });
            }
        });
        dialog.show();

    }

    public void mostrarDialogoDeseaGuardarActualizar(String titulo, String mensaje){
        AlertDialog.Builder builder = new AlertDialog.Builder(CreacionPrecotizacion.this);


        builder.setTitle(titulo);
        builder.setMessage(mensaje);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(modo == MODO_NUEVO){
                    if(guardarPrecotizacion()){
                        Toast.makeText(CreacionPrecotizacion.this,"Se han guardado los datos correctamente... Generando PDF",Toast.LENGTH_SHORT).show();

                        new Thread(new Runnable() {
                           @Override
                           public void run() {
                               guardarPdf();
                           }
                       }).start();
                        finish();
                    }else{
                        Toast.makeText(CreacionPrecotizacion.this,"No se guardó, intente de nuevo",Toast.LENGTH_SHORT).show();
                    }

                }else if(modo == MODO_MODIFICAR){
                   if(actualizarPrecotizacion(documento)){
                       Toast.makeText(CreacionPrecotizacion.this,"Precotización se ha actualizado... Generando PDF",Toast.LENGTH_SHORT).show();
                       new Thread(new Runnable() {
                           @Override
                           public void run() {
                               guardarPdf();
                           }
                       }).start();
                       finish();
                   }else{
                       Toast.makeText(CreacionPrecotizacion.this,"No se guardó, intente de nuevo",Toast.LENGTH_SHORT).show();
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

        builder.create().show();
    }


    public void asignarCliente(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(CreacionPrecotizacion.this);

        builder.setTitle("Elegir Cliente");

        builder.setItems(new String[]{"Elegir Cliente Existente","Digitar Nombre"}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               switch (i){
                   case 0:
                       Intent buscarCliente = new Intent(CreacionPrecotizacion.this,BuscarCliente.class);
                       startActivityForResult(buscarCliente,BUSCAR_CLIENTE);
                       break;

                   case 1:
                       mostrarDialogoCliente();

                       break;
               }
            }
        });

        dialog = builder.create();

        dialog.show();
    }

    private void mostrarDialogoCliente() {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreacionPrecotizacion.this);
        final AlertDialog dialog;
        View vista = LayoutInflater.from(this).inflate(R.layout.input_name_cliente_layout,null);

        builder.setTitle("Digitar nombre de cliente");
        builder.setView(vista);

        final TextInputEditText txtName = vista.findViewById(R.id.txt_name_dialog);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                nombrecliente = txtName.getText().toString();
                codigocliente = "";

                if(!TextUtils.isEmpty(nombrecliente)){
                    setTitle(nombrecliente);
                }
            }
        });

        dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                txtName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                        switch (actionId) {
                            case EditorInfo.IME_ACTION_DONE:
                                nombrecliente = txtName.getText().toString();
                                codigocliente = "";

                                if(!TextUtils.isEmpty(nombrecliente)){
                                    setTitle(nombrecliente);
                                }
                                dialog.dismiss();
                                return true;

                            default:
                                return false;
                        }
                    }
                });
            }
        });

        dialog.show();
    }

    public boolean guardarPrecotizacion(){
        SqlConnection connection = new SqlConnection(datosBd);
        String query;
        int reg;
        boolean error = false;
        int numDocumento = 0;

        if(connection.isConnected()){
            ResultSet rs = connection.consulta("SELECT precotizacion FROM ivbdproc");

            try {
                if(rs.next()){
                    numDocumento = rs.getInt(1) + 1;
                }
            } catch (SQLException e) {
                connection.rollback();
                error = true;
            }

            if(!error){
                reg = connection.comando("UPDATE IVBDPROC SET precotizacion=precotizacion+1");
                documento = ("0000000000")+numDocumento;
                documento = documento.substring(documento.length()-10);


                if(reg > 0){
                    query = "INSERT INTO IVBDHEPRECOTIZACION (HE_DOCUM,HE_FECHA,CL_CODIGO,HE_NOMBRE,HE_MONTO)" +
                            "VALUES('{docum}',{fecha},'{codigo}','{nombre}',{monto})";

                    query = query.replace("{docum}",documento);
                    query = query.replace("{codigo}",codigocliente);
                    query = query.replace("{nombre}",nombrecliente);
                    query = query.replace("{monto}",String.valueOf(total));
                    query = query.replace("{fecha}","CONVERT(DATE,GETDATE())");


                    reg = connection.comando(query);


                    if(reg > 0){
                        String queryArti = "INSERT INTO IVBDDEPRECOTIZACION(DE_DOCUM,AR_CODIGO,DE_DESCRI,DE_CANTID,DE_PRECIO)"+
                                "VALUES ('%s','%s','%s',%.2f,%.2f)";

                        for(int i = 0; i < articulos.size(); i++){
                            connection.comando(String.format(queryArti,
                                    documento,articulos.get(i).getCodigo(),articulos.get(i).getNombre(),
                                    articulos.get(i).getCantidad(),articulos.get(i).getPrecio()));
                        }
                    }
                }

            }

            connection.commit();

        }


        return !error;
    }

    public boolean actualizarPrecotizacion(String documento){
        SqlConnection connection = new SqlConnection(datosBd);
        String query;
        int reg;
        boolean error = false;


        if(connection.isConnected()){


            query = "UPDATE IVBDHEPRECOTIZACION SET HE_FECHA={fecha},CL_CODIGO='{codigo}',HE_NOMBRE='{nombre}',HE_MONTO={monto} " +
                            "WHERE HE_DOCUM='{docum}'";

            query = query.replace("{docum}",documento);
            query = query.replace("{codigo}",codigocliente);
            query = query.replace("{nombre}",nombrecliente);
            query = query.replace("{monto}",String.valueOf(total));
            query = query.replace("{fecha}","CONVERT(DATE,GETDATE())");


            reg = connection.comando(query);

            if(reg > 0){
                query = String.format("DELETE IVBDDEPRECOTIZACION WHERE DE_DOCUM='%s'",documento);


                reg = connection.comando(query);
            }


            if(reg > 0){

                String queryArti = "INSERT INTO IVBDDEPRECOTIZACION(DE_DOCUM,AR_CODIGO,DE_DESCRI,DE_CANTID,DE_PRECIO)"+
                                "VALUES ('%s','%s','%s',%.2f,%.2f)";

                        for(int i = 0; i < articulos.size(); i++){
                            connection.comando(String.format(queryArti,
                                    documento,articulos.get(i).getCodigo(),articulos.get(i).getNombre(),
                                    articulos.get(i).getCantidad(),articulos.get(i).getPrecio()));
                        }
                    }


            connection.commit();

        }


        return !error;
    }

    private int existeArticulo(Articulo articulo) {

        for(int i = 0; i < articulos.size(); i++){
            if(articulo.getCodigo().compareToIgnoreCase(articulos.get(i).getCodigo()) == 0){
                return i;
            }
        }

        return -1;
    }

    public void getModo() {
        Intent intent =  getIntent();
        Bundle extras = intent == null?(null):intent.getExtras();

        if(extras != null){
            modo = extras.getInt("modo");
        }else{
            modo = -1;
        }
    }
    public void getDocumentoFromIntent() {
        Intent intent =  getIntent();
        Bundle extras = intent == null?(null):intent.getExtras();

        if(extras != null){
            documento = extras.getString("documento");
        }else{
            documento = "";
        }
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

            AlertDialog.Builder builder = new AlertDialog.Builder(CreacionPrecotizacion.this);

            builder.setTitle("Espere por favor...");
            View vista = LayoutInflater.from(CreacionPrecotizacion.this).inflate(R.layout.progress_dialog_layout,null);
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
        protected void onPostExecute(Boolean aBoolean) {

            dialog.dismiss();

            if(aBoolean && bm != null){
                if(MetodosEstaticos.guardarImagen(Constantes.DIRECTORIO_FOTOS_ARTICULOS,nombre,bm)){
                    Intent verFoto = new Intent(CreacionPrecotizacion.this,VerFoto.class);
                    verFoto.putExtra(VerFoto.KEY_NOMBRE,nombre+".png");
                    verFoto.putExtra(VerFoto.KEY_DIRECTORIO, Constantes.DIRECTORIO_FOTOS_ARTICULOS);
                    startActivity(verFoto);
                }
            }else if(bm == null && aBoolean){
                Intent verFoto = new Intent(CreacionPrecotizacion.this,VerFoto.class);
                verFoto.putExtra(VerFoto.KEY_NOMBRE,nombre+".png");
                verFoto.putExtra(VerFoto.KEY_DIRECTORIO,Constantes.DIRECTORIO_FOTOS_ARTICULOS);
                startActivity(verFoto);
            } else {
                Toast.makeText(CreacionPrecotizacion.this,"No hay imagen disponible",Toast.LENGTH_SHORT).show();
            }

        }
    }

    private class ObtenerCotizacion extends AsyncTask<String,Void,Boolean>{
        ProgressBar barProgreso;
        AlertDialog dialog;
        int p;

        @Override
        protected void onPreExecute() {
            p = 0;

            AlertDialog.Builder builder = new AlertDialog.Builder(CreacionPrecotizacion.this);

            builder.setTitle("Obteniendo datos de la cotización...");
            View vista = LayoutInflater.from(CreacionPrecotizacion.this).inflate(R.layout.progress_dialog_layout,null);
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
            String query = strings[0];
            String queryArticulos = strings[1];

            articulos.clear();

            if(connection.isConnected()){
                ResultSet rs = connection.consulta(query);
                ResultSet rsItbis;

                try {
                    if(rs.next()){
                        nombrecliente = rs.getString(1).trim();
                        codigocliente = rs.getString(2).trim();
                    }

                    rs = connection.consulta(queryArticulos);

                    Articulo tempItem;
                    String itbisCampo;
                    char itbisType = 0;
                    double itbisValue;

                    while (rs.next()){
                        tempItem = new Articulo();
                        itbisCampo = "";
                        itbisValue = 0.0;

                        tempItem.setCodigo(rs.getString(1));
                        tempItem.setNombre(rs.getString(2));
                        tempItem.setCantidad(rs.getDouble(3));
                        tempItem.setPrecio(rs.getDouble(4));


                        rsItbis = connection.consulta(String.format("SELECT AR_ITBIS FROM IVBDARTI WHERE ar_codigo='%s'",tempItem.getCodigo()));

                        if(rsItbis != null){
                            rsItbis.next();
                            itbisType = rsItbis.getString(1).charAt(0);
                        }


                        if(itbisType == Articulo.ITBIS_NORMAL){
                            itbisCampo = "itbis";
                        }else if(itbisType == Articulo.ITBIS_TRANSITORIO){
                            itbisCampo = "itbis1";
                        }

                        rsItbis = connection.consulta(String.format("SELECT %s FROM FABDPROC",itbisCampo));

                        if(rsItbis != null){
                            rsItbis.next();
                            itbisValue = rsItbis.getDouble(1);
                        }

                        tempItem.setItbis(itbisValue);

                        articulos.add(tempItem);
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }else{
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            dialog.dismiss();

            if(aBoolean){
                setTitle(nombrecliente);
                adaptador = new AdaptadorArticulosSeleccionados(CreacionPrecotizacion.this,R.layout.list_articulos_seleccionados_layout,articulos);
                lsArticulos.setAdapter(adaptador);
                adaptador.notifyDataSetChanged();
            }else{
                Toast.makeText(CreacionPrecotizacion.this,"Ocurrió un error al obtener los datos",Toast.LENGTH_SHORT).show();
            }

        }
    }

}
