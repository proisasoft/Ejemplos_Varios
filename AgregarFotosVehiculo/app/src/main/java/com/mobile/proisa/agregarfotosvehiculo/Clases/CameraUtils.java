package com.mobile.proisa.agregarfotosvehiculo.Clases;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class CameraUtils {
    public static final String EXTENSION_IMAGE = ".jpg";

    //Obtener el uri de este Intent mediante : intent.getExtras().get(MediaStore.EXTRA_OUTPUT);
    //Necesita permisos de memoria
    public static Intent prepareIntentCamera(File route) throws IOException {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File foto;

        foto = File.createTempFile(createTmpFileName(),CameraUtils.EXTENSION_IMAGE,route);
        foto.delete();

        String path = foto.getAbsolutePath();

        intentCamera.putExtra("path",path);

        return intentCamera;
    }
    //create: crear directorios y subdirectorios de donde se guardara la imagen
    //Necesita permisos de memoria
    public static Intent prepareIntentCamera(File route, boolean create) throws IOException {
        Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File foto;

        if(create && !route.exists()){
            route.mkdirs();
        }

        foto = File.createTempFile(createTmpFileName(),CameraUtils.EXTENSION_IMAGE,route);
        foto.delete();

        String path = foto.getAbsolutePath();

        intentCamera.putExtra("path",path);

        return intentCamera;
    }
    //Obtiene una imagen del uri que se especifica
    //Arroja una excepcion de IOExcepcion (entrada y salida)
    public static Bitmap getImagenBitmap(ContentResolver contentResolver, Uri uri) throws IOException {
        contentResolver.notifyChange(uri,null);

        Bitmap bm = MediaStore.Images.Media.getBitmap(contentResolver,uri);

        return bm;
    }

    //Hay que tener permisos de Memoria para poder guardar
    public static boolean guardarImagen(Bitmap bm, String name, File route, boolean create) throws NullPointerException, IOException {
        FileOutputStream fos = null;
        boolean saved;

        if(create && !route.exists()) {
           route.mkdirs();
        }
        if(!name.contains(EXTENSION_IMAGE)){
            name = name.trim().concat(EXTENSION_IMAGE);
        }

        File archivo = new File(route,name);

        try {
            fos = new FileOutputStream(archivo);

            bm.compress(Bitmap.CompressFormat.JPEG, 85, fos);

            fos.flush();
            fos.close();
            fos = null;

            saved = true;
        } catch (IOException e) {
            throw  e;
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    throw e;
                }
            }
        }

        return saved;
    }

    public static InputStream getBinaryStreamFromFile(File f) throws FileNotFoundException {
        InputStream inputStream;

        inputStream = new FileInputStream(f);

        return inputStream;
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static Bitmap decodeSampledBitmapFromFile(String res,
                                                     int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(res, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(res,options);
    }

    public static String createTmpFileName(){
        return UUID.randomUUID().toString().substring(0,7);
    }

}
