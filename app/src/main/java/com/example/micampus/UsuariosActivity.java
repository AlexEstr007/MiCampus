package com.example.micampus;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

// Pantalla del administrador: lista de usuarios (restablecer contraseña / eliminar)
public class UsuariosActivity extends AppCompatActivity {

    DBHelper db;
    ListView lv;
    ArrayList<String[]> usuarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        db = new DBHelper(this);
        lv = findViewById(R.id.lvUsuarios);
        ((TextView) findViewById(R.id.barraTitulo)).setText("Usuarios");
        findViewById(R.id.barraIzq).setOnClickListener(v -> finish());

        lv.setOnItemClickListener((padre, vista, pos, id) -> opciones(usuarios.get(pos)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargar();
    }

    void cargar() {
        usuarios = db.obtenerUsuarios();
        ArrayList<String> textos = new ArrayList<>();
        for (String[] u : usuarios) {
            String rol = u[2].equals("admin") ? "Administrador" : "Alumno";
            textos.add(u[0] + "\n" + u[1] + "\n" + rol + "  ·  " + u[3] + " reportes");
        }
        lv.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, textos));
        ((TextView) findViewById(R.id.tvTotalUsuarios)).setText(
                "Usuarios registrados: " + usuarios.size());
    }

    void opciones(String[] u) {
        final String correo = u[1];
        if (u[2].equals("admin")) {
            Toast.makeText(this, "La cuenta de administrador no se puede modificar aquí",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        new AlertDialog.Builder(this)
                .setTitle(u[0])
                .setItems(new String[]{"Restablecer contraseña", "Eliminar usuario"}, (d, cual) -> {
                    if (cual == 0) {
                        db.restablecerPassword(correo, "123456");
                        Toast.makeText(this, "Contraseña restablecida a 123456",
                                Toast.LENGTH_LONG).show();
                    } else {
                        new AlertDialog.Builder(this)
                                .setTitle("Eliminar usuario")
                                .setMessage("Se eliminará a " + u[0] + " y todos sus reportes. ¿Continuar?")
                                .setPositiveButton("Eliminar", (d2, w) -> {
                                    db.eliminarUsuario(correo);
                                    Toast.makeText(this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                                    cargar();
                                })
                                .setNegativeButton("Cancelar", null)
                                .show();
                    }
                })
                .show();
    }
}
