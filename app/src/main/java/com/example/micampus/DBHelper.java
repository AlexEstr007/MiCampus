package com.example.micampus;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// Capa de datos: aqui estan todas las consultas a la base de datos
public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        super(context, "micampus.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE usuarios (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "nombre TEXT, correo TEXT UNIQUE, password TEXT, rol TEXT)");

        db.execSQL("CREATE TABLE reportes (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "correo TEXT, categoria TEXT, descripcion TEXT, " +
                "estado TEXT, calificacion INTEGER, fecha TEXT)");

        // Usuario administrador de prueba
        db.execSQL("INSERT INTO usuarios (nombre, correo, password, rol) " +
                "VALUES ('Administrador', 'admin@utags.edu.mx', 'admin123', 'admin')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS usuarios");
        db.execSQL("DROP TABLE IF EXISTS reportes");
        onCreate(db);
    }

    // ---------- USUARIOS ----------

    // Devuelve false si el correo ya existe
    public boolean registrarUsuario(String nombre, String correo, String password) {
        ContentValues v = new ContentValues();
        v.put("nombre", nombre);
        v.put("correo", correo);
        v.put("password", password);
        v.put("rol", "alumno");
        long resultado = getWritableDatabase().insert("usuarios", null, v);
        return resultado != -1;
    }

    // Devuelve {nombre, rol} o null si los datos son incorrectos
    public String[] login(String correo, String password) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT nombre, rol FROM usuarios WHERE correo=? AND password=?",
                new String[]{correo, password});
        String[] datos = null;
        if (c.moveToFirst()) {
            datos = new String[]{c.getString(0), c.getString(1)};
        }
        c.close();
        return datos;
    }

    public int contarUsuarios() {
        Cursor c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM usuarios", null);
        c.moveToFirst();
        int total = c.getInt(0);
        c.close();
        return total;
    }

    // ---------- REPORTES ----------

    public void agregarReporte(String correo, String categoria, String descripcion) {
        String fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        ContentValues v = new ContentValues();
        v.put("correo", correo);
        v.put("categoria", categoria);
        v.put("descripcion", descripcion);
        v.put("estado", "Pendiente");
        v.put("calificacion", 0);
        v.put("fecha", fecha);
        getWritableDatabase().insert("reportes", null, v);
    }

    // Si correo es null devuelve TODOS los reportes (para el administrador)
    public ArrayList<Reporte> obtenerReportes(String correo) {
        ArrayList<Reporte> lista = new ArrayList<>();
        Cursor c;
        if (correo == null) {
            c = getReadableDatabase().rawQuery("SELECT * FROM reportes ORDER BY id DESC", null);
        } else {
            c = getReadableDatabase().rawQuery(
                    "SELECT * FROM reportes WHERE correo=? ORDER BY id DESC", new String[]{correo});
        }
        while (c.moveToNext()) {
            lista.add(leerReporte(c));
        }
        c.close();
        return lista;
    }

    public Reporte obtenerReporte(int id) {
        Cursor c = getReadableDatabase().rawQuery(
                "SELECT * FROM reportes WHERE id=?", new String[]{String.valueOf(id)});
        Reporte r = null;
        if (c.moveToFirst()) {
            r = leerReporte(c);
        }
        c.close();
        return r;
    }

    public void actualizarReporte(int id, String estado, int calificacion) {
        ContentValues v = new ContentValues();
        v.put("estado", estado);
        v.put("calificacion", calificacion);
        getWritableDatabase().update("reportes", v, "id=?", new String[]{String.valueOf(id)});
    }

    public void eliminarReporte(int id) {
        getWritableDatabase().delete("reportes", "id=?", new String[]{String.valueOf(id)});
    }

    // Si estado es null cuenta todos los reportes
    public int contarReportes(String estado) {
        Cursor c;
        if (estado == null) {
            c = getReadableDatabase().rawQuery("SELECT COUNT(*) FROM reportes", null);
        } else {
            c = getReadableDatabase().rawQuery(
                    "SELECT COUNT(*) FROM reportes WHERE estado=?", new String[]{estado});
        }
        c.moveToFirst();
        int total = c.getInt(0);
        c.close();
        return total;
    }

    // Convierte el renglon del cursor en un objeto Reporte
    private Reporte leerReporte(Cursor c) {
        Reporte r = new Reporte();
        r.id = c.getInt(c.getColumnIndexOrThrow("id"));
        r.correo = c.getString(c.getColumnIndexOrThrow("correo"));
        r.categoria = c.getString(c.getColumnIndexOrThrow("categoria"));
        r.descripcion = c.getString(c.getColumnIndexOrThrow("descripcion"));
        r.estado = c.getString(c.getColumnIndexOrThrow("estado"));
        r.calificacion = c.getInt(c.getColumnIndexOrThrow("calificacion"));
        r.fecha = c.getString(c.getColumnIndexOrThrow("fecha"));
        return r;
    }
}
