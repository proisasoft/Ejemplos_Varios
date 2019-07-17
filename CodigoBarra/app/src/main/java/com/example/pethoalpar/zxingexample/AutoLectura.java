package com.example.pethoalpar.zxingexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;

import java.util.ArrayList;

public class AutoLectura extends AppCompatActivity {

    private Button button;
    private ProgressBar bar;
    private ListView mLista;
    private ArrayList<String> valores, valoresAux;
    private ArrayList<Integer> cantidad;
    private ArrayAdapter<String> mAdapter;
    private Switch escaneoAuto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_lectura);

        valores = new ArrayList<>();
        valoresAux = new ArrayList<>();

        mAdapter = new ArrayAdapter<String>(AutoLectura.this,android.R.layout.simple_list_item_1,valores);
        mLista.setAdapter(mAdapter);

        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                escanearCodigo();
            }
        });*/

    }
}
