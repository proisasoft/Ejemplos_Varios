package com.example.dionicio.appcotizacion.Actividades;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dionicio.appcotizacion.BaseDeDatos.DbData;
import com.example.dionicio.appcotizacion.BaseDeDatos.SqlConnection;
import com.example.dionicio.appcotizacion.Clases.MetodosEstaticos;
import com.example.dionicio.appcotizacion.R;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityLogin extends AppCompatActivity {
    private static final int REQUEST_AJUSTES = 1;
    private TextInputEditText txtUser, txtPass;
    private Button btnEntrar;
    private String user, pass;
    private DbData datosBd;

    private Switch swRecordar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        setTitle("Iniciar Sesión");

        txtUser = (TextInputEditText)findViewById(R.id.txt_user);
        txtPass = (TextInputEditText)findViewById(R.id.txt_pass);
        btnEntrar = (Button)findViewById(R.id.btn_entrar);
        swRecordar = (Switch)findViewById(R.id.sw_recordar_user);

        obtenerPreferenciasBaseDeDatos();
        obtenerPreferenciasUsuario();

        MetodosEstaticos.chequearSiTienePermiso(ActivityLogin.this, Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
        MetodosEstaticos.chequearSiTienePermiso(ActivityLogin.this,Manifest.permission.READ_EXTERNAL_STORAGE, 2);

        txtUser.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {

                if(id == EditorInfo.IME_ACTION_DONE){
                    if(!TextUtils.isEmpty(txtUser.getText().toString())){
                        txtPass.requestFocus();
                    }

                    return true;
                }else if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    if(!TextUtils.isEmpty(txtUser.getText().toString())){
                        txtPass.requestFocus();
                    }
                }

                return false;
            }
        });

        txtPass.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                switch (id){
                    case EditorInfo.IME_ACTION_DONE:
                        user = txtUser.getText().toString();
                        pass = txtPass.getText().toString();
                        MetodosEstaticos.ocultarTeclado(txtPass,ActivityLogin.this);
                        new ValidarEntrada().execute();
                        return true;

                    default:
                        return false;
                }
            }
        });


        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = txtUser.getText().toString();
                pass = txtPass.getText().toString();
                MetodosEstaticos.ocultarTeclado(txtPass,ActivityLogin.this);
                new ValidarEntrada().execute();

            }
        });

        swRecordar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
               /* SharedPreferences preferences = getSharedPreferences("user_data",MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                if(b){
                    editor.putString("user",txtUser.getText().toString());
                    editor.putBoolean("user_activo",b);
                }else{
                    editor.putString("user","");
                    editor.putBoolean("user_activo",b);
                }

                editor.commit();*/
               almacenarUsuario(b);
            }
        });

    }

    private void obtenerPreferenciasUsuario() {
        SharedPreferences preferences = getSharedPreferences("user_data",MODE_PRIVATE);

        if(preferences.contains("user")){
            txtUser.setText(preferences.getString("user",""));
            swRecordar.setChecked(preferences.getBoolean("user_activo",false));

            if(!TextUtils.isEmpty(txtUser.getText().toString())){
                txtPass.requestFocus();
            }
        }

    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_config_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.boton_ajustes:
                Intent ajustes = new Intent(ActivityLogin.this,ActivityConfiguracion.class);
                startActivityForResult(ajustes, REQUEST_AJUSTES);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_AJUSTES){
            if(resultCode == RESULT_OK){
                obtenerPreferenciasBaseDeDatos();
            }
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

    public void almacenarUsuario(boolean b){
        SharedPreferences preferences = getSharedPreferences("user_data",MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        if(b){
            editor.putString("user",txtUser.getText().toString());
            editor.putBoolean("user_activo",b);
        }else{
            editor.putString("user","");
            editor.putBoolean("user_activo",b);
        }

        editor.commit();
    }


    private class ValidarEntrada extends AsyncTask<Void,Void,Boolean>{
        ProgressBar barProgreso;
        AlertDialog dialog;
        int p;

        @Override
        protected void onPreExecute() {
            btnEntrar.setEnabled(false);
            p = 0;

            almacenarUsuario(swRecordar.isChecked());

            AlertDialog.Builder builder = new AlertDialog.Builder(ActivityLogin.this);

            builder.setTitle("Iniciando...");
            View vista = LayoutInflater.from(ActivityLogin.this).inflate(R.layout.progress_dialog_layout,null);
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
        protected Boolean doInBackground(Void... voids) {
            SqlConnection connection = new SqlConnection(datosBd);

            if(connection.isConnected()){
                ResultSet rs = connection.consulta(String.format("SELECT USUARIO, CLAVE FROM CONTASEG WHERE USUARIO='%s'",user));


                try {
                    if(rs.next()){
                        if(user.compareToIgnoreCase(rs.getString(1).trim()) == 0){
                            if(pass.compareTo(rs.getString(2).trim()) == 0){
                                return true;
                            }
                        }
                    }
                } catch (SQLException e) {

                }

            }


            return false;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            btnEntrar.setEnabled(true);
            dialog.dismiss();

            if(aBoolean){
                setResult(RESULT_OK,new Intent().putExtra("logeado",true));
                finish();
            }else{
                Toast.makeText(ActivityLogin.this,"Usuario o Contraseña incorrectos",Toast.LENGTH_SHORT).show();
            }
        }
    }

}
