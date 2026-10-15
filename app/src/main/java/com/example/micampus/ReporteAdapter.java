package com.example.micampus;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

// Dibuja cada renglon de la lista de reportes
public class ReporteAdapter extends ArrayAdapter<Reporte> {

    public ReporteAdapter(Context context, ArrayList<Reporte> datos) {
        super(context, 0, datos);
    }

    @Override
    public View getView(int posicion, View vista, ViewGroup padre) {
        if (vista == null) {
            vista = LayoutInflater.from(getContext()).inflate(R.layout.item_reporte, padre, false);
        }
        Reporte r = getItem(posicion);

        TextView tvIcono = vista.findViewById(R.id.tvIcono);
        TextView tvCategoria = vista.findViewById(R.id.tvCategoria);
        TextView tvEstado = vista.findViewById(R.id.tvEstado);
        TextView tvFecha = vista.findViewById(R.id.tvFecha);

        tvIcono.setText(icono(r.categoria));
        tvCategoria.setText(r.categoria);
        tvFecha.setText(r.fecha);
        tvEstado.setText(r.estado);

        // Color del "chip" segun el estado
        if (r.estado.equals("Solucionado")) {
            tvEstado.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4F4DD")));
            tvEstado.setTextColor(Color.parseColor("#1E7A3C"));
        } else if (r.estado.equals("En proceso")) {
            tvEstado.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFE8C2")));
            tvEstado.setTextColor(Color.parseColor("#B26A00"));
        } else {
            tvEstado.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D6E8FB")));
            tvEstado.setTextColor(Color.parseColor("#1F6FDB"));
        }
        return vista;
    }

    public static String icono(String categoria) {
        switch (categoria) {
            case "Fuga de agua":       return "💧";
            case "Falla de luz":       return "💡";
            case "Limpieza":           return "🍃";
            case "Mobiliario y equipo": return "🔧";
            case "Pagos y trámites":   return "💳";
            case "Eventos":            return "📅";
            default:                   return "📌";
        }
    }
}
