package com.mobile.proisa.fotosvehiculoslibrary.Procesos;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import com.mobile.proisa.fotosvehiculoslibrary.BaseDeDatos.SqlConnection;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.Constantes;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.MetodosEstaticos;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Marca;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Modelo;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Tipo;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Vehiculo;

import java.sql.ResultSet;
import java.util.ArrayList;

public class ProcesoBuscarVehiculos extends TareaAsincrona<String, Void, Void> {

    public ProcesoBuscarVehiculos(int id, Activity context, OnFinishedProcess listener) {
        super(id, context, listener);
    }

    @Override
    protected Void doInBackground(String... strings) {
        SqlConnection connection = new SqlConnection(MetodosEstaticos.obtenerPreferenciasBaseDeDatos(getContext().getSharedPreferences("base_de_datos", Context.MODE_PRIVATE)));
        ArrayList<Vehiculo> vehiculos = new ArrayList<Vehiculo>();

        if (connection.isConnected()) {
            try {
                ResultSet rs = connection.consulta(strings[0]);
                Vehiculo vehiculo;

                while (rs.next()) {
                    vehiculo = new Vehiculo(rs.getString("ve_codigo").trim());

                    vehiculo.setYear(rs.getInt("ve_ano"));
                    vehiculo.setTransmision(rs.getString("trn_descri"));
                    vehiculo.setTraccion(rs.getString("trc_descri"));

                    Marca marca = new Marca(rs.getString("ma_codigo"), rs.getString("ma_descri"));
                    vehiculo.setModelo(new Modelo(rs.getString("mo_codigo"), rs.getString("mo_descri"), marca));
                    vehiculo.setTipo(new Tipo(rs.getString("ti_codigo"), rs.getString("ti_descri")));
                    vehiculo.setColor(rs.getString("co_descri"));

                    double[] precios = {rs.getDouble("VE_PREMAY"), rs.getDouble("VE_PREMIN"), rs.getDouble("VE_PREMAY")};

                    vehiculo.setPrecios(precios);

                    String estado = rs.getString("VE_ESTADO");

                    if (estado.equals("N")) {
                        vehiculo.setEstado(Vehiculo.Estado.NUEVO);
                    } else {
                        vehiculo.setEstado(Vehiculo.Estado.USADO);
                    }

                    vehiculo.setNumeroChasis(rs.getString("VE_NUMCHA").trim());
                    vehiculo.setPlaca(rs.getString("VE_PLACA").trim());
                    vehiculos.add(vehiculo);
                }
            } catch (Exception e) {
                publishError(e);
            }
            //Set de data
            Bundle bundle = new Bundle();
            bundle.putSerializable(Constantes.VEHICULO_LIST, vehiculos);
            putData(bundle);
        }else{
            publishError(new Exception("Servidor no disponible"));
        }


        return null;
    }
}
