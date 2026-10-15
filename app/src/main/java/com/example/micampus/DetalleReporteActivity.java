package com.example.micampus;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;

// Detalle / editar reporte
public class DetalleReporteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_reporte);

        ((TextView) findViewById(R.id.barraTitulo)).setText("Detalle del reporte");
        findViewById(R.id.barraIzq).setOnClickListener(v -> finish());

        DBHelper db = new DBHelper(this);
        int id = getIntent().getIntExtra("id", 0);
        Reporte r = db.obtenerReporte(id);
        if (r == null) {   // por si el reporte ya no existe
            finish();
            return;
        }

        ((TextView) findViewById(R.id.tvCategoria)).setText(
                ReporteAdapter.icono(r.categoria) + "  " + r.categoria);
        ((TextView) findViewById(R.id.tvInfo)).setText(r.fecha + "  ·  " + r.correo);
        ((TextView) findViewById(R.id.tvDescripcion)).setText(r.descripcion);

        // Lista desplegable de estados
        Spinner spEstado = findViewById(R.id.spEstado);
        String[] estados = {"Pendiente", "En proceso", "Solucionado"};
        spEstado.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, estados));
        spEstado.setSelection(Arrays.asList(estados).indexOf(r.estado));

        // Solo el administrador puede cambiar el estado
        boolean esAdmin = Sesion.get(this, "rol").equals("admin");
        spEstado.setEnabled(esAdmin);

        RatingBar rbCalificacion = findViewById(R.id.rbCalificacion);
        rbCalificacion.setRating(r.calificacion);

        findViewById(R.id.btnActualizar).setOnClickListener(v -> {
            db.actualizarReporte(id, spEstado.getSelectedItem().toString(),
                    (int) rbCalificacion.getRating());
            Toast.makeText(this, "Reporte actualizado", Toast.LENGTH_SHORT).show();
            finish();
        });

        findViewById(R.id.btnEliminar).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Eliminar reporte")
                        .setMessage("¿Seguro que quieres eliminar este reporte?")
                        .setPositiveButton("Eliminar", (dialogo, cual) -> {
                            db.eliminarReporte(id);
                            Toast.makeText(this, "Reporte eliminado", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .setNegativeButton("Cancelar", null)
                        .show());
    }
}
