package com.example.micampus;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    EditText etCorreo, etPassword;
    boolean verPassword = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ((TextView) findViewById(R.id.barraTitulo)).setText("Mi Campus");
        findViewById(R.id.barraIzq).setVisibility(View.INVISIBLE);

        etCorreo = findViewById(R.id.etCorreo);
        etPassword = findViewById(R.id.etPassword);
        DBHelper db = new DBHelper(this);

        findViewById(R.id.tvVer).setOnClickListener(v -> {
            verPassword = !verPassword;
            if (verPassword) {
                etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            } else {
                etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
            }
            etPassword.setSelection(etPassword.getText().length());
        });

        findViewById(R.id.btnEntrar).setOnClickListener(v -> {
            String correo = etCorreo.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            // Validaciones
            if (correo.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            String[] usuario = db.login(correo, password);
            if (usuario == null) {
                Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                return;
            }

            // usuario[0] = nombre, usuario[1] = rol
            Sesion.guardar(this, correo, usuario[0], usuario[1]);
            if (usuario[1].equals("admin")) {
                startActivity(new Intent(this, AdminActivity.class));
            } else {
                startActivity(new Intent(this, MenuActivity.class));
            }
            finish();
        });

        findViewById(R.id.tvOlvide).setOnClickListener(v ->
                Toast.makeText(this, "Contacta al administrador para recuperar tu contraseña",
                        Toast.LENGTH_LONG).show());

        findViewById(R.id.tvRegistro).setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class)));
    }
}
