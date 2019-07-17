package com.example.administrador.capturarfoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConexionSQL {

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
           con = DriverManager.getConnection("jdbc:jtds:sqlserver://PROISAVPN3.DDNS.NET:1433;databaseName=FACFOXSQL;user=sa;password=pr0i$$a;");
            //con = DriverManager.getConnection("jdbc:jtds:sqlserver://10.0.0.223;databaseName=FACFOXSQL;user=sa;password=pr0i$$a;");
            //con = DriverManager.getConnection("jdbc:jtds:sqlserver://148.255.119.90:3389;databaseName=FACFOXSQL;user=sa;password=pr0i$$a;");
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
        Statement sqlcon;
        ResultSet resultSet;
        try
        {
            if(myConnection == null)
                return false;

            sqlcon = myConnection.createStatement();
            //Ejecutamos la consulta para traer datos
            resultSet = sqlcon.executeQuery(Query);

            if(resultSet.next()){
                return true;
            }

        }catch (SQLException e){
            return false;
        }
        return false;
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

    public boolean escribeImagenEnBD(String dirArchivo, String Query) {

        boolean respuesta = false;

        try {
            File fichero = new File(dirArchivo);
            FileInputStream streamEntrada = new FileInputStream(fichero);

            PreparedStatement pstmt = myConnection.prepareStatement(Query);

            pstmt.setInt(1, obtenerNewId("pruebaImagen","id_imagen"));

            pstmt.setBinaryStream(2, streamEntrada, (int)fichero.length());
            pstmt.executeUpdate();
            pstmt.close();

            streamEntrada.close();
            respuesta = true;
        }
        catch(Exception e) {
            e.printStackTrace();
            error = e.toString();
        }
        return respuesta;
    }

    public Bitmap obtenerImagen(String Query)
    {
        Statement sqlcon;
        ResultSet resultSet;
        InputStream inputStream;//Flujo de entrada
        Bitmap  bitmap = null;

        try
        {
            if(myConnection == null)
                return null;

            sqlcon = myConnection.createStatement();

            resultSet = sqlcon.executeQuery(Query);

            if(!resultSet.next()){
                return null;
            }

            inputStream = resultSet.getBinaryStream("ar_imagen");

            int len = inputStream.available();

            byte[] arrByte = new byte[len];


            bitmap = BitmapFactory.decodeStream(inputStream);

            resultSet.close();

        }catch (SQLException e){error = e.toString();} catch (IOException e) {
            error =error+" "+e.toString();
        }

        return bitmap;
    }

    public int obtenerNewId(String tabla, String campo)
    {
        String strFormatQuery = String.format("SELECT MAX(%s) FROM %s",campo,tabla);
        int newId = 0;
        Statement sqlcon;
        ResultSet resultSet;
        try
        {
            if(myConnection == null)
                return newId;

            sqlcon = myConnection.createStatement();

            resultSet = sqlcon.executeQuery(strFormatQuery);

            if(resultSet.next())
                newId = resultSet.getInt(1)+1;

        }catch (SQLException e){}

        return newId;
    }

    public int obtenerUltimoId(String tabla, String campo)
    {
        String strFormatQuery = String.format("SELECT MAX(%s) FROM %s",campo,tabla);
        int newId = 0;
        Statement sqlcon;
        ResultSet resultSet;
        try
        {
            if(myConnection == null)
                return newId;

            sqlcon = myConnection.createStatement();

            resultSet = sqlcon.executeQuery(strFormatQuery);

            if(resultSet.next())
                newId = resultSet.getInt(1);

        }catch (SQLException e){}

        return newId;
    }


    public String obtenerError(){
        return error;
    }


}
