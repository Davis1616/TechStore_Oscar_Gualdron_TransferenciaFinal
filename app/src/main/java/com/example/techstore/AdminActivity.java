package com.example.techstore;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AdminActivity extends AppCompatActivity {

    private RecyclerView recyclerVentas;

    private TextView txtTotalVentas;
    private TextView txtTotalPedidos;
    private TextView txtPagados;
    private TextView txtPendientes;

    private final List<Orden> lista = new ArrayList<>();

    private HistorialAdapter adapter;

    private int totalDinero;
    private int totalPedidos;
    private int pagados;
    private int pendientes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        inicializarVistas();
        configurarRecyclerView();

        findViewById(R.id.btnVolver)
                .setOnClickListener(v -> finish());

        cargarVentas();
    }

    private void inicializarVistas() {

        recyclerVentas = findViewById(R.id.recyclerVentas);

        txtTotalVentas = findViewById(R.id.txtTotalVentas);
        txtTotalPedidos = findViewById(R.id.txtTotalPedidos);
        txtPagados = findViewById(R.id.txtPagados);
        txtPendientes = findViewById(R.id.txtPendientes);
    }

    private void configurarRecyclerView() {

        recyclerVentas.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new HistorialAdapter(lista);

        recyclerVentas.setAdapter(adapter);
    }

    private void cargarVentas() {

        FirebaseFirestore.getInstance()
                .collection("ordenes")
                .get()
                .addOnSuccessListener(query -> {

                    int cantidadAnterior = lista.size();

                    lista.clear();

                    if (cantidadAnterior > 0) {
                        adapter.notifyItemRangeRemoved(0, cantidadAnterior);
                    }

                    reiniciarContadores();

                    for (QueryDocumentSnapshot doc : query) {

                        Orden orden = doc.toObject(Orden.class);

                        lista.add(orden);

                        totalPedidos++;

                        calcularTotalDinero(orden);

                        if ("pagado".equalsIgnoreCase(orden.getEstado())) {
                            pagados++;
                        } else {
                            pendientes++;
                        }
                    }

                    adapter.notifyItemRangeInserted(0, lista.size());

                    actualizarResumen();
                });
    }

    private void reiniciarContadores() {

        totalDinero = 0;
        totalPedidos = 0;
        pagados = 0;
        pendientes = 0;
    }

    private void calcularTotalDinero(Orden orden) {

        if (orden.getTotal() != null &&
                !orden.getTotal().trim().isEmpty()) {

            try {

                totalDinero += Integer.parseInt(
                        orden.getTotal().trim()
                );

            } catch (NumberFormatException ignored) {
            }
        }
    }

    private void actualizarResumen() {

        txtTotalVentas.setText(
                String.format(
                        Locale.getDefault(),
                        getString(R.string.total_vendido),
                        totalDinero
                )
        );

        txtTotalPedidos.setText(
                String.format(
                        Locale.getDefault(),
                        getString(R.string.total_pedidos),
                        totalPedidos
                )
        );

        txtPagados.setText(
                String.format(
                        Locale.getDefault(),
                        getString(R.string.total_pagados),
                        pagados
                )
        );

        txtPendientes.setText(
                String.format(
                        Locale.getDefault(),
                        getString(R.string.total_pendientes),
                        pendientes
                )
        );
    }
}