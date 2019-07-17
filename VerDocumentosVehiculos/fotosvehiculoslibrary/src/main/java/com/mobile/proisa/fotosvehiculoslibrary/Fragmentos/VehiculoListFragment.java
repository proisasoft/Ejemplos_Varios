package com.mobile.proisa.fotosvehiculoslibrary.Fragmentos;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.proisa.fotosvehiculoslibrary.Adaptadores.VehiculoViewAdapter;
import com.mobile.proisa.fotosvehiculoslibrary.Interfaces.OnTapListener;
import com.mobile.proisa.fotosvehiculoslibrary.R;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Vehiculo;

import java.util.ArrayList;

public class VehiculoListFragment extends Fragment{
    private static final String PARAM_VEHICULOS = "param_vehiculos";

    private ArrayList<Vehiculo> vehiculos;

    private RecyclerView mRecycleView;
    private RecyclerView.LayoutManager manager;
    private RecyclerView.Adapter adapter;


    public VehiculoListFragment() {
        // Required empty public constructor
    }

    public static VehiculoListFragment newInstance(ArrayList<Vehiculo> vehiculos) {
         Bundle args = new Bundle();
         args.putParcelableArrayList(PARAM_VEHICULOS,vehiculos);
         VehiculoListFragment fragment = new VehiculoListFragment();
         fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            vehiculos = getArguments().getParcelableArrayList(PARAM_VEHICULOS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_vehiculo_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRecycleView = view.findViewById(R.id.recycler_view);
        show();
    }

    private void show(){
        VehiculoViewAdapter.OnItemClickListener listener = null;
        OnTapListener<Vehiculo> onTapListener  = null;

        if(getActivity() instanceof  VehiculoViewAdapter.OnItemClickListener ){
            listener = (VehiculoViewAdapter.OnItemClickListener) getActivity();
        }

        if(getActivity() instanceof OnTapListener){
            onTapListener = (OnTapListener<Vehiculo>) getActivity();
        }

        manager = new LinearLayoutManager(getActivity());
        adapter = new VehiculoViewAdapter(vehiculos, R.layout.vehiculo_view_item, listener, onTapListener);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setLayoutManager(manager);
        mRecycleView.setAdapter(adapter);
    }

    public void refresh(){
        if(isAdded()){
            if(adapter == null) show();
            else adapter.notifyDataSetChanged();
        }
    }


}
