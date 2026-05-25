package com.example.techstore;

import java.io.Serializable;

@SuppressWarnings("unused")
public class Orden implements Serializable {

    private String total;
    private String estado;

    // Constructor vacío requerido por Firebase
    public Orden() {
    }

    public Orden(String total, String estado) {
        this.total = total;
        this.estado = estado;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}