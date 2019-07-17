package com.mobile.proisa.fotosvehiculoslibrary.Vehiculo;

import android.os.Parcel;
import android.os.Parcelable;


public class Modelo extends CatalogBase implements Parcelable{
    private Marca marca;

    public Modelo(String codigo, String nombre) {
        super(codigo, nombre);
    }


    public Modelo(String codigo, String nombre, Marca marca) {
        super(codigo, nombre);
        this.marca = marca;
    }

    protected Modelo(Parcel in) {
        super(in);
        this.marca = in.readParcelable(Marca.class.getClassLoader());
    }

    public static final Creator<Modelo> CREATOR = new Creator<Modelo>() {
        @Override
        public Modelo createFromParcel(Parcel in) {
            return new Modelo(in);
        }

        @Override
        public Modelo[] newArray(int size) {
            return new Modelo[size];
        }
    };

    public Marca getMarca() {
        return marca;
    }

    public void setMarca(Marca marca) {
        this.marca = marca;
    }

    public boolean hasMarca(){return (marca != null || !marca.getCodigo().trim().equals(""));}

    @Override
    public boolean isFromDb() {
        return !getCodigo().equals("");
    }

    @Override
    public String toString() {
        return getNombre();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(getCodigo());
        parcel.writeString(getNombre());
        parcel.writeParcelable(marca, i);
    }
}
