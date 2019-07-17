package com.example.dionicio.appcotizacion.Clases;


import java.io.Serializable;
import java.util.Date;

public class Precotizacion implements Serializable{
    private String documento;
    private String nombreCliente;
    private String codigoCliente;
    private Date fecha;
    private double total;

    public Precotizacion() {
        documento = "";
        nombreCliente = "";
        codigoCliente = "";
        fecha = new Date();
        total = 0.0;
    }

    public String getDocumento() {
        return documento.trim();
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public String getNombreCliente() {
        return nombreCliente.trim();
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public String getCodigoCliente() {
        return codigoCliente.trim();
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente.trim();
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
