package com.example.dionicio.appcotizacion.Plantillas;


import com.example.dionicio.appcotizacion.Clases.Articulo;
import com.example.dionicio.appcotizacion.Clases.MetodosEstaticos;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.draw.LineSeparator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

public class PrecotizacionPDF{
    private Document documentoPdf;
    private ArrayList<Articulo> articulos;
    private String noPrecotizacion;
    private String empresa;
    private String direccion;
    private String telefono;
    private Date fecha;
    private String cliente;
    private String codigoCliente;

    public PrecotizacionPDF(ArrayList<Articulo> articulos, String empresa, String direccion, String telefono, Date fecha) {
        this.articulos = articulos;
        this.empresa = empresa;
        this.direccion = direccion;
        this.telefono = telefono;
        this.fecha = fecha;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setNoPrecotizacion(String noPrecotizacion) {
        this.noPrecotizacion = noPrecotizacion;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public void initDocument(String nombre, String ruta){
        FileOutputStream salida;
        File file;

        documentoPdf = new Document();

        if(!nombre.contains(".pdf")){
            nombre = nombre.concat(".pdf");
        }

        file = new File(ruta,nombre);

        try {
            salida = new FileOutputStream(file.getAbsolutePath());

            PdfWriter.getInstance(documentoPdf,salida);

            documentoPdf.open();
        } catch (FileNotFoundException e) {

        } catch (DocumentException e) {

        }
    }

    private void asignarTitulos(){
        Paragraph parrafo = new Paragraph();
        Font estilo = FontFactory.getFont(new Properties());

        estilo.setStyle(Font.BOLD);

        parrafo.setFont(estilo);
        parrafo.setAlignment(Element.ALIGN_CENTER);

        parrafo.add(empresa);
        parrafo.add(Chunk.NEWLINE);
        parrafo.add(direccion);
        parrafo.add(Chunk.NEWLINE);
        parrafo.add(telefono);
        parrafo.add(Chunk.NEWLINE);

        try {

            documentoPdf.add(parrafo);
            documentoPdf.add(Chunk.NEWLINE);
            documentoPdf.add(new LineSeparator());


        } catch (DocumentException e) {

        }
    }

    private  void asignarClienteDocumento(){
        Paragraph parrafo = new Paragraph();

        parrafo.add("Fecha: "+MetodosEstaticos.formatDate(fecha));
        parrafo.add(Chunk.NEWLINE);
        parrafo.add("Cliente: "+cliente);
        parrafo.add(Chunk.NEWLINE);
        parrafo.add("Código: "+codigoCliente);
        parrafo.add(Chunk.NEWLINE);
        parrafo.add("Documento: "+noPrecotizacion);
        parrafo.add(Chunk.NEWLINE);

        try {
            documentoPdf.add(parrafo);
            documentoPdf.add(new Paragraph(Chunk.NEWLINE));
            documentoPdf.add(new LineSeparator());
            documentoPdf.add(Chunk.NEWLINE);

        } catch (DocumentException e) {

        }

    }

    private void crearTabla(){
        float[] columnas = new float[]{0.12f,0.35f,0.12f,0.12f,0.145f,0.145f};
        PdfPTable tabla = new PdfPTable(columnas);
        double totalBruto, totalItbis, totalNeto;

        Font estilo = FontFactory.getFont(new Properties());

        estilo.setStyle(Font.BOLD);

        totalBruto = 0.0;
        totalItbis = 0.0;
        totalNeto = 0.0;

        tabla.setWidthPercentage(100.0f);

        tabla.addCell(new Paragraph("Código", estilo));
        tabla.addCell(new Paragraph("Descripción", estilo));
        tabla.addCell(new Paragraph("Cantidad", estilo));
        tabla.addCell(new Paragraph("Precio", estilo));
        tabla.addCell(new Paragraph("ITBIS", estilo));
        tabla.addCell(new Paragraph("Total", estilo));

        for(int i = 0; i < articulos.size(); i++){
            tabla.addCell(new Paragraph(articulos.get(i).getCodigo()));
            tabla.addCell(new Paragraph(articulos.get(i).getNombre()));
            tabla.addCell(new Paragraph(MetodosEstaticos.formatNumber(articulos.get(i).getCantidad())));
            tabla.addCell(new Paragraph(MetodosEstaticos.formatNumber(articulos.get(i).getPrecio())));
            tabla.addCell(new Paragraph(MetodosEstaticos.formatNumber(articulos.get(i).getCalculoItbis())));
            tabla.addCell(new Paragraph(MetodosEstaticos.formatNumber(articulos.get(i).getTotal())));

            totalBruto += articulos.get(i).getTotal();
            totalItbis += articulos.get(i).getCalculoItbis();
        }

        totalNeto = totalBruto + totalItbis;

        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("Total Bruto: ", estilo));
        tabla.addCell(new Paragraph(MetodosEstaticos.formatNumber(totalBruto), estilo));

        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("Total ITBIS: ", estilo));
        tabla.addCell(new Paragraph(MetodosEstaticos.formatNumber(totalItbis), estilo));

        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("", estilo));
        tabla.addCell(new Paragraph("Total Neto: ", estilo));
        tabla.addCell(new Paragraph(MetodosEstaticos.formatNumber(totalNeto), estilo));

        try {
            documentoPdf.add(tabla);
        } catch (DocumentException e) {

        }
    }

    public void guardar(){
        asignarTitulos();
        asignarClienteDocumento();
        crearTabla();

        documentoPdf.close();
    }
}
