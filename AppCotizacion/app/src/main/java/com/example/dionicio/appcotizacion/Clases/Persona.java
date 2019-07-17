package com.example.dionicio.appcotizacion.Clases;

import java.io.Serializable;
import java.util.Date;

public class Persona implements Serializable {
    private String nombre;
    private String direccion;
    private String codigo;
    private String telefono;
    private String RNC;
    private String codigoZona;
    private String email;
    private Date fechaIngreso;
    private Date fechaNacimiento;


    public Persona(){
        nombre ="";
        direccion ="";
        codigo ="";
        telefono ="";
        RNC = "";
        codigoZona ="";
        fechaIngreso = new Date();
        fechaNacimiento = new Date();
    }

    public Persona(String nombre, String codigo, String telefono) {
        this.nombre = nombre;
        this.codigo = codigo;
        this.telefono = telefono;

        direccion ="";
        RNC = "";
        codigoZona ="";
        fechaIngreso = new Date();
        fechaNacimiento = new Date();
    }

    public String getNombre() {
        return nombre.trim();
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion.trim();
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getCodigo() {
        return codigo.trim();
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getTelefono() {
        return telefono.trim();
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Date getFechaIngreso() {
        return fechaIngreso;
    }

    public void setFechaIngreso(Date fechaIngreso) {
        this.fechaIngreso = fechaIngreso;
    }

    public Date getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(Date fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getRNC() {
        return RNC;
    }

    public void setRNC(String RNC) {
        this.RNC = RNC;
    }

    public String getZona() {
        return codigoZona;
    }

    public void setZona(String codigoZona) {
        this.codigoZona = codigoZona;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}