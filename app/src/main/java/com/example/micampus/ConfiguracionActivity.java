package com.example.micampus;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

// Pantalla de configuración: avisos, tema de ntfy, cerrar sesión y versión
public class ConfiguracionActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);

        ((TextView) findViewById(R.id.barraTitulo)).setText("Configuración");
        findViewById(R.id.barraIzq).setOnClickListener(v -> finish());

        boolean esAdmin = Sesion.get(this, "rol").equals("admin");
        final String tema = Notificador.tema(Sesion.get(this, "correo"));

        // El administrador no recibe avisos, solo los alumnos
        if (esAdmin) {
            findViewById(R.id.bloqueAvisos).setVisibility(View.GONE);
            findViewById(R.id.bloqueTema).setVisibility(View.GONE);
        }

        SwitchCompat sw = findViewById(R.id.swAvisos);
        sw.setChecked(Notificador.avisosActivos(this));
        sw.setOnCheckedChangeListener((CompoundButton b, boolean activo) -> {
            Notificador.setAvisosActivos(this, activo);
            if (activo) Notificador.pedirPermiso(this);
            Toast.makeText(this, activo ? "Avisos activados" : "Avisos desactivados",
                    Toast.LENGTH_SHORT).show();
        });

        ((TextView) findViewById(R.id.tvTema)).setText(tema);
        findViewById(R.id.btnCopiarTema).setOnClickListener(v -> {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setPrimaryClip(ClipData.newPlainText("tema", tema));
            Toast.makeText(this, "Tema copiado", Toast.LENGTH_SHORT).show();
        });

        findViewById(R.id.rowCerrar).setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Cerrar sesión")
                        .setMessage("¿Quieres cerrar tu sesión?")
                        .setPositiveButton("Cerrar sesión", (d, w) -> Sesion.cerrar(this))
                        .setNegativeButton("Cancelar", null)
                        .show());

        ((TextView) findViewById(R.id.tvVersion)).setText("MiCampus v1.0");
    }
}
