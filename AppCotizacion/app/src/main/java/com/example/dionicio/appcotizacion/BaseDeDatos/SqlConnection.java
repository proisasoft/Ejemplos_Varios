package com.example.dionicio.appcotizacion.BaseDeDatos;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SqlConnection {
    private DbData datos;
    private Connection sqlConnection;
    private boolean conectado;

    public SqlConnection(){

    }

    public SqlConnection(String server, String database, String user, String password, int port) {
        datos = new DbData(server,database,user,password,port);
         this.connect();
    }

    public SqlConnection(DbData datos) {
        this.datos = datos;
         this.connect();
    }

    public void setDatos(DbData datos) {
        this.datos = datos;
    }

    public DbData getDatos() {
        return datos;
    }

    public void connect(){

        try{
            StrictMode.ThreadPolicy policy = new  StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

            sqlConnection = DriverManager.getConnection(buildConnection());
            conectado = true;
        } catch (Exception e){
            conectado = false;
        }


    }

    public boolean isConnected(){
        return this.conectado;
    }

    public ResultSet consulta(String query)
    {
        Statement sqlcon;
        ResultSet resultSet;
        try
        {
            sqlcon = sqlConnection.createStatement();

            resultSet = sqlcon.executeQuery(query);

            return resultSet;
        }catch (SQLException e){
            return  null;
        }
    }

    public int comando(String query)
    {
        PreparedStatement sqlComando;

        try {
            int registrosAfectados;

            sqlComando = sqlConnection.prepareStatement(query);
            registrosAfectados = sqlComando.executeUpdate();

            return registrosAfectados;

        }catch (SQLException e){
            rollback();
            return  0;
        }
    }

    public void rollback(){
        try {
            sqlConnection.rollback();
        } catch (SQLException e) {

        }
    }

    public void commit(){
        try {
            sqlConnection.commit();
        } catch (SQLException e) {

        }
    }

    private String buildConnection(){
        return String.format("jdbc:jtds:sqlserver://%s:%d;databaseName=%s;user=%s;password=%s;",
                datos.getServer(),datos.getPort(),datos.getDatabase(),datos.getUser(),datos.getPassword());
    }

}