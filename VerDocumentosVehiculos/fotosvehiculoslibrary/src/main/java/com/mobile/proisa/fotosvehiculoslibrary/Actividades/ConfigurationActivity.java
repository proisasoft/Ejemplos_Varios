package com.mobile.proisa.fotosvehiculoslibrary.Actividades;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.proisa.fotosvehiculoslibrary.BaseDeDatos.SqlConnection;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.Constantes;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.MetodosEstaticos;
import com.mobile.proisa.fotosvehiculoslibrary.R;

public class ConfigurationActivity extends AppCompatActivity {
    private EditText txtUsuario, txtContra, txtPuerto, txtServidor, txtBase;
    private static final String confPass ="abc123";
    private SqlConnection.DbData datosBd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuration);
        enlazarUI();
        dialogEntrar();
    }

    private void enlazarUI() {
        txtUsuario = (EditText)findViewById(R.id.txt_usuario_db);
        txtContra = (EditText)findViewById(R.id.txt_contra_db);
        txtPuerto = (EditText)findViewById(R.id.txt_puerto_db);
        txtServidor = (EditText)findViewById(R.id.txt_servidor_db);
        txtBase = (EditText)findViewById(R.id.txt_base_db);


    }
    private void setInfo(){
        datosBd = MetodosEstaticos.obtenerPreferenciasBaseDeDatos(getSharedPreferences(Constantes.PREFERENCES_DATA_BASE,MODE_PRIVATE));

        setTitle("Ajustes");

        if(datosBd != null){
            txtUsuario.setText(datosBd.getUser());
            txtContra.setText(datosBd.getPassword());
            txtServidor.setText(datosBd.getServer());
            txtPuerto.setText(String.valueOf(datosBd.getPort()));
            txtBase.setText(datosBd.getDatabase());
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_config,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();

        if (i == R.id.boton_guardar_ajustes) {
            try {
                mostrarDialogoGuardar();
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
            }

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private void mostrarDialogoGuardar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final TextInputEditText txtPass;

        View input = LayoutInflater.from(this).inflate(R.layout.input_dialog_password_config,null);


        txtPass = input.findViewById(R.id.txt_pass_dialog);

        builder.setMessage("Introduzca la contraseña para guardar los datos.");
        builder.setView(input);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String pass = txtPass.getText().toString() ;
                savePreferences(pass);
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                txtPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if(i == EditorInfo.IME_ACTION_DONE){
                            String pass = txtPass.getText().toString();
                            savePreferences(pass);
                        }
                        return true;
                    }
                });
            }
        });

        dialog.show();

    }
    private void dialogEntrar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final TextInputEditText txtPass;

        View input = LayoutInflater.from(this).inflate(R.layout.input_dialog_password_config,null);


        txtPass = input.findViewById(R.id.txt_pass_dialog);

        builder.setMessage("Introduzca la contraseña para ingresar");
        builder.setView(input);
        builder.setCancelable(false);

        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String pass = txtPass.getText().toString() ;
                if(!(confPass.compareToIgnoreCase(pass) == 0)){
                    setResult(RESULT_CANCELED);
                    finish();
                }else{
                    setInfo();
                }
            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        AlertDialog dialog = builder.create();


        dialog.show();

    }
    private void savePreferences(String pass){
        SharedPreferences preferences = getSharedPreferences(Constantes.PREFERENCES_DATA_BASE,MODE_PRIVATE);

        if(confPass.compareToIgnoreCase(pass) == 0){
            String server, user, password, dbName, port;

            server   = txtServidor.getText().toString();
            user     = txtUsuario.getText().toString();
            password = txtContra.getText().toString();
            dbName   = txtBase.getText().toString();
            port     = txtPuerto.getText().toString();

            if(preferences != null){
                SharedPreferences.Editor editor = preferences.edit();

                editor.putString("server",server);
                editor.putString("database",dbName);
                editor.putString("user",user);
                editor.putString("password",password);
                editor.putInt("port",Integer.valueOf(port));

                if(editor.commit()){
                    Toast.makeText(getApplicationContext(),"Se han guardado los datos correctamente", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"Error al tratar de guardar!", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
