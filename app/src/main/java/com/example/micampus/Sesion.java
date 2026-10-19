package com.example.micampus;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

// Guarda quien inicio sesion (usa SharedPreferences)
public class Sesion {

    public static void guardar(Context c, String correo, String nombre, String rol) {
        c.getSharedPreferences("sesion", Context.MODE_PRIVATE).edit()
                .putString("correo", correo)
                .putString("nombre", nombre)
                .putString("rol", rol)
                .apply();
    }

    public static void actualizarNombre(Context c, String nombre) {
        c.getSharedPreferences("sesion", Context.MODE_PRIVATE).edit()
                .putString("nombre", nombre).apply();
    }

    // clave puede ser "correo", "nombre" o "rol"
    public static String get(Context c, String clave) {
        return c.getSharedPreferences("sesion", Context.MODE_PRIVATE).getString(clave, "");
    }

    public static void cerrar(Activity a) {
        a.getSharedPreferences("sesion", Context.MODE_PRIVATE).edit().clear().apply();
        Intent i = new Intent(a, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        a.startActivity(i);
    }
}
