package com.example.pethoalpar.zxingexample;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import android.view.inputmethod.InputMethodManager;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import android.widget.ListView;
import android.widget.ProgressBar;

import android.widget.Switch;
import android.widget.Toast;



import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class
MainActivity extends AppCompatActivity {

    private Button button;
    private ProgressBar bar;
    private ListView mLista;
    private ArrayList<String> valores, valoresAux;
    private ArrayAdapter<String> mAdapter;
    private Switch escaneoAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enlazarConUI();

        valores = new ArrayList<>();
        valoresAux = new ArrayList<>();

        mAdapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,valores);
        mLista.setAdapter(mAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanearCodigo();
            }
        });

        registerForContextMenu(mLista);
    }

    private void enlazarConUI(){
        button = (Button) findViewById(R.id.button);
        bar = (ProgressBar)findViewById(R.id.progressBar);
        mLista = (ListView)findViewById(R.id.list_products);
        escaneoAuto = (Switch)findViewById(R.id.escaneo_auto);
    }
    public void mostrarTeclado(View texto) {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(texto,0);
    }
    public void ocultarTeclado(View texto)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(texto.getWindowToken(),0);
    }

    public void escanearCodigo(){
        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        //integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);

        integrator.setPrompt("Enfoque el codigo con la cámara");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(true);
        //integrator.setPrompt("Use la tecla subir volumen para activar el flash");
        integrator.setCaptureActivity(ActividadCaptura.class);
        integrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if(result != null) {
            if(result.getContents() == null) {
                return;
            } else {
                MediaPlayer mp = MediaPlayer.create(MainActivity.this,R.raw.barlector);
                mp.start();

                String codigoBarra = result.getContents().trim();
                new Escaneo().execute(codigoBarra);
            }


        } else {
            // This is important, otherwise the result will not be passed to the fragment
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu,v,menuInfo);

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_de_contexto_opciones,menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();


        switch(item.getItemId()){
            case R.id.borrar_elemento:
                borrarElemento(menuInfo.position);
                return true;

            case R.id.modificar_elemento:
                modificarElemento(menuInfo.position);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    private void modificarElemento(final int pos) {

        final AlertDialog alertDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.input_numbers_articles,null);
        final EditText txtNumArti = (EditText) v.findViewById(R.id.txt_num_arti);


            builder.setTitle("Introduce la cantidad");
            builder.setMessage(valoresAux.get(pos));
            builder.setView(v);

            builder.setPositiveButton("Guardar", null);

        alertDialog = builder.create();
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button btnOk = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String num = txtNumArti.getText().toString();

                        if(!num.trim().isEmpty()){
                            valores.remove(pos);
                            valores.add(pos,valoresAux.get(pos).concat(String.format(" (%s)",num)));
                            mAdapter.notifyDataSetChanged();
                            alertDialog.dismiss();

                        }else{
                            Toast.makeText(MainActivity.this,"Debe ingresar la cantidad",Toast.LENGTH_SHORT).show();
                            txtNumArti.setText("");
                            return;
                        }

                        alertDialog.dismiss();

                    }
                });

                mostrarTeclado(txtNumArti);
            }
        });



        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

    private void borrarElemento(int pos) {
        valoresAux.remove(pos);
        valores.remove(pos);
        mAdapter.notifyDataSetChanged();
    }

    private class Escaneo extends AsyncTask<String,Void,String>{
        int progress = 0;
        String precio = "",descripcion= "",codigo= "";
        ConexionSQL con;
        @Override
        protected void onPreExecute() {
            button.setEnabled(false);
            mLista.setVisibility(View.INVISIBLE);
            bar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            bar.setProgress(++progress);
        }

        @Override
        protected String doInBackground(String... strings) {
            con = new ConexionSQL();
            codigo = strings[0];
            String query = String.format("SELECT * FROM ivbdarti WHERE ar_codigo='%s'",codigo);

            if(!con.conexionEsNula()){
                ResultSet resultadoBD = con.consulta(query);

                if (resultadoBD != null) {

                    try {
                        if (resultadoBD.next()){
                            descripcion = resultadoBD.getString("ar_descri").trim();
                            precio = resultadoBD.getString("ar_predet");
                        }
                    } catch (SQLException e) {

                    }
                }
                con.desconectarSQL();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            button.setEnabled(true);
            bar.setVisibility(View.INVISIBLE);
            mLista.setVisibility(View.VISIBLE);

            final AlertDialog alertDialog;
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

            View v = LayoutInflater.from(MainActivity.this).inflate(R.layout.input_numbers_articles,null);
            final EditText txtNumArti = (EditText) v.findViewById(R.id.txt_num_arti);

            if(!descripcion.equals("")){
                builder.setTitle("Introduce la cantidad");
                builder.setMessage(descripcion);
                builder.setView(v);

                builder.setPositiveButton("Guardar", null);

            }else if(con.conexionEsNula()){
                builder.setTitle("Error:");
                builder.setIcon(R.mipmap.error);
                builder.setMessage("Verifique su conexion a Internet");
                builder.setPositiveButton("OK", null);
            }else{
                builder.setTitle("Error: ");
                builder.setMessage("Producto no encontrado: "+codigo);
                builder.setIcon(R.mipmap.error);
                builder.setPositiveButton("Ok", null);
            }




            alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    Button btnOk = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if(!descripcion.equals("")){
                                String num = txtNumArti.getText().toString();
                                if(!num.trim().isEmpty()){
                                    valoresAux.add(descripcion);
                                    valores.add(descripcion.concat(String.format(" (%s)",num)));
                                    mAdapter.notifyDataSetChanged();
                                    alertDialog.dismiss();

                                }else{
                                    Toast.makeText(MainActivity.this,"Debe ingresar la cantidad",Toast.LENGTH_SHORT).show();
                                    txtNumArti.setText("");
                                    return;
                                }
                            }
                                if(escaneoAuto.isChecked()){
                                    escanearCodigo();
                                }
                                alertDialog.dismiss();



                        }
                    });

                    mostrarTeclado(txtNumArti);
                }
            });

            alertDialog.setCanceledOnTouchOutside(false);
            alertDialog.show();

        }
    }

}

