package com.example.dionicio.appcotizacion.Clases;


import java.io.Serializable;

public class Articulo implements Serializable{
    private String codigo;
    private String nombre;
    private double itbis;
    private double precio;
    private double cantidad;

    public static final char ITBIS_NORMAL = 'S';
    public static final char ITBIS_TRANSITORIO = 'T';

    public Articulo() {
    }

    public Articulo(String codigo, String nombre, double itbis, double precio, double cantidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.itbis = itbis;
        this.precio = precio;
        this.cantidad = cantidad;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCodigo() {
        return codigo.trim();
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre.trim();
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getItbis() {
        return itbis;
    }

    public void setItbis(double itbis) {
        this.itbis = itbis;
    }

    public double getTotal() {
        return cantidad*precio;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getCalculoItbis(){
        return (cantidad*precio)*(itbis/100.0);
    }
}
