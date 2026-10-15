package com.example.micampus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

// Pantalla 5: Mis reportes (el administrador ve todos los reportes)
public class MisReportesActivity extends AppCompatActivity {

    ListView lvReportes;
    TextView tvVacio;
    DBHelper db;
    ArrayList<Reporte> lista;
    boolean esAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_reportes);

        db = new DBHelper(this);
        lvReportes = findViewById(R.id.lvReportes);
        tvVacio = findViewById(R.id.tvVacio);
        esAdmin = Sesion.get(this, "rol").equals("admin");

        ((TextView) findViewById(R.id.barraTitulo)).setText(esAdmin ? "Todos los reportes" : "Mis reportes");
        findViewById(R.id.barraIzq).setOnClickListener(v -> finish());

        View btnNuevo = findViewById(R.id.btnNuevo);
        if (esAdmin) {
            btnNuevo.setVisibility(View.GONE);
        }
        btnNuevo.setOnClickListener(v ->
                startActivity(new Intent(this, NuevoReporteActivity.class)));

        // Al tocar un reporte se abre su detalle
        lvReportes.setOnItemClickListener((padre, vista, posicion, id) -> {
            Intent i = new Intent(this, DetalleReporteActivity.class);
            i.putExtra("id", lista.get(posicion).id);
            startActivity(i);
        });
    }

    // Se recarga la lista cada vez que se regresa a esta pantalla
    @Override
    protected void onResume() {
        super.onResume();
        if (esAdmin) {
            lista = db.obtenerReportes(null);
        } else {
            lista = db.obtenerReportes(Sesion.get(this, "correo"));
        }
        lvReportes.setAdapter(new ReporteAdapter(this, lista));
        tvVacio.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
