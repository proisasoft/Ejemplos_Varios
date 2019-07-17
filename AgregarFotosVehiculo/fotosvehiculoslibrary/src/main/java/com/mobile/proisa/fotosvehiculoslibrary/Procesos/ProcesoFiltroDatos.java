package com.mobile.proisa.fotosvehiculoslibrary.Procesos;

import android.app.Activity;
import android.os.Bundle;

import com.mobile.proisa.fotosvehiculoslibrary.BaseDeDatos.SqlConnection;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Marca;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Tipo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ProcesoFiltroDatos extends TareaAsincrona<Void,Void,Void> {

    public ProcesoFiltroDatos(int id, Activity context, OnFinishedProcess listener) {
        super(id, context, listener);
    }

    @Override
    protected Void doInBackground(Void... voids) {
        SqlConnection connection = new SqlConnection(getCurrentDatabase());
        ArrayList<Tipo> tipos = new ArrayList<>();
        ArrayList<Marca> marcas = new ArrayList<>();

        tipos.add(new Tipo("","Todos"));
        marcas.add(new Marca("","Todas"));

        if(connection.isConnected()){
            try {
                PreparedStatement preparedStatement = connection.preparedStatement("SELECT TI_CODIGO, TI_DESCRI FROM PVBDTIPO WHERE COD_EMPR=? ORDER BY 2");
                preparedStatement.setInt(1,1);
                ResultSet rs = connection.consulta(preparedStatement);

                while(rs.next()){
                    tipos.add(new Tipo(rs.getString(1).trim(),rs.getString(2).trim()));
                }
                preparedStatement.clearParameters();
                preparedStatement = connection.preparedStatement("SELECT MA_CODIGO, MA_DESCRI FROM PVBDMARCA WHERE COD_EMPR=? ORDER BY 2");
                preparedStatement.setInt(1,1);

                rs = connection.consulta(preparedStatement);

                while(rs.next()){
                    marcas.add(new Marca(rs.getString(1).trim(),rs.getString(2).trim()));
                }

                Bundle data = new Bundle();
                data.putSerializable("tipos",tipos);
                data.putSerializable("marcas",marcas);

                putData(data);
            } catch (SQLException e) {
                publishError(e);
            }
        }else{
         publishError(new Exception("Servidor No Disponible"));
        }

        return null;
    }



}
