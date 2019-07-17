package com.example.dionicio.appcotizacion.Clases;

import java.io.Serializable;
import java.util.ArrayList;

public class Cliente extends Persona implements Serializable{
    private double limiteCredito;
    private double balance;
    private ArrayList<Factura> facturas;
    private String ncf;


    public Cliente() {
    }

    public Cliente(String nombre, String codigo, String telefono, double limiteCredito, double balance) {
        super(nombre, codigo, telefono);
        this.limiteCredito = limiteCredito;
        this.balance = balance;
    }

    public double getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(double limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public void setFacturas(ArrayList<Factura> facturas) {
        this.facturas = facturas;
    }

    public ArrayList<Factura> getFacturas() {
        return facturas;
    }

    public String getNcf() {
        return ncf;
    }

    public void setNcf(String ncf) {
        this.ncf = ncf;
    }

    @Override
    public String toString() {
        return String.format("Nombre=%s, Telefono=%s, RNC=%s, NCF=%s",
                getNombre(),getTelefono(),getRNC(),getNcf());
    }
}