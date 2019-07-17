package com.mobile.proisa.fotosvehiculoslibrary.Procesos;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.mobile.proisa.fotosvehiculoslibrary.BaseDeDatos.SqlConnection;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.Constantes;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.MetodosEstaticos;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public abstract class TareaAsincrona<Params,Progress,Result> extends AsyncTask<Params, Progress, Result>{
    private int id;             // Id que debe ser unico por proceso en una actividad
    private Activity context;   //Actividad en la que se ejecuta el proceso
    private Bundle data;        //Bundle que contiene datos que son devuelto al terminar el proceso
    private OnFinishedProcess listener;     //Listener que debe ser implementado en la clase donde haya un proceso a ejecutar
    private Stack<Exception> exceptions;    //Pila de exceptiones que son manejadas dentro del proceso
    private List<Object> noBundleableData;  //Objetos que no pueden ser puestos dentro de un Bundle de datos

    public TareaAsincrona(int id, Activity context, OnFinishedProcess listener) {
        this.id = id;
        this.context = context;
        this.listener = listener;
        this.exceptions = new Stack<>();
        this.data = new Bundle();
        this.noBundleableData = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public Activity getContext() {
        return context;
    }

    public void putData(Bundle data){ this.data = data;}

    public void addExtraData(Object object){
        this.noBundleableData.add(object);
    }

    public List<Object> getExtraData(){
       return  this.noBundleableData;
    }

    public Bundle getData() {
        return data;
    }

    public void setData(Bundle data) {
        this.data = data;
    }

    public boolean hasErrors(){return (this.exceptions.size()>0)?(true):(false);}

    @Override
    protected void onPostExecute(Result result){
        ///Si el listener está implementado y el proceso termina los metodos siguientes son llamados
        if(listener != null){
            listener.onFinishedProcess(this);

            //Si el numero de excepciones es mayor a 0 llamar este metodo
            if(exceptions.size() > 0){
                listener.onErrorOccurred(getId(),exceptions);
            }
        }
    }
    /*Añade una excepcion a la pila de excepciones*/
    protected void publishError(Exception e){
        exceptions.add(e);
    }



    //Interfaz que debe ser implementada en las actividades don corre uno o varios procesos
    public interface OnFinishedProcess{
        /*Metodo que es llamado luego de que una tarea finalice independiente de si ocurrio un
        * error o no*/
        void onFinishedProcess(TareaAsincrona task);
        //Este metodo es llamado cuando termina el proceso y hay excepciones por leer en la pila de excepciones
        void onErrorOccurred(int id, Stack<Exception> exceptions);
    }


    protected SqlConnection.DbData getCurrentDatabase(){
        SharedPreferences preferences = getContext().getSharedPreferences(Constantes.PREF_CURR_DB, Context.MODE_PRIVATE);
        String mCurrentDatabasePref = null;

        if(preferences == null){
            mCurrentDatabasePref = Constantes.PREFERENCES_DATA_BASE;
        }else{
            mCurrentDatabasePref = preferences.getString(Constantes.PREF_CURR_DB_KEY, Constantes.PREFERENCES_DATA_BASE);
        }


        return MetodosEstaticos.obtenerPreferenciasBaseDeDatos(getContext().getSharedPreferences(mCurrentDatabasePref, Context.MODE_PRIVATE));
    }


}