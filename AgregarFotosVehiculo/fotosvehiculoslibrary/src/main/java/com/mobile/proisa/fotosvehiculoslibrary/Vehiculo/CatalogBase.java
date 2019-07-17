package com.mobile.proisa.fotosvehiculoslibrary.Vehiculo;

import android.os.Parcel;
import android.os.Parcelable;

public  class CatalogBase implements Parcelable{
    private String codigo;
    private String nombre;

    public CatalogBase(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }

    protected CatalogBase(Parcel in) {
        codigo = in.readString();
        nombre = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(codigo);
        dest.writeString(nombre);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<CatalogBase> CREATOR = new Creator<CatalogBase>() {
        @Override
        public CatalogBase createFromParcel(Parcel in) {
            return new CatalogBase(in);
        }

        @Override
        public CatalogBase[] newArray(int size) {
            return new CatalogBase[size];
        }
    };

    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean isFromDb() {
        return false;
    }
}
