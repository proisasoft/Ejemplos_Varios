package com.mobile.proisa.fotosvehiculoslibrary.Adaptadores;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.proisa.fotosvehiculoslibrary.Clases.CameraUtils;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.MetodosEstaticos;
import com.mobile.proisa.fotosvehiculoslibrary.Interfaces.OnTapListener;
import com.mobile.proisa.fotosvehiculoslibrary.R;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Vehiculo;

import java.util.ArrayList;
import java.util.List;

public class VehiculoViewAdapter extends RecyclerView.Adapter<VehiculoViewAdapter.ViewHolder> {
    private List<Vehiculo> vehiculos;
    private int layoutResource;
    private OnItemClickListener listener;
    private OnTapListener<Vehiculo> onTapListener;

    public VehiculoViewAdapter(ArrayList<Vehiculo> vehiculos, int layoutResource, OnItemClickListener listener) {
        this.vehiculos = vehiculos;
        this.layoutResource = layoutResource;
        this.listener = listener;
    }

    public VehiculoViewAdapter(List<Vehiculo> vehiculos, int layoutResource, OnItemClickListener listener, OnTapListener<Vehiculo> onTapListener) {
        this.vehiculos = vehiculos;
        this.layoutResource = layoutResource;
        this.listener = listener;
        this.onTapListener = onTapListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutResource,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.enlazar(vehiculos.get(position),onTapListener);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener != null)
                listener.onItemClick(vehiculos.get(position), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehiculos.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView imagen, imgMore;
        private TextView lblDescripcion;
        private TextView lblEstado;

        public ViewHolder(View itemView) {
            super(itemView);

            imagen = itemView.findViewById(R.id.img_contenedor);
            lblDescripcion = itemView.findViewById(R.id.lbl_descripcion);
            lblEstado = itemView.findViewById(R.id.lbl_estado);
            imgMore = itemView.findViewById(R.id.more_info);

        }

        public void enlazar(final Vehiculo vehiculo, final OnTapListener listener){
            Bitmap bm = null;

            if(vehiculo != null){
                if(vehiculo.hasFotos())
                    bm = CameraUtils.decodeSampledBitmapFromFile(vehiculo.getFotos().get(0).getPath(),200,200);

                lblDescripcion.setText(Vehiculo.getLongTitle(vehiculo));
                lblEstado.setText(MetodosEstaticos.convertString(vehiculo.getEstado().name()));
            }

            if(bm != null){
                imagen.setImageBitmap(bm);
            }

            imgMore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null)
                    listener.OnTapItem(vehiculo);
                }
            });


        }
    }

    public interface OnItemClickListener{
        void onItemClick(Vehiculo v, int posicion);
    }
}