package com.example.dionicio.appcotizacion.BaseDeDatos;

public class DbData {
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