package com.example.dionicio.appcotizacion.Adaptadores;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.dionicio.appcotizacion.R;

import java.util.ArrayList;


public class AdaptadorClientes extends BaseAdapter {
    private ArrayList<String> nombres;
    private ArrayList<String> codigos;
    private Context contexto;
    private int layoutResource;


    public AdaptadorClientes(Context contexto, int layoutResource, ArrayList<String> nombres, ArrayList<String> codigos) {
        this.nombres = nombres;
        this.codigos = codigos;
        this.contexto = contexto;
        this.layoutResource = layoutResource;
    }

    @Override
    public int getCount() {
        return nombres.size();
    }

    @Override
    public Object getItem(int i) {
        return nombres.get(i);
    }

    @Override
    public long getItemId(int id) {
        return id;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        TextView lblCodigo, lblNombre;

        convertView = LayoutInflater.from(contexto).inflate(layoutResource,null);

        lblCodigo = convertView.findViewById(R.id.lbl_codigo);
        lblNombre = convertView.findViewById(R.id.lbl_nombre);

        lblCodigo.setText(codigos.get(i));
        lblNombre.setText(nombres.get(i));

        return convertView;
    }
}
