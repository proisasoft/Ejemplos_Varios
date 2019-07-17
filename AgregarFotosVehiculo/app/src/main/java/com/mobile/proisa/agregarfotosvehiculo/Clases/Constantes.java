package com.mobile.proisa.agregarfotosvehiculo.Clases;

import android.os.Environment;

import java.io.File;
import java.util.Locale;

public class Constantes {
    public static final String TEMP_IMAGENES = Environment.getExternalStorageDirectory().toString()
                                                .concat(String.format(Locale.getDefault(),"%s%s",File.separator,".Proisa/temp"));
    public static final String VEHICULO = "vehiculo";
    public static final String ACTIVITY_INFO = "info_activity";
    public static final int CODE_FOTO_CAMERA_ACTIVITY = 200;
}
