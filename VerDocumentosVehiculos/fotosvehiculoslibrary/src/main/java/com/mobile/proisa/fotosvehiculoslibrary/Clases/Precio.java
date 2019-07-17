package com.mobile.proisa.fotosvehiculoslibrary.Clases;

import java.io.Serializable;

public class Precio implements Serializable {
    private double precio;

    public Precio(double precio) {
        this.precio = precio;
    }

    public double getPrecio() {
        return precio;
    }

    @Override
    public String toString() {
        return "RD$ "+MetodosEstaticos.formatNumber(precio,MetodosEstaticos.FORMATO_NUMBER_INTEGER);
    }
}
