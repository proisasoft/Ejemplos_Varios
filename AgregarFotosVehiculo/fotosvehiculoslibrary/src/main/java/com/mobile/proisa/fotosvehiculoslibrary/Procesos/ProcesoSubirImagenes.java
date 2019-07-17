package com.mobile.proisa.fotosvehiculoslibrary.Procesos;

import android.app.Activity;
import android.net.Uri;
import android.widget.Toast;

import com.mobile.proisa.fotosvehiculoslibrary.BaseDeDatos.SqlConnection;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.CameraUtils;
import com.mobile.proisa.fotosvehiculoslibrary.R;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Vehiculo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProcesoSubirImagenes extends TareaAsincrona<Uri,Integer,Void> {
    private Vehiculo vehiculo;

    public ProcesoSubirImagenes(int id, Activity context, OnFinishedProcess listener, Vehiculo vehiculo) {
        super(id, context, listener);
        this.vehiculo = vehiculo;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        Toast.makeText(getContext(),String.format("Subiendo Imagenes: %d/%d",values[0],values[1]), Toast.LENGTH_SHORT).show();
    }

    @Override
    protected Void doInBackground(Uri... fotos) {
        SqlConnection connection = new SqlConnection(getCurrentDatabase());

        if(connection.isConnected()){
            try {
                PreparedStatement preparedStatement = connection.preparedStatement("DELETE PRBDVEHIG WHERE NO_DOC=?");
                preparedStatement.setString(1,vehiculo.getId());
                preparedStatement.executeUpdate();


                preparedStatement = connection.preparedStatement("INSERT INTO PRBDVEHIG  (COD_EMPR, COD_SUCU, NO_DOC,IMAGEN, TIPO,CONTADOR) VALUES (1,1,?,?,?,?)");

                for(int i = 0; i < vehiculo.getFotos().size(); i++){
                    Uri uri = vehiculo.getFotos().get(i);

                    File f = new File(uri.getPath());
                    InputStream stream = CameraUtils.getBinaryStreamFromFile(f);

                    preparedStatement.setString(1,vehiculo.getId());
                    preparedStatement.setBinaryStream(2,stream,(int)f.length());
                    preparedStatement.setString(3,"JPG");
                    preparedStatement.setInt(4,(i+1));
                    preparedStatement.executeUpdate();
                    preparedStatement.clearParameters();

                    publishProgress(i+1,vehiculo.getFotos().size());
                }
            } catch (SQLException e) {
                publishError(e);
                connection.rollback();
            } catch (FileNotFoundException e) {
                connection.rollback();
                publishError(e);
            }
        }else{
            publishError(new Exception(getContext().getString(R.string.servidor_no_disponible)));
        }

        return null;
    }
}
