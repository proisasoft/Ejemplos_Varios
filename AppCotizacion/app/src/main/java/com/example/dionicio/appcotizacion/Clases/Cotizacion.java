package com.example.dionicio.appcotizacion.Clases;

import java.io.Serializable;
import java.util.ArrayList;

public class Cotizacion implements Serializable{
    private Cliente cliente;
    private Evento evento;
    private String numero;
    private ArrayList<Articulo> articulos;

    public Cotizacion(Cliente cliente, Evento evento, String numero) {
        this.cliente = cliente;
        this.evento = evento;
        this.numero = numero;
    }

    public Cotizacion(Cliente cliente, Evento evento) {
        this.cliente = cliente;
        this.evento = evento;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public ArrayList<Articulo> getArticulos() {
        return articulos;
    }

    public void setArticulos(ArrayList<Articulo> articulos) {
        this.articulos = articulos;
    }
}