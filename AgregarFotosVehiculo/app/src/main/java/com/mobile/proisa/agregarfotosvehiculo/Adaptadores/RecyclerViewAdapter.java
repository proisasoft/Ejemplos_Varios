package com.mobile.proisa.agregarfotosvehiculo.Adaptadores;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.mobile.proisa.agregarfotosvehiculo.Clases.CameraUtils;
import com.mobile.proisa.agregarfotosvehiculo.R;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private List<Uri> fotos;
    private int layoutResource;
    private OnItemClickListener listener;

    public RecyclerViewAdapter(ArrayList<Uri> fotos, int layoutResource, OnItemClickListener listener) {
        this.fotos = fotos;
        this.layoutResource = layoutResource;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(layoutResource,parent,false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.enlazar(fotos.get(position),listener);
    }

    @Override
    public int getItemCount() {
        return fotos.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView imagen;

        public ViewHolder(View itemView) {
            super(itemView);

            imagen = itemView.findViewById(R.id.img_contenedor);
        }

        public void enlazar(final Uri uri, final OnItemClickListener listener){
            Bitmap bm = null;

            if(uri != null)
              bm = CameraUtils.decodeSampledBitmapFromFile(uri.getPath(),200,200);

            if(bm != null){
                imagen.setImageBitmap(bm);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.onItemClick(uri,getAdapterPosition());
                    }
                });
            }


        }
    }

    public interface OnItemClickListener{
        void onItemClick(Uri uri, int posicion);
    }
}