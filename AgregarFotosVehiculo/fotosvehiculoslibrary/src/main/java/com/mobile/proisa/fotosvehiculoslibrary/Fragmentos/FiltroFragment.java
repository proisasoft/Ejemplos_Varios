package com.mobile.proisa.fotosvehiculoslibrary.Fragmentos;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobile.proisa.fotosvehiculoslibrary.Clases.MetodosEstaticos;
import com.mobile.proisa.fotosvehiculoslibrary.Clases.Precio;
import com.mobile.proisa.fotosvehiculoslibrary.Interfaces.OnTapListener;
import com.mobile.proisa.fotosvehiculoslibrary.Procesos.BuscarModelo;
import com.mobile.proisa.fotosvehiculoslibrary.Procesos.TareaAsincrona;
import com.mobile.proisa.fotosvehiculoslibrary.R;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Marca;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Modelo;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Tipo;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Vehiculo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;


public class FiltroFragment extends Fragment implements AdapterView.OnItemSelectedListener, TareaAsincrona.OnFinishedProcess{
    private Spinner spTipo, spMarca, spModelo;
    private Spinner spEstado, spPrecio1,spPrecio2, spYear1, spYear2;
    private Spinner spCombustible, spTransmision, spTraccion, spColor;
    private TextView btnBuscar;
    private static final int YEAR_MINIMUM = 1975;
    private double PRECIO_INCREMENTO;
    private double PRECIO_LIMITE;

    private ArrayList<Tipo> tipos;
    private ArrayList<Marca> marcas;
    private ArrayList<Modelo> modelos;


    private OnTapListener onTapListener;
    private SearchListener searchListener;
    private String filtro = "";
    private static final int ID_BUSQUEDA_MODELOS = 1;

    public FiltroFragment() {
    }

