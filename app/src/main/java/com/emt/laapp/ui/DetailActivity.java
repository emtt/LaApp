package com.emt.laapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.emt.laapp.R;

public class DetailActivity extends AppCompatActivity {

    private TextView detailNombre, detailNumero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        detailNombre = (TextView) findViewById(R.id.detailNombre);
        detailNumero = (TextView) findViewById(R.id.detailNumero);
        String nombre = getIntent().getStringExtra("nombre");
        String telefono = getIntent().getStringExtra("telefono");

        detailNombre.setText(nombre);
        detailNumero.setText(telefono);

    }
}
