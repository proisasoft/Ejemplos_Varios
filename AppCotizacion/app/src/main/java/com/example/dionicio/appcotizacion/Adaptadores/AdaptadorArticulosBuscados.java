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


public class AdaptadorArticulosBuscados extends BaseAdapter {
    private ArrayList<Articulo> articulosList;
    private Context contexto;
    private int layoutResource;


    public AdaptadorArticulosBuscados(Context contexto, int layoutResource, ArrayList<Articulo> articulos) {
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
        TextView lblCodigo, lblDescripcion, lblPrecio;

        convertView = LayoutInflater.from(contexto).inflate(layoutResource,null);

        lblCodigo = convertView.findViewById(R.id.lbl_codigo);
        lblDescripcion = convertView.findViewById(R.id.lbl_descripcion);
        lblPrecio = convertView.findViewById(R.id.lbl_precio);


        lblCodigo.setText(articulosList.get(i).getCodigo());
        lblDescripcion.setText(articulosList.get(i).getNombre());
        lblPrecio.setText("RD$ "+formatNumber(articulosList.get(i).getPrecio()));


        return convertView;
    }

    public String formatNumber(double numero){
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");

        return decimalFormat.format(numero);
    }
}