    public static FiltroFragment newInstance(SearchListener searchListener) {
        Bundle args = new Bundle();
        FiltroFragment fragment = new FiltroFragment();
        fragment.setArguments(args);

        fragment.setSearchListener(searchListener);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null){
            if(getArguments().size() > 0){

            }else{
                PRECIO_INCREMENTO = 25000;
                PRECIO_LIMITE = 2000000;
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filtro, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btnBuscar = view.findViewById(R.id.btn_buscar);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               callSearch();
            }
        });
        spYear1 = view.findViewById(R.id.sp_year_1);
        spYear1.setAdapter(years());
        spYear1.setOnItemSelectedListener(this);

        spYear2 = view.findViewById(R.id.sp_year_2);
        spYear2.setAdapter(years());
        spYear2.setOnItemSelectedListener(this);

        spPrecio1 = view.findViewById(R.id.sp_precio_1);
        spPrecio1.setAdapter(precios());
        spPrecio1.setOnItemSelectedListener(this);

        spPrecio2 = view.findViewById(R.id.sp_precio_2);
        spPrecio2.setAdapter(precios());
        spPrecio2.setOnItemSelectedListener(this);

        spEstado = view.findViewById(R.id.sp_estado);
        spEstado.setAdapter(estados());
        spEstado.setOnItemSelectedListener(this);

        spTipo = view.findViewById(R.id.sp_tipo);

        if(tipos != null)
        spTipo.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,tipos));

        spMarca = view.findViewById(R.id.sp_marca);

        if(marcas != null)
        spMarca.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,marcas));

        spMarca.setOnItemSelectedListener(this);

        spModelo = view.findViewById(R.id.sp_modelo);
    }

    private void callSearch(){
        filtro = "";

        if(spYear1.getSelectedItemPosition() != spYear2.getSelectedItemPosition())
            filtro = filtro.concat(String.format("AND VE_ANO>=%d AND VE_ANO <= %d\n",spYear1.getSelectedItem(),spYear2.getSelectedItem()));

        if(getEstado() != '\0')
        filtro = filtro.concat(String.format("AND VE_ESTADO='%c'\n",getEstado()));

        Modelo m = (Modelo) spModelo.getSelectedItem();

        try {
            if (m.isFromDb()) {
                filtro = filtro.concat(String.format("AND VEHI.VE_MODELO='%s'\n", m.getCodigo()));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        Tipo t = (Tipo) spTipo.getSelectedItem();

        try {
            if(t.isFromDb()){
                filtro = filtro.concat(String.format("AND VEHI.VE_CODTIP='%s'\n",t.getCodigo()));
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }

        Marca ma = (Marca)spMarca.getSelectedItem();
        try{
            if(ma.isFromDb()){
                filtro = filtro.concat(String.format("AND MO.MA_CODIGO='%s'\n",ma.getCodigo()));
            }

        }catch (NullPointerException e){
            e.printStackTrace();
        }

        if(spPrecio1.getSelectedItemPosition() != spPrecio2.getSelectedItemPosition()){
            Precio p1, p2;

            p1 = (Precio) spPrecio1.getSelectedItem();
            p2 = (Precio) spPrecio2.getSelectedItem();

            if(p1.getPrecio() == 0.0){
                filtro = filtro.concat(String.format("AND VE_PREMAY>=%f\n",p2.getPrecio()));
            }else{
                filtro = filtro.concat(String.format("AND VE_PREMAY>=%f AND VE_PREMAY<=%f\n",p1.getPrecio(),p2.getPrecio()));
            }
        }

        if(searchListener != null)
            searchListener.onSerch(filtro);
    }

    private char getEstado(){
        char estadoChr;

        try{
            estadoChr = Vehiculo.Estado.valueOf(String.valueOf(spEstado.getSelectedItem())).toString().charAt(0);
        }catch (Exception e){
            estadoChr ='\0';
        }


        return estadoChr;
    }
    private ArrayAdapter<Integer> years(){
        int yearStart = MetodosEstaticos.getOfCalendar(Calendar.YEAR)+1;
        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line);

        while(yearStart >= YEAR_MINIMUM){
            arrayAdapter.add(yearStart);
            yearStart--;
        }
        return arrayAdapter;
    }

    private ArrayAdapter<String> estados(){
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line);
        String estados ="";

        for(Vehiculo.Estado e : Vehiculo.Estado.values()){
            arrayAdapter.add(e.toString());
            estados = estados.concat(e.toString()).concat("/");
        }
        estados = estados.substring(0,estados.length()-1);
        arrayAdapter.insert(estados,0);
        return arrayAdapter;
    }
    private ArrayAdapter<Precio> precios(){
        ArrayAdapter<Precio> precios;

        precios = new ArrayAdapter<Precio>(getActivity(),android.R.layout.simple_dropdown_item_1line);

        double precioBase = 0.0;
        double increment = PRECIO_INCREMENTO;

        for (int i = 1; precioBase <= PRECIO_LIMITE; i++) {
            precios.add(new Precio(precioBase));
            precioBase += increment;

            switch (i){
                case 6:
                case 11:
                case 15:
                    increment *= 2;
                    break;
                case 16:
                    increment +=300000;
                    break;
            }
        }
        return precios;
    }



    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        int pos1, pos2, count;

        int viewId = adapterView.getId();

        if (viewId == R.id.sp_year_1) {
            pos1 = spYear1.getSelectedItemPosition();
            pos2 = spYear2.getSelectedItemPosition();
            if (pos1 < pos2) {
                count = spYear2.getAdapter().getCount() - 1;
                if (pos1 == count) {
                    spYear2.setSelection(pos1);
                } else {
                    spYear2.setSelection(pos1 - 1);
                }
            }

        } else if (viewId == R.id.sp_year_2) {
            pos1 = spYear2.getSelectedItemPosition();
            pos2 = spYear1.getSelectedItemPosition();

            if (pos1 > pos2) {
                if (pos1 == 0) {
                    spYear1.setSelection(pos1);
                } else {
                    spYear1.setSelection(pos1 + 1);
                }
            }

        } else if (viewId == R.id.sp_precio_1) {
            pos1 = spPrecio1.getSelectedItemPosition();
            pos2 = spPrecio2.getSelectedItemPosition();

            if (pos1 > pos2) {
                count = spPrecio2.getAdapter().getCount() - 1;
                if (pos1 == count) {
                    spPrecio2.setSelection(pos1);
                } else {
                    spPrecio2.setSelection(pos1 + 1);
                }
            }

        } else if (viewId == R.id.sp_precio_2) {
            pos1 = spPrecio2.getSelectedItemPosition();
            pos2 = spPrecio1.getSelectedItemPosition();

            if (pos1 < pos2) {
                if (pos1 == 0) {
                    spPrecio1.setSelection(pos1);
                } else {
                    spPrecio1.setSelection(pos1 - 1);
                }
            }

        } else if (viewId == R.id.sp_marca) {
            Marca marca = (Marca) adapterView.getItemAtPosition(i);

            new BuscarModelo(ID_BUSQUEDA_MODELOS,getActivity(),this).execute(marca);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    public void setTipos(ArrayList<Tipo> data) {
        this.tipos = data;

        if(isAdded()){
            spTipo.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,tipos));
        }
    }

    public void setMarcas(ArrayList<Marca> marcas) {
        this.marcas = marcas;
        if(isAdded()) {
            spMarca.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, marcas));
        }
    }

    public void setModelos(ArrayList<Modelo> modelos) {
        this.modelos = modelos;
        if(isAdded()){
            spModelo.setAdapter(new ArrayAdapter<>(getActivity(),android.R.layout.simple_dropdown_item_1line,modelos));
        }
    }

    public void setOnTapListener(OnTapListener onTapListener) {
        this.onTapListener = onTapListener;
    }

    public void setSearchListener(SearchListener searchListener) {
        this.searchListener = searchListener;
    }

    public interface SearchListener{
        void onSerch(CharSequence searchText);
    }



    @Override
    public void onFinishedProcess(TareaAsincrona task) {
        if(!task.hasErrors()){
            switch (task.getId()){
                case ID_BUSQUEDA_MODELOS:
                    ArrayList<Modelo> modelos = task.getData().getParcelableArrayList("modelos");
                    setModelos(modelos);
                    break;
            }
        }
    }

    @Override
    public void onErrorOccurred(int id, Stack<Exception> exceptions) {
        Log.e("errorIn",exceptions.pop().getMessage());
    }
}
