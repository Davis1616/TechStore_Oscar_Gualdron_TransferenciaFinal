package com.example.techstore;

import com.google.firebase.firestore.IgnoreExtraProperties;

@IgnoreExtraProperties
@SuppressWarnings("unused")
public class Usuario {

    private String nombre;
    private String email;
    private String rol;

    public Usuario() {
    }

    public Usuario(String nombre, String email, String rol) {
        this.nombre = nombre;
        this.email = email;
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }
}