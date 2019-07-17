package com.mobile.proisa.fotosvehiculoslibrary.Clases;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;


import com.mobile.proisa.fotosvehiculoslibrary.BaseDeDatos.SqlConnection;
import com.mobile.proisa.fotosvehiculoslibrary.Interfaces.ITotal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MetodosEstaticos {
    public final static String FORMATO_FECHA = "dd-MM-yyyy";
    public final static String FORMATO_FECHA_MC = "dd-MMM-yyyy";
    public final static String FORMATO_FECHA_ML = "dd-MMMM-yyyy";
    public final static String FORMATO_FECHA_TIEMPO =  "dd-MM-yyyy hh:mm a";
    public final static String FORMATO_FECHA_TIEMPO_1 =  "dd-MM-yyyy HH:mm";
    public final static String FORMATO_FECHA_ESTANDAR = "yyyy-MM-dd";
    public final static String FORMATO_NUMBER_INTEGER = "###,##0";

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
            if(ActivityCompat.shouldShowRequestPermissionRationale(context,permiso)) {
                ActivityCompat.requestPermissions(context,new String[]{permiso},requestCode);
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

        FileOutputStream fos;
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

    public static SqlConnection.DbData obtenerPreferenciasBaseDeDatos(SharedPreferences preferences){
        SharedPreferences mPreferencias = preferences;
        SqlConnection.DbData datosBd = new SqlConnection.DbData();

        if (!mPreferencias.contains("server")){
            SharedPreferences.Editor editor = mPreferencias.edit();

            editor.putString("server", "PROISAVPN3.DDNS.NET");
            editor.putString("database","FACFOXSQL");
            editor.putString("user","sa");
            editor.putString("password","pr0i$$a");
            editor.putInt("port",1433);

            if(editor.commit()){
                return obtenerPreferenciasBaseDeDatos(preferences);
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

    public static Date convertToDate(Timestamp date){
        Date d = date;
        return d;
    }

    public static double getTotal(double[] valores) {
        double total = 0;

        for(int i = 0; i < valores.length; i++){
            total += valores[i];
        }

        return total;
    }
    public static double getTotal(ArrayList<Double> valores) {
        double[] copy =new double[valores.size()];

        for(int i = 0; i < valores.size(); i++){
            copy[i] = valores.get(i);
        }

        return getTotal(copy);
    }

    public static String createDocumento(int nZeros, int secuencia){
        String format = String.format("%%0%dd",nZeros);


        return String.format(format,secuencia);
    }

    public static int getOfCalendar(int field){
        Calendar c = Calendar.getInstance();

        return c.get(field);
    }

    public static int getRango(int dias, int rangoDias){
        int rango, mod;

        mod = dias%rangoDias;
        rango = (dias/rangoDias);

        return rango + (mod == 0?(0):(1));
    }

    public static double getTotal(List<ITotal> totales){
        if(totales != null){
            double total =0;

            for(ITotal t : totales){
                total += t.getTotal();
            }

            return total;
        }else{
            return 0;
        }
    }

    public static Uri compress(Uri foto){
        try {
            CameraUtils.compressPhoto(foto,Constantes.QUALITY_LESS_PHOTO);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return foto;
    }

    public static Bitmap redimensionarImagen(Bitmap mBitmap, float nuevoAncho, float nuevoAlto) {
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

    public static String convertString(String string){
        char[] charSequence;

        try{

          charSequence = string.toCharArray();
          charSequence[0] = Character.toUpperCase(charSequence[0]);

          for(int i = 1; i < charSequence.length; i++){
            charSequence[i] = Character.toLowerCase(charSequence[i]);
          }
        }catch (Exception e){
            return "";
        }

        return String.valueOf(charSequence);
    }
}