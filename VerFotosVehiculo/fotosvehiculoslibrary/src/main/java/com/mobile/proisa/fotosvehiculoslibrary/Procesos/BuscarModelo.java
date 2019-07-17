package com.mobile.proisa.fotosvehiculoslibrary.Procesos;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.mobile.proisa.fotosvehiculoslibrary.BaseDeDatos.SqlConnection;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.Constantes;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.MetodosEstaticos;
import com.mobile.proisa.fotosvehiculoslibrary.R;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Marca;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Modelo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class BuscarModelo extends TareaAsincrona<Marca,Void,Void> {

    public BuscarModelo(int id, Activity context, OnFinishedProcess listener) {
        super(id, context, listener);
    }

    @Override
    protected Void doInBackground(Marca... marcas) {
        Marca forMarca = marcas[0];
        ArrayList<Modelo> modelos;

        if(forMarca != null){
            SqlConnection connection = new SqlConnection(
                    MetodosEstaticos.obtenerPreferenciasBaseDeDatos(getContext().getSharedPreferences(Constantes.PREFERENCES_DATA_BASE, Context.MODE_PRIVATE)));

            modelos = new ArrayList<>();
            modelos.add(new Modelo("","Todos"));

            if(connection.isConnected()){
                PreparedStatement preparedStatement = null;
                try {
                    preparedStatement = connection.preparedStatement("SELECT MO_CODIGO, MO_DESCRI FROM PVBDMODELO WHERE COD_EMPR=? AND MA_CODIGO =? ORDER BY 2");
                    preparedStatement.setInt(1,1);
                    preparedStatement.setString(2,forMarca.getCodigo());
                    ResultSet rs = connection.consulta(preparedStatement);

                    while(rs.next()){
                        modelos.add(new Modelo(rs.getString(1).trim(),rs.getString(2).trim()));
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Bundle data = new Bundle();
                data.putSerializable("modelos",modelos);
                putData(data);

            }else{
                publishError(new Exception(getContext().getString(R.string.servidor_no_disponible)));
            }
        }
        return null;
    }
}
