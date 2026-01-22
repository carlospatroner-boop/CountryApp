package com.example.google;

public class Pais {
    private String nombre;
    private String codigoIso;
    public Pais(String nombre, String codigoIso) {
        this.nombre = nombre;
        this.codigoIso = codigoIso;
    }

    public String getNombre() { return nombre; }
    public String getCodigoIso() { return codigoIso; }
}