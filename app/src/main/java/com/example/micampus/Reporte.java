package com.example.micampus;

// Clase sencilla que guarda los datos de un reporte
public class Reporte {
    public int id;
    public String correo;      // quien lo creo
    public String categoria;
    public String descripcion;
    public String estado;      // Pendiente, En proceso, Solucionado
    public int calificacion;   // 0 a 5
    public String fecha;
}
