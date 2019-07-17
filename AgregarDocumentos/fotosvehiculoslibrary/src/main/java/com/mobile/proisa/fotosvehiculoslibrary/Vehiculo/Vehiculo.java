package com.mobile.proisa.fotosvehiculoslibrary.Vehiculo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.proisa.fotosvehiculoslibrary.Clases.MetodosEstaticos;

import java.util.ArrayList;

public class Vehiculo implements Parcelable {
    private String id;
    private Modelo modelo;
    private ArrayList<Uri> fotos;
    private Estado estado;
    private String color;
    private int year;
    private String transmision;
    private String traccion;
    private String numeroChasis;
    private String placa;
    private Tipo tipo;
    private double precio;
    private double[] precios;


    public Vehiculo(String id) {
        this.id = id;
        precios =  new double[3];
    }

    public Vehiculo(String id, Modelo modelo) {
        this.id = id;
        this.modelo = modelo;
        precios =  new double[3];
    }

    protected Vehiculo(Parcel in) {
        id = in.readString();
        modelo = in.readParcelable(Modelo.class.getClassLoader());
        fotos = in.createTypedArrayList(Uri.CREATOR);
        color = in.readString();
        year = in.readInt();
        transmision = in.readString();
        traccion = in.readString();
        tipo = in.readParcelable(Tipo.class.getClassLoader());
        precio = in.readDouble();

        numeroChasis = in.readString();
        placa = in.readString();
        estado = Estado.valueOf(in.readString());
        precios = in.createDoubleArray();
    }

    public static final Creator<Vehiculo> CREATOR = new Creator<Vehiculo>() {
        @Override
        public Vehiculo createFromParcel(Parcel in) {
            return new Vehiculo(in);
        }

        @Override
        public Vehiculo[] newArray(int size) {
            return new Vehiculo[size];
        }
    };

    public String getId() {
        return id;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public ArrayList<Uri> getFotos() {
        if(fotos == null) fotos = new ArrayList<>();
        return fotos;
    }

    public void setFotos(ArrayList<Uri> fotos) {
        this.fotos = fotos;
    }

    public boolean hasFotos(){return this.fotos != null? this.fotos.size()>0 : false;}

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getTransmision() {
        return transmision;
    }

    public void setTransmision(String transmision) {
        this.transmision = transmision;
    }

    public String getTraccion() {
        return traccion;
    }

    public void setTraccion(String traccion) {
        this.traccion = traccion;
    }

    public Tipo getTipo() {
        return tipo;
    }

    public void setTipo(Tipo tipo) {
        this.tipo = tipo;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getNumeroChasis() {
        return numeroChasis;
    }

    public void setNumeroChasis(String numeroChasis) {
        this.numeroChasis = numeroChasis;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setPrecios(double[] precios) {
        this.precios = precios;
    }

    public double[] getPrecios() {
        return precios;
    }

    public static String getShortTitle(Vehiculo v){
        if(v.getModelo() != null)
            return String.format("%d %s %s",v.getYear(),v.getModelo().getMarca().getNombre(), v.getModelo().getNombre());
        else
            return  "";
    }

    public static String getLongTitle(Vehiculo v){
        if(v.getModelo() != null){
            Tipo t = v.getTipo();
            Modelo m = v.getModelo();
            Marca mr = m.getMarca();

            return String.format("%s %s %s %d %s", MetodosEstaticos.convertString(t.getNombre()),
                    MetodosEstaticos.convertString(mr.getNombre()),MetodosEstaticos.convertString(m.getNombre()),v.getYear(),MetodosEstaticos.convertString(v.getColor()));
        }

        else
            return  "";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeParcelable(modelo, i);
        parcel.writeTypedList(fotos);
        parcel.writeString(color);
        parcel.writeInt(year);
        parcel.writeString(transmision);
        parcel.writeString(traccion);
        parcel.writeParcelable(tipo, i);
        parcel.writeDouble(precio);
        parcel.writeString(numeroChasis);
        parcel.writeString(placa);
        parcel.writeString(estado.name());
        parcel.writeDoubleArray(precios);
    }


    public enum Estado {NUEVO, USADO}
}
