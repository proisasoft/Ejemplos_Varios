package com.mobile.proisa.fotosvehiculoslibrary.Vehiculo;

import android.os.Parcel;
import android.os.Parcelable;

public class Marca extends CatalogBase implements Parcelable{

    public Marca(String codigo, String nombre) {
        super(codigo, nombre);
    }

    protected Marca(Parcel in) {
        this(in.readString(),in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getCodigo());
        dest.writeString(getNombre());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Marca> CREATOR = new Creator<Marca>() {
        @Override
        public Marca createFromParcel(Parcel in) {
            return new Marca(in);
        }

        @Override
        public Marca[] newArray(int size) {
            return new Marca[size];
        }
    };

    @Override
    public boolean isFromDb() {
        return !getCodigo().equals("");
    }

    @Override
    public String toString() {
        return getNombre();
    }
}
