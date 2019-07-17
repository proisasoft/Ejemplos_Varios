package com.example.dionicio.appcotizacion.Clases;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.example.dionicio.appcotizacion.BaseDeDatos.DbData;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MetodosEstaticos {

    public final static String FORMATO_FECHA = "dd-MM-yyyy";
    public final static String FORMATO_FECHA_MC = "dd-MMM-yyyy";
    public final static String FORMATO_FECHA_ML = "dd-MMMM-yyyy";
    public final static String FORMATO_FECHA_TIEMPO =  "dd-MM-yyyy HH:mm";

    public static String formatDate(Date fecha){
        SimpleDateFormat format = new SimpleDateFormat(FORMATO_FECHA);
        return format.format(fecha);
    }

    public static String formatDate(Date fecha, String patron){
        SimpleDateFormat format = new SimpleDateFormat(patron, Locale.getDefault());
        return format.format(fecha);
    }

    public static String formatNumber(double number){
        DecimalFormat format = new DecimalFormat("###,##0.00");
        return format.format(number);
    }
    public static String formatNumber(double number, String patron){
        DecimalFormat format = new DecimalFormat(patron);
        return format.format(number);
    }

    public static String obtenerNombre(CharSequence text) {
        String name = "";

        for(int i = 0; text.charAt(i) != ' ' && i < text.length(); i++){
            name += text.charAt(i);
        }

        return name;
    }

    public static void mostrarTeclado(TextView texto, Activity context) {
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(texto,0);
    }
    public static void ocultarTeclado(TextView texto, Activity context)
    {
        InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(texto.getWindowToken(),0);
    }


    public static void chequearSiTienePermiso(Activity context, String permiso, int requestCode)
    {
        if(ContextCompat.checkSelfPermission(context.getApplicationContext(),permiso) != PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(context,permiso))
            {

            }else{
                ActivityCompat.requestPermissions(context,new String[]{permiso},requestCode);
            }
        }
    }

    public static boolean guardarImagen(String ruta, String nombre, Bitmap imagen){
        File dirImages = new File(ruta);

        if(!nombre.contains(".png")){
            nombre = nombre.concat(".png");
        }

        FileOutputStream fos = null;
        File myPath = new File(ruta, nombre);
        try{
            if(!dirImages.exists()){
                dirImages.mkdirs();
            }
            fos = new FileOutputStream(myPath);
            imagen.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
            return false;
        }catch (IOException ex){
            ex.printStackTrace();
           return false;
        }

        return true;
    }

    public static String encodeBase64(File file){
        byte[] fileArray = new byte[(int) file.length()];
        //InputStream inputStream;

        String encodedFile = "";
        try {
            /*inputStream = new FileInputStream(file);
            inputStream.read(fileArray);*/
            encodedFile = android.util.Base64.encodeToString(fileArray, android.util.Base64.DEFAULT);
        } catch (Exception e) {
            // Manejar Error
        }

        return encodedFile;
    }

    public static Bitmap decodeBase64(String strToDecode){
        byte[] bytes = android.util.Base64.decode(strToDecode, android.util.Base64.DEFAULT);

        return BitmapFactory.decodeByteArray(bytes,0,bytes.length);
    }

    public static DbData obtenerPreferenciasBaseDeDatos(SharedPreferences preferences){
        SharedPreferences mPreferencias = preferences;
        DbData datosBd = new DbData();

        if (!mPreferencias.contains("server")){
            SharedPreferences.Editor editor = mPreferencias.edit();

            editor.putString("server","10.0.0.224");
            editor.putString("database","FACFOXSQL");
            editor.putString("user","sa");
            editor.putString("password","pr0i$$a");
            editor.putInt("port",1433);

            if(editor.commit()){
                obtenerPreferenciasBaseDeDatos(preferences);
            }
        }else{

            datosBd.setDatabase(mPreferencias.getString("database","nada"));
            datosBd.setServer(mPreferencias.getString("server","nada"));
            datosBd.setUser(mPreferencias.getString("user","nadie"));
            datosBd.setPassword(mPreferencias.getString("password","nada"));
            datosBd.setPort(mPreferencias.getInt("port",0));
        }

        return datosBd;
    }

    public static int numberOfRows(ResultSet rs){
        if(rs == null){
            return 0;
        }

        int nRows = 0;

        try {
            boolean ultimo = rs.last();

            if(ultimo){
                nRows = rs.getRow();
            }
        } catch (SQLException e) {
            return 0;
        }

        return nRows;
    }

    public static Date convertToDate(String stringDate, String patron){
        SimpleDateFormat format = new SimpleDateFormat(patron);
        Date fecha = null;

        try {
            fecha = format.parse(stringDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return fecha;
    }

    public static double getTotal(double[] valores) {
        double total = 0;

        for(int i = 0; i < valores.length; i++){
            total += valores[i];
        }

        return total;
    }
}