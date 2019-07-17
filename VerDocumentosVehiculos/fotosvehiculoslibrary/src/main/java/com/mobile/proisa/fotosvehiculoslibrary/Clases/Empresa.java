package com.mobile.proisa.fotosvehiculoslibrary.Clases;

public class Empresa {
    private int id;
    private String name;

    public Empresa(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
