package com.example.dionicio.appcotizacion.Actividades;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dionicio.appcotizacion.BaseDeDatos.DbData;
import com.example.dionicio.appcotizacion.R;

public class ActivityConfiguracion extends AppCompatActivity {
    private EditText txtUsuario, txtContra, txtPuerto, txtServidor, txtBase;

    private DbData datosBd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        enlazarUI();
        obtenerPreferenciasBaseDeDatos();

        setTitle("Ajustes");

        if(datosBd != null){
            txtUsuario.setText(datosBd.getUser());
            txtContra.setText(datosBd.getPassword());
            txtServidor.setText(datosBd.getServer());
            txtPuerto.setText(String.valueOf(datosBd.getPort()));
            txtBase.setText(datosBd.getDatabase());
        }



    }

    private void enlazarUI() {
        txtUsuario = (EditText)findViewById(R.id.txt_usuario_db);
        txtContra = (EditText)findViewById(R.id.txt_contra_db);
        txtPuerto = (EditText)findViewById(R.id.txt_puerto_db);
        txtServidor = (EditText)findViewById(R.id.txt_servidor_db);
        txtBase = (EditText)findViewById(R.id.txt_base_db);

        txtUsuario.setHint("Usuario");
        txtContra.setHint("Contraseña");
        txtPuerto.setHint("Puerto");
        txtServidor.setHint("Servidor");
        txtBase.setHint("Base de datos");

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_config,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.boton_guardar_ajustes:
                try{
                    mostrarDialogoGuardar();
                }catch (Exception e){
                    Toast.makeText(ActivityConfiguracion.this,e.toString(),Toast.LENGTH_LONG).show();
                }

                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void mostrarDialogoGuardar() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ActivityConfiguracion.this);
        final TextInputEditText txtPass;

        View input = LayoutInflater.from(ActivityConfiguracion.this).inflate(R.layout.input_dialog_password_config,null);


        txtPass = input.findViewById(R.id.txt_pass_dialog);

        builder.setMessage("Introduzca la contraseña para guardar los datos.");
        builder.setView(input);
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String pass = txtPass.getText().toString() ;
                String confPass ="abc123";
                SharedPreferences preferences = getSharedPreferences("base_de_datos",MODE_PRIVATE);

                if(confPass.compareToIgnoreCase(pass) == 0){
                    String server, user, password, dbName, port;

                    server = txtServidor.getText().toString();
                    user = txtUsuario.getText().toString();
                    password = txtContra.getText().toString();
                    dbName = txtBase.getText().toString();
                    port = txtPuerto.getText().toString();

                    if(preferences != null){
                        SharedPreferences.Editor editor = preferences.edit();

                        editor.putString("server",server);
                        editor.putString("database",dbName);
                        editor.putString("user",user);
                        editor.putString("password",password);
                        editor.putInt("port",Integer.valueOf(port));

                        if(editor.commit()){
                            Toast.makeText(ActivityConfiguracion.this,"Se han guardado los datos correctamente", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(ActivityConfiguracion.this,"Error al tratar de guardar!", Toast.LENGTH_SHORT).show();
                        }
                        setResult(RESULT_OK);
                        finish();
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

        AlertDialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                txtPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                        if(i == EditorInfo.IME_ACTION_DONE){
                            String pass = txtPass.getText().toString();
                            String confPass ="abc123";
                            SharedPreferences preferences = getSharedPreferences("base_de_datos",MODE_PRIVATE);

                            if(confPass.compareToIgnoreCase(pass) == 0){
                                String server, user, password, dbName, port;

                                server = txtServidor.getText().toString();
                                user = txtUsuario.getText().toString();
                                password = txtContra.getText().toString();
                                dbName = txtBase.getText().toString();
                                port = txtPuerto.getText().toString();

                                if(preferences != null){
                                    SharedPreferences.Editor editor = preferences.edit();

                                    editor.putString("server",server);
                                    editor.putString("database",dbName);
                                    editor.putString("user",user);
                                    editor.putString("password",password);
                                    editor.putInt("port",Integer.valueOf(port));

                                    if(editor.commit()){
                                        Toast.makeText(ActivityConfiguracion.this,"Se han guardado los datos correctamente", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(ActivityConfiguracion.this,"Error al tratar de guardar!", Toast.LENGTH_SHORT).show();
                                    }
                                }

                            }
                        }

                        return true;
                    }
                });
            }
        });

        dialog.show();

    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        finish();
    }
}
