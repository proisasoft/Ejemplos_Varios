package com.example.dionicio.appcotizacion.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dionicio.appcotizacion.Clases.Articulo;
import com.example.dionicio.appcotizacion.R;

import java.text.DecimalFormat;
import java.util.ArrayList;


public class AdaptadorArticulosSeleccionados extends BaseAdapter {
    private ArrayList<Articulo> articulosList;
    private Context contexto;
    private int layoutResource;


    public AdaptadorArticulosSeleccionados(Context contexto, int layoutResource, ArrayList<Articulo> articulos) {
        this.articulosList = articulos;
        this.contexto = contexto;
        this.layoutResource = layoutResource;
    }

    @Override
    public int getCount() {
        return articulosList.size();
    }

    @Override
    public Object getItem(int i) {
        return articulosList.get(i);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        TextView lblCodigo, lblDescripcion, lblCantidad, lblPrecio, lblIbtis, lblTotal;

        convertView = LayoutInflater.from(contexto).inflate(layoutResource,null);

        lblCodigo = convertView.findViewById(R.id.lbl_codigo_articulo);
        lblDescripcion = convertView.findViewById(R.id.lbl_descripcion_articulo);
        lblCantidad = convertView.findViewById(R.id.lbl_cantidad_articulo);
        lblPrecio = convertView.findViewById(R.id.lbl_precio_articulo);
        lblIbtis = convertView.findViewById(R.id.lbl_itbis_articulo);
        lblTotal= convertView.findViewById(R.id.lbl_total_articulo);

        lblCodigo.setText(articulosList.get(i).getCodigo());
        lblDescripcion.setText(articulosList.get(i).getNombre());
        lblCantidad.setText(formatNumber(articulosList.get(i).getCantidad()));
        lblPrecio.setText(formatNumber(articulosList.get(i).getPrecio()));
        lblIbtis.setText(formatNumber(articulosList.get(i).getCalculoItbis()));
        lblTotal.setText(formatNumber(articulosList.get(i).getTotal()));

        return convertView;
    }

    public String formatNumber(double numero){
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");

        return decimalFormat.format(numero);
    }
}
