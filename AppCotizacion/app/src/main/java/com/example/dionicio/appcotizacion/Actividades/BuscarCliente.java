package com.example.dionicio.appcotizacion.Actividades;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

import com.example.dionicio.appcotizacion.Adaptadores.AdaptadorClientes;
import com.example.dionicio.appcotizacion.BaseDeDatos.DbData;
import com.example.dionicio.appcotizacion.BaseDeDatos.SqlConnection;
import com.example.dionicio.appcotizacion.Clases.MetodosEstaticos;
import com.example.dionicio.appcotizacion.R;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BuscarCliente extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private ArrayList<String> nombres;
    private ArrayList<String> codigos;

    private AdaptadorClientes adaptador;

    private String[] filtros = new String[]{"cl_nombre","cl_rnc","cl_telef1"};

    //UI
    private ListView lsClientes;
    private EditText txtBusqueda;
    private ImageButton btnBuscar;
    private RadioGroup rgFiltro;

    //Datos de la BD
    private DbData datosBd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_cliente);

        setTitle("Buscar Cliente");
        obtenerPreferenciasBaseDeDatos();

        enlazarUI();

        //buscarCliente("");

    }
    private void enlazarUI(){
        lsClientes = (ListView)findViewById(R.id.lista_clientes);
        txtBusqueda = (EditText)findViewById(R.id.txt_busqueda_cliente);
        btnBuscar = (ImageButton)findViewById(R.id.btn_busqueda);
        rgFiltro = (RadioGroup)findViewById(R.id.rg_filtro_cliente);

        btnBuscar.setOnClickListener(this);

        txtBusqueda.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                switch (i){
                    case EditorInfo.IME_ACTION_DONE:
                        buscarCliente(txtBusqueda.getText().toString());
                        MetodosEstaticos.ocultarTeclado(txtBusqueda,BuscarCliente.this);
                        return true;
                }

                return false;
            }
        });

        rgFiltro.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i){
                    case R.id.rb_rnc_cliente:
                        txtBusqueda.setInputType(InputType.TYPE_CLASS_NUMBER);
                        break;

                    case R.id.rb_telefono_cliente:
                        txtBusqueda.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;

                    default:
                        txtBusqueda.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }
        });

        lsClientes.setOnItemClickListener(this);
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
                String valorBusqueda = txtBusqueda.getText().toString();
                buscarCliente(valorBusqueda);
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent datos;

        datos = new Intent();

        datos.putExtra("nombre",nombres.get(i));
        datos.putExtra("codigo",codigos.get(i));

        setResult(RESULT_OK,datos);
        finish();
    }

    private void buscarCliente(String valorBusqueda) {
        String campo, query;
        switch (rgFiltro.getCheckedRadioButtonId()){
            case R.id.rb_rnc_cliente:
                campo = filtros[1];
                break;
            case R.id.rb_telefono_cliente:
                campo = filtros[2];
                break;

            default:
                campo = filtros[0];
        }

        if(TextUtils.isEmpty(valorBusqueda)){
            Toast.makeText(BuscarCliente.this,"Debe escribir el nombre que desea buscar",Toast.LENGTH_SHORT).show();
            return;
        }else{
            query = String.format("SELECT cl_nombre, cl_codigo FROM CCBDCLIE WHERE %s LIKE '%%%s%%'",campo,valorBusqueda);
        }

        new BusquedaClientes().execute(query);

    }

    private class BusquedaClientes extends AsyncTask<String, Void, Boolean>{
        ProgressBar barProgreso;
        AlertDialog dialog;
        int p;

        @Override
        protected void onPreExecute() {
            btnBuscar.setEnabled(false);

            if(nombres == null){
                nombres = new ArrayList<>();
                codigos = new ArrayList<>();
            }else{
                adaptador.notifyDataSetInvalidated();
                nombres.clear();
                codigos.clear();
            }

            p = 0;

            AlertDialog.Builder builder = new AlertDialog.Builder(BuscarCliente.this);

            builder.setTitle("Buscando...");
            View vista = LayoutInflater.from(BuscarCliente.this).inflate(R.layout.progress_dialog_layout,null);
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

                try {
                    while (rs.next()){
                        nombres.add(rs.getString(1));
                        codigos.add(rs.getString(2));
                    }
                } catch (SQLException e) {
                    return  false;
                }
            }else{
                return  false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            btnBuscar.setEnabled(true);
            dialog.dismiss();

            if(result){
                if(adaptador == null){
                    adaptador = new AdaptadorClientes(BuscarCliente.this,R.layout.list_clientes_layout,nombres,codigos);
                    lsClientes.setAdapter(adaptador);
                }

                adaptador.notifyDataSetChanged();
            }

            if(nombres.size() == 0){
                AlertDialog.Builder builder = new AlertDialog.Builder(BuscarCliente.this);

                builder.setTitle("Cliente no Encontrado");
                builder.setIcon(R.drawable.ic_info);


                builder.setMessage(String.format("No se ha encontrado ningún cliente con el valor \"%s\"",txtBusqueda.getText().toString()));
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
}
