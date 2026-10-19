package com.example.micampus;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Conexion con la API externa ntfy.sh (https://ntfy.sh), gratuita y sin registro.
//  - avisarCambioEstado(): cuando el admin cambia el estado, hace POST al "tema" del alumno.
//  - revisarAvisos(): el alumno consulta su tema y muestra una notificacion por cada aviso nuevo.
// Ademas, el alumno puede instalar la app ntfy y suscribirse a su tema para recibir push reales.
public class Notificador {

    public static final String SERVIDOR = "https://ntfy.sh";
    static final String CANAL = "estado_reportes";
    static final String TAG = "Notificador";

    // Las peticiones de red NO pueden hacerse en el hilo principal
    private static final ExecutorService hilo = Executors.newSingleThreadExecutor();

    // Tema privado de cada alumno, derivado de su correo (no es adivinable facilmente)
    public static String tema(String correo) {
        try {
            byte[] h = MessageDigest.getInstance("SHA-256")
                    .digest(("micampus-utags|" + correo.toLowerCase()).getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder("micampus-");
            for (int i = 0; i < 10; i++) sb.append(String.format("%02x", h[i]));
            return sb.toString();
        } catch (Exception e) {
            return "micampus-general";
        }
    }

    // ---------- ENVIAR (lo usa el admin) ----------

    public static void avisarCambioEstado(Context context, Reporte r, String nuevoEstado) {
        final String tema = tema(r.correo);
        final String titulo = "Tu reporte cambió de estado";
        final String mensaje = r.categoria + ": ahora está \"" + nuevoEstado + "\"";
        hilo.execute(() -> {
            try {
                // Publicacion en formato JSON (permite acentos sin problemas)
                JSONObject cuerpo = new JSONObject();
                cuerpo.put("topic", tema);
                cuerpo.put("title", titulo);
                cuerpo.put("message", mensaje);
                cuerpo.put("tags", new org.json.JSONArray().put("bell"));

                HttpURLConnection con = (HttpURLConnection) new URL(SERVIDOR).openConnection();
                con.setRequestMethod("POST");
                con.setConnectTimeout(8000);
                con.setReadTimeout(8000);
                con.setDoOutput(true);
                con.setRequestProperty("Content-Type", "application/json; charset=utf-8");
                try (OutputStream os = con.getOutputStream()) {
                    os.write(cuerpo.toString().getBytes(StandardCharsets.UTF_8));
                }
                Log.d(TAG, "ntfy respondio: " + con.getResponseCode());
                con.disconnect();
            } catch (Exception e) {
                Log.w(TAG, "No se pudo enviar el aviso: " + e.getMessage());
            }
        });
    }

    // ---------- RECIBIR (lo usa el alumno) ----------

    public static void pedirPermiso(Activity a) {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(a,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(a,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
        }
    }

    public static void revisarAvisos(Context context) {
        final Context app = context.getApplicationContext();
        final String correo = Sesion.get(app, "correo");
        if (correo.isEmpty() || Sesion.get(app, "rol").equals("admin")) return;

        hilo.execute(() -> {
            try {
                String clave = "ultimo_aviso_" + correo;
                long ahora = System.currentTimeMillis() / 1000;
                long desde = app.getSharedPreferences("avisos", Context.MODE_PRIVATE)
                        .getLong(clave, 0);
                if (desde == 0) {   // primera vez: solo avisos desde ahora
                    app.getSharedPreferences("avisos", Context.MODE_PRIVATE)
                            .edit().putLong(clave, ahora).apply();
                    return;
                }

                HttpURLConnection con = (HttpURLConnection) new URL(
                        SERVIDOR + "/" + tema(correo) + "/json?poll=1&since=" + desde).openConnection();
                con.setConnectTimeout(8000);
                con.setReadTimeout(8000);

                long masReciente = desde;
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        if (linea.trim().isEmpty()) continue;
                        JSONObject j = new JSONObject(linea);
                        if (!"message".equals(j.optString("event"))) continue;
                        long t = j.optLong("time", 0);
                        if (t <= desde) continue;   // ya se mostro antes
                        mostrar(app, j.optString("title", "MiCampus"), j.optString("message"),
                                (int) (t % Integer.MAX_VALUE));
                        masReciente = Math.max(masReciente, t);
                    }
                }
                con.disconnect();
                app.getSharedPreferences("avisos", Context.MODE_PRIVATE)
                        .edit().putLong(clave, masReciente).apply();
            } catch (Exception e) {
                Log.w(TAG, "No se pudieron consultar avisos: " + e.getMessage());
            }
        });
    }

    private static void mostrar(Context c, String titulo, String texto, int id) {
        if (Build.VERSION.SDK_INT >= 33 && ContextCompat.checkSelfPermission(c,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel canal = new NotificationChannel(CANAL,
                    "Estado de reportes", NotificationManager.IMPORTANCE_DEFAULT);
            c.getSystemService(NotificationManager.class).createNotificationChannel(canal);
        }
        NotificationCompat.Builder b = new NotificationCompat.Builder(c, CANAL)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(titulo)
                .setContentText(texto)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(texto))
                .setAutoCancel(true);
        NotificationManagerCompat.from(c).notify(id, b.build());
    }
}
