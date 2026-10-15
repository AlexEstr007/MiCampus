package com.example.micampus;

import android.content.Intent;
import android.os.Bundle;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

// Pantalla 7: Panel de administracion
public class AdminActivity extends AppCompatActivity {

    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        db = new DBHelper(this);
        ((TextView) findViewById(R.id.barraTitulo)).setText("Administración");

        // Icono de menu (☰) con la opcion de cerrar sesion
        TextView barraIzq = findViewById(R.id.barraIzq);
        barraIzq.setText("☰");
        barraIzq.setTextSize(24);
        barraIzq.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(this, v);
            menu.getMenu().add("Cerrar sesión");
            menu.setOnMenuItemClickListener(item -> {
                Sesion.cerrar(this);
                return true;
            });
            menu.show();
        });

        findViewById(R.id.rowReportes).setOnClickListener(v ->
                startActivity(new Intent(this, MisReportesActivity.class)));
        findViewById(R.id.rowUsuarios).setOnClickListener(v ->
                Toast.makeText(this, "Usuarios registrados: " + db.contarUsuarios(),
                        Toast.LENGTH_SHORT).show());
        findViewById(R.id.rowConfig).setOnClickListener(v ->
                Toast.makeText(this, "Disponible en una versión futura", Toast.LENGTH_SHORT).show());
    }

    // Las estadisticas se actualizan cada vez que se regresa a esta pantalla
    @Override
    protected void onResume() {
        super.onResume();
        ((TextView) findViewById(R.id.tvTotal)).setText(String.valueOf(db.contarReportes(null)));
        ((TextView) findViewById(R.id.tvSolucionados)).setText(String.valueOf(db.contarReportes("Solucionado")));
        ((TextView) findViewById(R.id.tvProceso)).setText(String.valueOf(db.contarReportes("En proceso")));
        ((TextView) findViewById(R.id.tvPendientes)).setText(String.valueOf(db.contarReportes("Pendiente")));
    }
}
