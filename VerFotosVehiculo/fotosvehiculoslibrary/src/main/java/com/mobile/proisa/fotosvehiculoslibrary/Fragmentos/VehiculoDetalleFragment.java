package com.mobile.proisa.fotosvehiculoslibrary.Fragmentos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobile.proisa.fotosvehiculoslibrary.Clases.MetodosEstaticos;
import com.mobile.proisa.fotosvehiculoslibrary.R;
import com.mobile.proisa.fotosvehiculoslibrary.Vehiculo.Vehiculo;


public class VehiculoDetalleFragment extends Fragment {
    private static final String PARAM_VEHICULO = "param_vehiculo";
    private Vehiculo vehiculo;

    public VehiculoDetalleFragment() { }

    public static VehiculoDetalleFragment newInstance(Vehiculo vehiculo) {
        VehiculoDetalleFragment fragment = new VehiculoDetalleFragment();
        Bundle args = new Bundle();
        args.putParcelable(PARAM_VEHICULO,vehiculo);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehiculo = getArguments().getParcelable(PARAM_VEHICULO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vehiculo_detalle, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView lblEstado = view.findViewById(R.id.lbl_estado);
        lblEstado.setText(MetodosEstaticos.convertString(vehiculo.getEstado().name()));

        TextView lblTipo = view.findViewById(R.id.lbl_tipo);
        lblTipo.setText(MetodosEstaticos.convertString(vehiculo.getTipo().getNombre()));

        TextView lblDescripcion = view.findViewById(R.id.lbl_descripcion);
        lblDescripcion.setText(Vehiculo.getLongTitle(vehiculo));

        TextView lblMarca = view.findViewById(R.id.lbl_marca);
        lblMarca.setText(MetodosEstaticos.convertString(vehiculo.getModelo().getMarca().getNombre()));

        TextView lblModelo = view.findViewById(R.id.lbl_modelo);
        lblModelo.setText(MetodosEstaticos.convertString(vehiculo.getModelo().getNombre()));

        TextView lblColor = view.findViewById(R.id.lbl_color);
        lblColor.setText(MetodosEstaticos.convertString(vehiculo.getColor()));

        TextView lblTransmision = view.findViewById(R.id.lbl_transmision);
        lblTransmision.setText(MetodosEstaticos.convertString(vehiculo.getTransmision()));

        TextView lblTraccion = view.findViewById(R.id.lbl_traccion);
        lblTraccion.setText(MetodosEstaticos.convertString(vehiculo.getTraccion()));

        TextView lblYear = view.findViewById(R.id.lbl_year);
        lblYear.setText(String.valueOf(vehiculo.getYear()));

        TextView lblChasis = view.findViewById(R.id.lbl_chasis);
        lblChasis.setText(vehiculo.getNumeroChasis());

        TextView lblPlaca = view.findViewById(R.id.lbl_placa);
        lblPlaca.setText(vehiculo.getPlaca());

        if(vehiculo.getPrecios().length > 2){
            double[] precios = vehiculo.getPrecios();
            TextView lblPrecio1 = view.findViewById(R.id.lbl_precio_venta);
            lblPrecio1.setText(MetodosEstaticos.formatNumber(precios[0]));

            TextView lblPrecio2 = view.findViewById(R.id.lbl_precio_minimo);
            lblPrecio2.setText(MetodosEstaticos.formatNumber(precios[1]));

            TextView lblPrecio3 = view.findViewById(R.id.lbl_precio_ultimo);
            lblPrecio3.setText(MetodosEstaticos.formatNumber(precios[2]));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
