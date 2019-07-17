package com.example.dionicio.appcotizacion.Clases;

import java.io.Serializable;
import java.util.Date;

public class Evento implements Serializable{
   private Date fechaHoraEvento;
   private Date fechaHoraEntrega;
   private Date fechaHoraRecogida;
   private String responsable;
   private String direccion;
   private String tipo;
   private String ciudad;

   public Evento() {
   }

   public Evento(Date fechaHoraEvento, Date fechaHoraEntrega, Date fechaHoraRecogida, String responsable, String direccion) {
      this.fechaHoraEvento = fechaHoraEvento;
      this.fechaHoraEntrega = fechaHoraEntrega;
      this.fechaHoraRecogida = fechaHoraRecogida;
      this.responsable = responsable;
      this.direccion = direccion;
   }

   public Date getFechaHoraEvento() {
      return fechaHoraEvento;
   }

   public void setFechaHoraEvento(Date fechaHoraEvento) {
      this.fechaHoraEvento = fechaHoraEvento;
   }

   public Date getFechaHoraEntrega() {
      return fechaHoraEntrega;
   }

   public void setFechaHoraEntrega(Date fechaHoraEntrega) {
      this.fechaHoraEntrega = fechaHoraEntrega;
   }

   public Date getFechaHoraRecogida() {
      return fechaHoraRecogida;
   }

   public void setFechaHoraRecogida(Date fechaHoraRecogida) {
      this.fechaHoraRecogida = fechaHoraRecogida;
   }

   public String getResponsable() {
      return responsable;
   }

   public void setResponsable(String responsable) {
      this.responsable = responsable;
   }

   public String getDireccion() {
      return direccion;
   }

   public void setDireccion(String direccion) {
      this.direccion = direccion;
   }

   public String getTipo() {
      return tipo;
   }

   public void setTipo(String tipo) {
      this.tipo = tipo;
   }

   public String getCiudad() {
      return ciudad;
   }

   public void setCiudad(String ciudad) {
      this.ciudad = ciudad;
   }

   @Override
   public String toString() {
      return String.format("Evento=%s,Entrega=%s,Recogida=%s,Responsable=%s,Direccion=%s,Tipo=%s,Ciudad=%s",
              MetodosEstaticos.formatDate(fechaHoraEvento),
              MetodosEstaticos.formatDate(fechaHoraEntrega),
              MetodosEstaticos.formatDate(fechaHoraRecogida),
              responsable,direccion,tipo,ciudad);
   }
}