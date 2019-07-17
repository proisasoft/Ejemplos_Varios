package com.example.administrador.capturarfoto;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceScreen;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.jar.Attributes;

public class MainActivity extends AppCompatActivity{

    Button btnTomarFoto, btnObtenerFoto;
    ImageView imagen;
    Bitmap mBitmap;
    final static int RESPUESTA_CAMERA = 1;
    final static String LLAVE_URI = "URI";
    private Uri mImgUri;
    String miRuta, nombreDeArchivo;




    //Ruta que creará la carpeta para alamacenar archivos de la aplicación
    final static File RUTA_DE_ARCHIVOS = new File(Environment.getExternalStorageDirectory()+"/Proisa/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnTomarFoto = (Button)findViewById(R.id.btn_tomar_foto);
        btnObtenerFoto = (Button)findViewById(R.id.btn_obtener_foto);
        imagen = (ImageView)findViewById(R.id.imagen);



        btnTomarFoto.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        seguroGuardarImagen();
                    }
                }).start();
            }
        });

        btnObtenerFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {

                    }
                }).start();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        if (resultCode == Activity.RESULT_OK)
        {
               mBitmap = obtenerImagen(mBitmap);
            
                if(mBitmap != null)
                {
                   // imagen.setImageBitmap(redimensionarImagen(mBitmap,mBitmap.getWidth()/2,mBitmap.getHeight()/2));
                    imagen.setImageBitmap(mBitmap);
                    miRuta = RUTA_DE_ARCHIVOS.toString()+'/';
                    nombreDeArchivo = mImgUri.getLastPathSegment();


                            dialogoGuardar();
                }
        }
        else{
            Toast.makeText(this,"No tomaste la foto",Toast.LENGTH_LONG).show();
        }

        //Toast.makeText(this,mImgUri.toString(),Toast.LENGTH_LONG).show();
    }

    private boolean guardarImagen()
    {
        boolean guardadaEnMemo = false;

        if(!RUTA_DE_ARCHIVOS.exists()) {
            RUTA_DE_ARCHIVOS.mkdirs();
        }else {
            FileOutputStream fos = null;

             miRuta = RUTA_DE_ARCHIVOS.toString()+'/'; //Environment.getExternalStorageDirectory() + "/Download/";
            nombreDeArchivo = UUID.randomUUID().toString().substring(0, 7) + ".jpg";
            //Toast.makeText(this, miRuta + nombreDeArchivo, Toast.LENGTH_LONG).show();
            //Toast.makeText(this,miRuta+nombreDeArchivo,Toast.LENGTH_LONG).show();
            try {
                fos = new FileOutputStream(miRuta+nombreDeArchivo);

                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();
                fos = null;

                guardadaEnMemo = true;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
            } finally {
                if (fos != null) {
                    try {
                        fos.flush();
                        fos.close();
                    } catch (IOException e) {
                        Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        return guardadaEnMemo;
    }

    private void guardarImagenEnServer(String ruta, String nombreArchivo) {
        ConexionSQL con = new ConexionSQL();
        boolean respuesta;

        if(!con.conexionEsNula())
        {
            String query = String.format("INSERT INTO pruebaimagen(id_imagen,ar_imagen) VALUES (?,?)");
            respuesta = con.escribeImagenEnBD(ruta+nombreArchivo,query);

            if (!respuesta)
                Toast.makeText(this,con.obtenerError(),Toast.LENGTH_LONG).show();
            else
                Toast.makeText(this,"Se guardó",Toast.LENGTH_LONG).show();
        }
    }

    private void dialogoGuardar()
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alert;
        builder.setTitle("¿Está seguro que desea guardar?");
        builder.setMessage("Se guardará en el servidor");
        //builder.setMessage(query.getQuery());
        builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    guardarImagenEnServer(miRuta, nombreDeArchivo);


                    //eliminarArchivo(miRuta, nombreDeArchivo);

                    /*if(!(new File(miRuta,nombreDeArchivo).exists())) {
                       mostarMensaje("No se guardó");
                    }*/
                //mostarMensaje("Se guardó la imagen");

            }
        });

        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //eliminarArchivo(miRuta, nombreDeArchivo);
                //areaParaFirmar.limpiarArea();
            }
        });

        alert = builder.create();
        alert.show();
    }

    private ArrayList<String> crearLista(String[] campos)
    {
        ArrayList<String> list = new ArrayList<>();

        for(int i = 0; i < campos.length; i++) {
            list.add(campos[i]);
        }

        return list;
    }
    private Bitmap redimensionarImagen(Bitmap mBitmap, float nuevoAncho, float nuevoAlto) {
        int width = mBitmap.getWidth();
        int height = mBitmap.getHeight();
        float scaleWidth = ((float) nuevoAncho) / width;
        float scaleHeight = ((float) nuevoAlto) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        return Bitmap.createBitmap(mBitmap, 0, 0, width, height, matrix, false);
    }

    private void seguroGuardarImagen()
    {
        Intent actividadCamara = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File foto = null;
        nombreDeArchivo = UUID.randomUUID().toString().substring(0, 7);
        try{
            //Toast.makeText(this,nombreDeArchivo,Toast.LENGTH_LONG).show();
            foto = File.createTempFile(nombreDeArchivo,".jpg",new File(RUTA_DE_ARCHIVOS.toString()));
            foto.delete();
        }catch (Exception e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }

        mImgUri = Uri.fromFile(foto);
        actividadCamara.putExtra(MediaStore.EXTRA_OUTPUT,mImgUri);
        startActivityForResult(actividadCamara,RESPUESTA_CAMERA);

    }

    private Bitmap obtenerImagen(Bitmap bitmap)
    {
        this.getContentResolver().notifyChange(mImgUri,null);
        ContentResolver cr = this.getContentResolver();
        
        try{
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr,mImgUri);
        }catch (Exception e) {
            Toast.makeText(this,e.toString(),Toast.LENGTH_SHORT).show();
        }

        return bitmap;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mImgUri != null)
        {
            outState.putString(LLAVE_URI,mImgUri.toString());
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if(savedInstanceState.containsKey(LLAVE_URI)){
            mImgUri = Uri.parse( savedInstanceState.getString(LLAVE_URI));
        }
    }


}


