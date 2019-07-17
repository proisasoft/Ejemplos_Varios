package com.example.dionicio.appcotizacion.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dionicio.appcotizacion.Clases.Precotizacion;
import com.example.dionicio.appcotizacion.R;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class AdaptadorPrecotizaciones extends BaseAdapter {
    private ArrayList<Precotizacion> precotizacionesList;
    private Context contexto;
    private int layoutResource;

    //Constantes
    public static final String RD_SYMBOL = "RD$ ";

    public AdaptadorPrecotizaciones(Context contexto,int layoutResource, ArrayList<Precotizacion> precotizacionesList) {
        this.precotizacionesList = precotizacionesList;
        this.contexto = contexto;
        this.layoutResource = layoutResource;
    }

    @Override
    public int getCount() {
        return precotizacionesList.size();
    }

    @Override
    public Object getItem(int i) {
        return precotizacionesList.get(i);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        TextView lblDocumento, lblNombre, lblTotal, lblFecha;

        convertView = LayoutInflater.from(contexto).inflate(layoutResource,null);

        lblDocumento = convertView.findViewById(R.id.lbl_documento);
        lblNombre = convertView.findViewById(R.id.lbl_nombre_cliente);
        lblTotal = convertView.findViewById(R.id.lbl_total);
        lblFecha = convertView.findViewById(R.id.lbl_fecha);

        lblDocumento.setText(precotizacionesList.get(i).getDocumento());
        lblNombre.setText(precotizacionesList.get(i).getNombreCliente());
        lblFecha.setText(formatFecha(precotizacionesList.get(i).getFecha()));
        lblTotal.setText(RD_SYMBOL+formatNumber(precotizacionesList.get(i).getTotal()));

        return convertView;
    }

    public String formatFecha(Date fecha){
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

        return dateFormat.format(fecha);
    }

    public String formatNumber(double numero){
        DecimalFormat decimalFormat = new DecimalFormat("###,##0.00");

        return decimalFormat.format(numero);
    }
}
