package com.example.dionicio.appcotizacion.Clases;

import java.io.Serializable;
import java.util.Date;

public class Factura implements Serializable{
    private String numero;
    private String ncf;
    private int dias;
    private double balance;
    private double monto;
    private double credito;
    private Date fecha;

    public Factura() {
    }

    public Factura(String numero, int dias, double balance, double monto) {
        this.numero = numero;
        this.dias = dias;
        this.balance = balance;
        this.monto = monto;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getNcf() {
        return ncf;
    }

    public void setNcf(String ncf) {
        this.ncf = ncf;
    }

    public int getDias() {
        return dias;
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public double getCredito() {
        return credito;
    }

    public void setCredito(double credito) {
        this.credito = credito;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public double getAbonado(){
        return monto - balance;
    }
}