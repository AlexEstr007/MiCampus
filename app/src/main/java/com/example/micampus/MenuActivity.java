package com.example.micampus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

// Pantalla 3: Menu principal
public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        ((TextView) findViewById(R.id.barraTitulo)).setText("MiCampus");
        ((TextView) findViewById(R.id.tvSaludo)).setText("Hola, " + Sesion.get(this, "nombre"));

        // Icono de menu (☰) de la barra
        TextView barraIzq = findViewById(R.id.barraIzq);
        barraIzq.setText("☰");
        barraIzq.setTextSize(24);
        barraIzq.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, v);
            menu.getMenu().add("Perfil");
            menu.getMenu().add("Cerrar sesión");
            menu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().toString().equals("Perfil")) {
                    startActivity(new Intent(this, PerfilActivity.class));
                } else {
                    Sesion.cerrar(this);
                }
                return true;
            });
            menu.show();
        });

        findViewById(R.id.tileNuevo).setOnClickListener(v ->
                startActivity(new Intent(this, NuevoReporteActivity.class)));
        findViewById(R.id.tileMis).setOnClickListener(v ->
                startActivity(new Intent(this, MisReportesActivity.class)));
        findViewById(R.id.tilePerfil).setOnClickListener(v ->
                startActivity(new Intent(this, PerfilActivity.class)));
        findViewById(R.id.tileSalir).setOnClickListener(v -> Sesion.cerrar(this));
    }
}
