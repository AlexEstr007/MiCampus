package com.example.micampus;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

// Registro de usuarios nuevos
public class RegistroActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        auth = FirebaseAuth.getInstance();

        ((TextView) findViewById(R.id.barraTitulo)).setText("Crear cuenta");
        findViewById(R.id.barraIzq).setOnClickListener(v -> finish());

        EditText etNombre = findViewById(R.id.etNombre);
        EditText etCorreo = findViewById(R.id.etCorreo);
        EditText etPassword = findViewById(R.id.etPassword);
        DBHelper db = new DBHelper(this);

        findViewById(R.id.btnRegistrar).setOnClickListener(v -> {
            String nombre = etNombre.getText().toString().trim();
            String correo = etCorreo.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (nombre.isEmpty() || correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
            } else if (!correo.contains("@")) {
                Toast.makeText(this, "Escribe un correo válido", Toast.LENGTH_SHORT).show();
            } else if (password.length() < 6) {
                Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Registro en Firebase
                auth.createUserWithEmailAndPassword(correo, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Opcional: guardar también en SQLite local
                                db.registrarUsuario(nombre, correo, password);
                                Toast.makeText(this, "Cuenta creada, ya puedes iniciar sesión",
                                        Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                String msg = task.getException() != null
                                        ? task.getException().getMessage()
                                        : "Error desconocido";
                                Toast.makeText(this, "Error: " + msg, Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }
}