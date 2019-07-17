package com.mobile.proisa.fotosvehiculoslibrary.Clases;

import android.os.Environment;

import java.io.File;
import java.util.Locale;

public class Constantes {
    public static final String TEMP_IMAGENES = Environment.getExternalStorageDirectory().toString()
                                                .concat(String.format(Locale.getDefault(),"%s%s",File.separator,".Proisa/temp"));
    public static final String VEHICULO = "vehiculo";
    public static final String ACTIVITY_INFO = "info_activity";
    public static final int CODE_FOTO_CAMERA_ACTIVITY = 200;
    public static final int CODE_FOTO_GALLERY_ACTIVITY = 201;
    public static final String VEHICULO_LIST = "vehiculo_list";
    public static final String PREFERENCES_DATA_BASE = "base_de_datos";
    public static final String PREFERENCES_DATA_BASE_2 = "base_de_datos2";
    public static final int QUALITY_LESS_PHOTO = 15;
    public static final int CODE_AJUSTES_ACTIVITY = 128;
    public static final String PREF_CURR_DB = "bd_actual";
    public static final String PREF_CURR_DB_KEY = "bd_actual_key";
}
