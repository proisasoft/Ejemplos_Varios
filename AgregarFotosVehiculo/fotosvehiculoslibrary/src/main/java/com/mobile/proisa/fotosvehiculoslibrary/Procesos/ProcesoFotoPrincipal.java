package com.mobile.proisa.fotosvehiculoslibrary.Procesos;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.mobile.proisa.fotosvehiculoslibrary.BaseDeDatos.SqlConnection;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.CameraUtils;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.Constantes;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProcesoFotoPrincipal extends TareaAsincrona<String, Uri, Void> {

    public ProcesoFotoPrincipal(int id, Activity context, OnFinishedProcess listener) {
        super(id, context, listener);
    }

    @Override
    protected void onProgressUpdate(Uri... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected Void doInBackground(String... strings) {
        SqlConnection connection = new SqlConnection(getCurrentDatabase());
        Uri foto = null;

        if(connection.isConnected()){
            try {
                PreparedStatement statement = connection.preparedStatement(
                        "SELECT IMAGEN FROM PRBDVEHIG WHERE COD_EMPR=1 AND NO_DOC=? AND CONTADOR=1");
                statement.setString(1, strings[0]);

                ResultSet rs = connection.consulta(statement);

                if(rs.next()){
                    Bitmap bm = BitmapFactory.decodeStream(rs.getBinaryStream(1));

                    if(bm != null){
                        String name = strings[0].concat(CameraUtils.createTmpFileName()).concat(".jpg");
                        CameraUtils.guardarImagen(bm,name, new File(Constantes.TEMP_IMAGENES),true);

                        foto = Uri.fromFile(new File( new File(Constantes.TEMP_IMAGENES),name));
                    }

                }else{
                    return null;
                }

                Bundle data = new Bundle();
                data.putParcelable("foto",  foto);
                putData(data);

            } catch (SQLException e) {
               publishError(e);
            } catch (IOException e) {
                publishError(e);
                e.printStackTrace();
            }
        }else{
            publishError(new Exception("Servidor No Disponible"));

        }

        return null;
    }


}
