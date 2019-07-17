package com.mobile.proisa.fotosvehiculoslibrary.BaseDeDatos;

import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//Clase que  provee una conexion con una base de datos;
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

    public Connection getSqlConnection() {
        return sqlConnection;
    }

    public void connect(){

        try{
            StrictMode.ThreadPolicy policy = new  StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

            DriverManager.setLoginTimeout(30);
            sqlConnection = DriverManager.getConnection(buildConnection());
            conectado = true;
        } catch (Exception e){
            conectado = false;
        }


    }

    public boolean isConnected(){
        return this.conectado;
    }

    public ResultSet consulta(String query) throws SQLException {
        Statement sqlcon;
        ResultSet resultSet;

        sqlcon = sqlConnection.createStatement();

        resultSet = sqlcon.executeQuery(query);

        return resultSet;
    }

    public ResultSet consulta(PreparedStatement query) throws SQLException {
        ResultSet resultSet;

        resultSet = query.executeQuery();

        return resultSet;
    }
    public int comando(String query) throws SQLException {
        PreparedStatement sqlComando;

        int registrosAfectados;

        sqlComando = sqlConnection.prepareStatement(query);
        registrosAfectados = sqlComando.executeUpdate();

        return registrosAfectados;
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

    public PreparedStatement preparedStatement(String sqlQuery) throws SQLException {
        PreparedStatement ps = sqlConnection.prepareStatement(sqlQuery);

        return ps;
    }
    private String buildConnection(){
        return String.format("jdbc:jtds:sqlserver://%s:%d;databaseName=%s;user=%s;password=%s;",
                datos.getServer(),datos.getPort(),datos.getDatabase(),datos.getUser(),datos.getPassword());
    }

    /*Clase que contiene los datos del servidor de base de datos*/
    public static class DbData {
        private String server;
        private String database;
        private String user;
        private String password;
        private int port;

        public DbData() {
        }

        public DbData(String server, String database, String user, String password, int port) {
            this.server = server;
            this.database = database;
            this.user = user;
            this.password = password;
            this.port = port;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public String getDatabase() {
            return database;
        }

        public void setDatabase(String database) {
            this.database = database;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }
    }

}