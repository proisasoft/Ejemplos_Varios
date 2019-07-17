package com.example.dionicio.appcotizacion.Actividades;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.File;

public class VerFoto extends Activity implements MediaScannerConnection.MediaScannerConnectionClient {
    public static final String KEY_DIRECTORIO = "DIR";
    public static final String KEY_NOMBRE = "NAME";
    File[] todosArchivos;
    String ruta;
    File archivo, directorio;
    String nombreArchivo;

    private MediaScannerConnection mMs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent() == null ? (null) : getIntent().getExtras();

        if (extras != null) {
            nombreArchivo = extras.getString(KEY_NOMBRE);
            ruta = extras.getString(KEY_DIRECTORIO);

            directorio = new File(ruta+'/');

            if (directorio.exists()) {
                todosArchivos = directorio.listFiles();

                for (int i = 0; i < todosArchivos.length; i++) {
                    String name = todosArchivos[i].getName();
                    if (name.compareTo(nombreArchivo) == 0) {
                        archivo = todosArchivos[i];
                        break;
                    }
                }

            }

            if (archivo != null){
                mMs = new MediaScannerConnection(this,this);
                mMs.connect();
            }
        }


    }

    @Override
    public void onMediaScannerConnected() {
        mMs.scanFile(archivo.getAbsolutePath(), null);
    }

    @Override
    public void onScanCompleted(String s, Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(uri);
        startActivity(intent);
        mMs.disconnect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.finish();
    }
}
