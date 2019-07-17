package com.mobile.proisa.fotosvehiculoslibrary.Procesos;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import com.mobile.proisa.fotosvehiculoslibrary.BaseDeDatos.SqlConnection;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.CameraUtils;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.Constantes;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.MetodosEstaticos;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Vehiculo;

import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProcesoDescargarImagen extends TareaAsincrona<Integer, Uri, Void> {
    private Vehiculo vehiculo;
    private String query;

    public ProcesoDescargarImagen(int id, Activity context, OnFinishedProcess listener, Vehiculo vehiculo, String query) {
        super(id, context, listener);
        this.vehiculo = vehiculo;
        this.query = query;
    }

    @Override
    protected Void doInBackground(Integer... integers) {
        SqlConnection connection = new SqlConnection(MetodosEstaticos.obtenerPreferenciasBaseDeDatos(getContext().getSharedPreferences("base_de_datos", Context.MODE_PRIVATE)));

        if(vehiculo == null){
            return null;
        }
        if(connection.isConnected()){
            Uri foto = null;

            try {
                PreparedStatement statement = connection.preparedStatement(query);
                statement.setString(1,vehiculo.getId());
                statement.setInt(2, integers[0]);

                ResultSet rs = connection.consulta(statement);

                if(rs!=null){
                    if(rs.next()){

                        Bitmap bm = BitmapFactory.decodeStream(rs.getBinaryStream(1));
                        if(bm != null){
                            String name = vehiculo.getId().concat(CameraUtils.createTmpFileName()).concat(".jpg");
                            CameraUtils.guardarImagen(bm,name, new File(Constantes.TEMP_IMAGENES),true);

                            foto = Uri.fromFile(new File(new File(Constantes.TEMP_IMAGENES),name));
                        }
                    }else{
                        return null;
                    }
                }else{
                    return null;
                }

                Bundle data = new Bundle();
                data.putParcelable("foto",  foto);
                data.putInt("next",integers[0]+1);
                putData(data);

            } catch (SQLException e) {
                publishError(e);
            } catch (IOException e) {
                publishError(e);
                e.printStackTrace();
            }
        }else{
            publishError(new Exception("Servidor No disponible"));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);


    }
}
