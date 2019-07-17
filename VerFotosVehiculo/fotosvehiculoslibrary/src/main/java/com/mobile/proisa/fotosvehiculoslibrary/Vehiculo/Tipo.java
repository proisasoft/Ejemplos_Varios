package com.mobile.proisa.fotosvehiculoslibrary.Vehiculo;

import android.os.Parcel;
import android.os.Parcelable;

public class Tipo extends CatalogBase implements Parcelable{
    public Tipo(String codigo, String nombre) {
        super(codigo, nombre);
    }

    protected Tipo(Parcel in) {
        this(in.readString(), in.readString());
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

    public static final Creator<Tipo> CREATOR = new Creator<Tipo>() {
        @Override
        public Tipo createFromParcel(Parcel in) {
            return new Tipo(in);
        }

        @Override
        public Tipo[] newArray(int size) {
            return new Tipo[size];
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
