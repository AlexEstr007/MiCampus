package com.example.micampus;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Pantalla 4: Nuevo reporte
public class NuevoReporteActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_reporte);

        ((TextView) findViewById(R.id.barraTitulo)).setText("Nuevo reporte");
        findViewById(R.id.barraIzq).setOnClickListener(v -> finish());

        Spinner spCategoria = findViewById(R.id.spCategoria);
        EditText etDescripcion = findViewById(R.id.etDescripcion);
        DBHelper db = new DBHelper(this);

        String[] categorias = {"Selecciona una opción", "Fuga de agua", "Falla de luz", "Limpieza",
                "Mobiliario y equipo", "Pagos y trámites", "Eventos", "Otro"};
        spCategoria.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, categorias));

        findViewById(R.id.btnEnviar).setOnClickListener(v -> {
            String descripcion = etDescripcion.getText().toString().trim();

            if (spCategoria.getSelectedItemPosition() == 0) {
                Toast.makeText(this, "Selecciona una categoría", Toast.LENGTH_SHORT).show();
            } else if (descripcion.isEmpty()) {
                Toast.makeText(this, "Describe el problema", Toast.LENGTH_SHORT).show();
            } else {
                db.agregarReporte(Sesion.get(this, "correo"),
                        spCategoria.getSelectedItem().toString(), descripcion);
                Toast.makeText(this, "Reporte enviado", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
}
