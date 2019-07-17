package com.example.dionicio.appcotizacion.Fragmentos;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import com.example.dionicio.appcotizacion.Clases.Cliente;
import com.example.dionicio.appcotizacion.R;

import java.util.ArrayList;


public class DatosClienteFragment extends Fragment implements AdapterView.OnItemSelectedListener{
    private TextInputEditText txtNombre, txtDireccion, txtTelefono, txtRNC, txtEmail;
    private Spinner spNfc;

    private Cliente mCliente;
    private String ncf ="";

    public DatosClienteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_datos_cliente, container, false);

        txtNombre = v.findViewById(R.id.txt_nombre_cliente);
        txtDireccion = v.findViewById(R.id.txt_direccion_evento);
        txtTelefono = v.findViewById(R.id.txt_telefono_cliente);
        txtRNC = v.findViewById(R.id.txt_rnc_cliente);
        txtEmail = v.findViewById(R.id.txt_email_cliente);
        spNfc = v.findViewById(R.id.sp_ncf);


        spNfc.setOnItemSelectedListener(this);
        return v;
    }

    //NOTA: crear un método para validaciones
    public boolean validaciones(){

        if(TextUtils.isEmpty(txtNombre.getText().toString().trim())){
            Toast.makeText(this.getContext(),String.format("%s: %s","Nombre",getString(R.string.msg_campo_requerido)),Toast.LENGTH_SHORT).show();
            txtNombre.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(txtRNC.getText().toString().trim())){
            Toast.makeText(this.getContext(),String.format("%s: %s","RNC",getString(R.string.msg_campo_requerido)),Toast.LENGTH_SHORT).show();
            txtRNC.requestFocus();
            return false;
        }else if(TextUtils.isEmpty(ncf) || ncf.equals("0")){
            Toast.makeText(this.getContext(),"NCF es requerido",Toast.LENGTH_SHORT).show();
            spNfc.requestFocus();
            return false;
        }

        return true;
    }

    //Devolver los datos recogidos del cliente
    public Cliente getCliente() {
        mCliente = new Cliente();

        mCliente.setNombre(txtNombre.getText().toString());
        mCliente.setDireccion(txtDireccion.getText().toString());
        mCliente.setTelefono(txtTelefono.getText().toString());
        mCliente.setRNC(txtRNC.getText().toString());
        mCliente.setEmail(txtEmail.getText().toString());
        mCliente.setNcf(ncf);

        return mCliente;
    }

    public void llenarSpinerNcf(ArrayList<String> list){
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getContext(),android.R.layout.simple_spinner_dropdown_item,list);
        spNfc.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        ncf = String.valueOf(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
