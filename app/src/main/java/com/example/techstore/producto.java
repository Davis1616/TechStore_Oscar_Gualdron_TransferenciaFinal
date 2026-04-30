package com.example.techstore;

public class producto {

    public String id;
    public String nombre;
    public String precio;
    public String descripcion;
    public String imagen; //  UNIFICADO

    //  Constructor vacío (Firebase)
    public producto() {
    }

    public producto(String nombre, String precio, String descripcion, String imagen) {
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
        this.imagen = imagen;
    }
}