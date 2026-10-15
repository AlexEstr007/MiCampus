package com.example.micampus;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Pantalla 6: Perfil
public class PerfilActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        ((TextView) findViewById(R.id.barraTitulo)).setText("Perfil");
        findViewById(R.id.barraIzq).setOnClickListener(v -> finish());

        ((TextView) findViewById(R.id.tvNombre)).setText(Sesion.get(this, "nombre"));
        ((TextView) findViewById(R.id.tvCorreo)).setText(Sesion.get(this, "correo"));

        // Estas opciones quedan para una version futura
        findViewById(R.id.rowEditar).setOnClickListener(v -> proximamente());
        findViewById(R.id.rowCambiar).setOnClickListener(v -> proximamente());
        findViewById(R.id.rowConfig).setOnClickListener(v -> proximamente());
    }

    void proximamente() {
        Toast.makeText(this, "Disponible en una versión futura", Toast.LENGTH_SHORT).show();
    }
}
