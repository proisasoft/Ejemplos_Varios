package com.mobile.proisa.fotosvehiculoslibrary.Fragmentos;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.mobile.proisa.fotosvehiculoslibrary.Adaptadores.RecyclerViewAdapter;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.MyMediaScanner;
import com.mobile.proisa.fotosvehiculoslibrary.R;

import java.util.ArrayList;

public class GalleryFragment extends Fragment implements RecyclerViewAdapter.OnItemClickListener {
    private static final String URIS_KEY = "fotos";
    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;
    private ArrayList<Uri> fotos;

    public GalleryFragment() { }

    public static GalleryFragment newInstance(ArrayList<Uri> uris) {

        Bundle args = new Bundle();
        args.putParcelableArrayList(URIS_KEY, uris);
        GalleryFragment fragment = new GalleryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            this.fotos = getArguments().getParcelableArrayList(URIS_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecycleView = view.findViewById(R.id.recycler_view);
        showFotos();
    }

    private void showFotos(){
        manager = new GridLayoutManager(getActivity(), 3);
        adapter = new RecyclerViewAdapter(fotos, R.layout.recycler_view_item,this);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setLayoutManager(manager);
        mRecycleView.setAdapter(adapter);
    }

    public void setFotos(ArrayList<Uri> fotos) {
        this.fotos = fotos;
        refresh();
    }

    public void refresh(){
        if(isAdded()){
            if(adapter == null)
                showFotos();
            else
                adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(final Uri uri, int posicion) {
        MyMediaScanner scanner = new MyMediaScanner(getActivity(),uri);
    }
}
