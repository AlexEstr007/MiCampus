package com.example.micampus;

import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

// Pantalla 6: Perfil (editar nombre, cambiar contraseña, configuración)
public class PerfilActivity extends AppCompatActivity {

    DBHelper db;
    TextView tvNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        db = new DBHelper(this);
        ((TextView) findViewById(R.id.barraTitulo)).setText("Perfil");
        findViewById(R.id.barraIzq).setOnClickListener(v -> finish());

        tvNombre = findViewById(R.id.tvNombre);
        tvNombre.setText(Sesion.get(this, "nombre"));
        ((TextView) findViewById(R.id.tvCorreo)).setText(Sesion.get(this, "correo"));

        findViewById(R.id.rowEditar).setOnClickListener(v -> editarNombre());
        findViewById(R.id.rowCambiar).setOnClickListener(v -> cambiarPassword());
        findViewById(R.id.rowConfig).setOnClickListener(v ->
                startActivity(new android.content.Intent(this, ConfiguracionActivity.class)));
    }

    void editarNombre() {
        EditText et = campo("Nombre", InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        et.setText(Sesion.get(this, "nombre"));
        et.setSelection(et.getText().length());

        new AlertDialog.Builder(this)
                .setTitle("Editar nombre")
                .setView(contenedor(et))
                .setPositiveButton("Guardar", (d, w) -> {
                    String nombre = et.getText().toString().trim();
                    if (nombre.isEmpty()) {
                        Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    db.actualizarNombre(Sesion.get(this, "correo"), nombre);
                    Sesion.actualizarNombre(this, nombre);
                    tvNombre.setText(nombre);
                    Toast.makeText(this, "Nombre actualizado", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    void cambiarPassword() {
        int tipo = InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD;
        EditText etActual = campo("Contraseña actual", tipo);
        EditText etNueva = campo("Nueva contraseña (mínimo 6)", tipo);
        LinearLayout caja = contenedor(etActual);
        caja.addView(etNueva);

        new AlertDialog.Builder(this)
                .setTitle("Cambiar contraseña")
                .setView(caja)
                .setPositiveButton("Guardar", (d, w) -> {
                    String actual = etActual.getText().toString().trim();
                    String nueva = etNueva.getText().toString().trim();
                    if (nueva.length() < 6) {
                        Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres",
                                Toast.LENGTH_SHORT).show();
                    } else if (db.cambiarPassword(Sesion.get(this, "correo"), actual, nueva)) {
                        Toast.makeText(this, "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "La contraseña actual es incorrecta", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    // Muestra a donde llegan los avisos de cambio de estado
    void configuracion() {
        String tema = Notificador.tema(Sesion.get(this, "correo"));
        new AlertDialog.Builder(this)
                .setTitle("Configuración")
                .setMessage("MiCampus v1.0\n\nAvisos de estado de reportes:\n" +
                        "La app te notifica cuando el administrador cambia el estado de un reporte.\n\n" +
                        "Para recibirlos también como notificación push, instala la app \"ntfy\" y " +
                        "suscríbete al tema:\n" + tema)
                .setPositiveButton("Cerrar", null)
                .show();
    }

    private EditText campo(String pista, int tipo) {
        EditText et = new EditText(this);
        et.setHint(pista);
        et.setInputType(tipo);
        et.setSingleLine(true);
        return et;
    }

    private LinearLayout contenedor(EditText primero) {
        LinearLayout l = new LinearLayout(this);
        l.setOrientation(LinearLayout.VERTICAL);
        int p = (int) (20 * getResources().getDisplayMetrics().density);
        l.setPadding(p, p / 2, p, 0);
        l.addView(primero);
        return l;
    }
}
