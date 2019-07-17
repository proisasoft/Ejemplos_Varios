package com.example.dionicio.appcotizacion.Fragmentos;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;


import com.example.dionicio.appcotizacion.Clases.DatePickerFragment;
import com.example.dionicio.appcotizacion.Clases.Evento;
import com.example.dionicio.appcotizacion.Clases.MetodosEstaticos;
import com.example.dionicio.appcotizacion.Clases.TimePickerFragment;
import com.example.dionicio.appcotizacion.R;

import java.util.ArrayList;
import java.util.Date;


public class DatosEventosFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemSelectedListener{
    private TextInputEditText txtFecHora, txtFecHoraEntrega,txtFecHoraRecogida,txtResponsable, txtDireccion;
    private Spinner spTipo,spCiudad;

    private Evento mEvento;
    private String fecha, hora;
    private String tipoEventoSeleccionado="";
    private String ciudadSeleccionada="";

    public DatosEventosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_datos_eventos, container, false);


        txtFecHora = v.findViewById(R.id.txt_fec_hora_evento);
        txtFecHoraEntrega = v.findViewById(R.id.txt_fec_hora_entrega_evento);
        txtFecHoraRecogida = v.findViewById(R.id.txt_fec_hora_recogida_evento);
        txtResponsable = v.findViewById(R.id.txt_responsable_evento);
        txtDireccion = v.findViewById(R.id.txt_direccion_evento);
        spTipo = v.findViewById(R.id.sp_tipo_evento);
        spCiudad = v.findViewById(R.id.sp_ciudad_evento);

        spTipo.setOnItemSelectedListener(this);
        spCiudad.setOnItemSelectedListener(this);


        //Listener
        txtFecHora.setOnClickListener(this);
        txtFecHora.setInputType(InputType.TYPE_NULL);

        txtFecHoraEntrega.setOnClickListener(this);
        txtFecHoraEntrega.setInputType(InputType.TYPE_NULL);

        txtFecHoraRecogida.setOnClickListener(this);
        txtFecHoraRecogida.setInputType(InputType.TYPE_NULL);

        txtResponsable.setImeOptions(EditorInfo.IME_ACTION_NEXT);
        txtDireccion.setImeOptions(EditorInfo.IME_ACTION_DONE);
        return v;
    }

    public void llenarSpinnerTipo(ArrayList<String> datos){
        ArrayAdapter adapter = new ArrayAdapter(this.getContext(),android.R.layout.simple_spinner_dropdown_item,datos);
        spTipo.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void llenarSpinnerCiudad(ArrayList<String> datos){
        ArrayAdapter adapter = new ArrayAdapter(this.getContext(),android.R.layout.simple_spinner_dropdown_item,datos);
        spCiudad.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public boolean validaciones(){


        if(TextUtils.isEmpty(txtFecHora.getText().toString())){
            txtFecHora.requestFocus();
            Toast.makeText(this.getContext(), R.string.msg_campo_requerido,Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(txtFecHoraEntrega.getText().toString())){
            txtFecHoraEntrega.requestFocus();
            Toast.makeText(this.getContext(), R.string.msg_campo_requerido,Toast.LENGTH_SHORT).show();
            return false;
        }else if(TextUtils.isEmpty(txtFecHoraRecogida.getText().toString())){
            txtFecHoraRecogida.requestFocus();
            Toast.makeText(this.getContext(),R.string.msg_campo_requerido,Toast.LENGTH_SHORT).show();
            return false;
        }else if(tipoEventoSeleccionado == "0"){
            Toast.makeText(this.getContext(), getString(R.string.tipo_evento_no_seleccionado),Toast.LENGTH_SHORT).show();
            spTipo.requestFocus();
            return false;
        }




        return true;
    }

    public Evento getDatosEvento(){
        mEvento = new Evento();

        mEvento.setFechaHoraEvento(MetodosEstaticos.convertToDate(txtFecHora.getText().toString(),MetodosEstaticos.FORMATO_FECHA_TIEMPO));
        mEvento.setFechaHoraEntrega(MetodosEstaticos.convertToDate(txtFecHoraEntrega.getText().toString(),MetodosEstaticos.FORMATO_FECHA_TIEMPO));
        mEvento.setFechaHoraRecogida(MetodosEstaticos.convertToDate(txtFecHoraRecogida.getText().toString(),MetodosEstaticos.FORMATO_FECHA_TIEMPO));
        mEvento.setResponsable(txtResponsable.getText().toString());
        mEvento.setDireccion(txtDireccion.getText().toString());
        mEvento.setTipo(tipoEventoSeleccionado);

        mEvento.setCiudad(ciudadSeleccionada);

        return this.mEvento;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_fec_hora_evento:
               mostrarDialogoFecha(txtFecHora);
                break;
            case R.id.txt_fec_hora_entrega_evento:
                mostrarDialogoFecha(txtFecHoraEntrega);
                break;

            case R.id.txt_fec_hora_recogida_evento:
                mostrarDialogoFecha(txtFecHoraRecogida);
                break;
        }
    }

    public void mostrarDialogoFecha(final EditText editText){
        DatePickerFragment.newInstance(new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                fecha = String.format("%d-%d-%d",day,month+1,year);

                mostrarDialogoHora(editText);
            }
        }).show(this.getActivity().getFragmentManager(),"");
    }

    public void mostrarDialogoHora(final EditText txt){
        TimePickerFragment.newInstance(new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                hora = String.format(" %d:%02d",i,i1);
                Date mFecha  = MetodosEstaticos.convertToDate(String.format("%s%s",fecha,hora), MetodosEstaticos.FORMATO_FECHA_TIEMPO);

                if (mFecha != null){
                    txt.setText(MetodosEstaticos.formatDate(mFecha,"dd-MM-yyyy HH:mm"));
                }
            }
        }).show(this.getActivity().getFragmentManager(),"");

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.sp_tipo_evento:
                tipoEventoSeleccionado = String.valueOf(i);
                break;
            case R.id.sp_ciudad_evento:
                ciudadSeleccionada = String.valueOf(i);
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
