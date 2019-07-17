package com.example.pethoalpar.zxingexample;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQL{

    private Connection myConnection;
    private String error ="";
    public ConexionSQL(){
        myConnection = conectarSQL();

        if (myConnection == null)
            error = "No se pudo acceder a la BD";
    }
    public ConexionSQL(String Server, String DataBase, String User, String Pass)
    {
        myConnection = conectarSQL(Server,DataBase,User,Pass);

        if (myConnection == null)
            error = "No se pudo acceder a la BD";
    }

   public void conectar(){
       try {
           if(myConnection.isClosed())
               myConnection = conectarSQL();
       } catch (SQLException e) {
           e.printStackTrace();
       }
   }

    private Connection conectarSQL()
    {
        Connection con;
        try{
            StrictMode.ThreadPolicy policy = new  StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();

            //148.255.94.76--IP_SERVER
            //con = DriverManager.getConnection("jdbc:jtds:sqlserver://PROISAVPN.DDNS.NET:1433;databaseName=FACFOXSQL;user=sa;password=pr0i$$a;");
            con = DriverManager.getConnection("jdbc:jtds:sqlserver://192.227.116.66:1433;databaseName=FACFOXSQL;user=sa;password=pr0i$$a;");
        } catch (SQLException e){
            return null;
        } catch (Exception e){
            return null;
        }

        return con;
    }

   private Connection conectarSQL(String Server, String DataBase, String User, String Pass){

        Connection con;
        String str_con = String.format("jdbc:jtds:sqlserver://%s;databaseName=%s;user=%s;password=%s;",Server,DataBase,User,Pass);
        try{
            StrictMode.ThreadPolicy policy = new  StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(str_con);
        }catch (Exception e){
            return null;
        }

        return con;
   }

    public ResultSet consulta(String Query)
    {
        Statement sqlcon;
        ResultSet resultSet;
        try
        {
            if(myConnection == null)
                return null;

            sqlcon = myConnection.createStatement();
            //Ejecutamos la consulta para traer datos
            resultSet = sqlcon.executeQuery(Query);


            //Devolvemos los resultados traidos de la BD
            return resultSet;
        }catch (SQLException e){
            return  null;
        }

    }

    public int comando(String Query)
    {
        PreparedStatement cmd_sql;

        if(myConnection == null)
            return 0;

        try
        {
            int reg_afec;//Cantidad de registros afectados

            cmd_sql = myConnection.prepareStatement(Query);

            reg_afec = cmd_sql.executeUpdate();

            return reg_afec;

        }catch (SQLException e){
            return  0;
        }
    }

    public boolean verificarExistencia(String Query)
    {
        PreparedStatement cmd_sql;

        try
        {
            int reg_afec;//Cantidad de registros afectados

            if(myConnection == null)
                return false;

            cmd_sql = myConnection.prepareStatement(Query);

            reg_afec = cmd_sql.executeUpdate();

            if (reg_afec!=0)
                return true;
            else
                return false;

        }catch (SQLException e){
            return  false;
        }
    }

    public void desconectarSQL() {
        closeConexion(myConnection);
    }

    public boolean conexionEstaCerrada()
    {
        try {
            return myConnection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        //return false;
    }

    public boolean conexionEsNula(){return myConnection == null;}

    public static boolean hayConexion(Context contexto)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager)contexto.getSystemService(contexto.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        return networkInfo != null && networkInfo.isAvailable() && networkInfo.isConnected();
    }

    private void closeConexion(Connection connection)
    {
        try{
            connection.close();
        }catch (SQLException e){
            return;
        }
    }

    public String obtenerError(){
        return error;
    }


}
