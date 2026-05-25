package com.example.techstore;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
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

    private Button btnVolver;
    private RecyclerView recyclerVentas;

    private TextView txtTotalVentas;
    private TextView txtTotalPedidos;
    private TextView txtPagados;
    private TextView txtPendientes;

    private List<Orden> lista;
    private HistorialAdapter adapter;
    private FirebaseFirestore db;

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

        db = FirebaseFirestore.getInstance();

        btnVolver.setOnClickListener(v -> finish());

        cargarVentas();
    }

    private void inicializarVistas() {

        btnVolver = findViewById(R.id.btnVolver);
        recyclerVentas = findViewById(R.id.recyclerVentas);

        txtTotalVentas = findViewById(R.id.txtTotalVentas);
        txtTotalPedidos = findViewById(R.id.txtTotalPedidos);
        txtPagados = findViewById(R.id.txtPagados);
        txtPendientes = findViewById(R.id.txtPendientes);
    }

    private void configurarRecyclerView() {

        recyclerVentas.setLayoutManager(new LinearLayoutManager(this));

        lista = new ArrayList<>();

        adapter = new HistorialAdapter(lista);

        recyclerVentas.setAdapter(adapter);
    }

    private void cargarVentas() {

        db.collection("ordenes")
                .get()
                .addOnSuccessListener(query -> {

                    lista.clear();
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

                    actualizarResumen();

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Log.e("AdminActivity", "Error cargando ventas", e)
                );
    }

    private void reiniciarContadores() {
        totalDinero = 0;
        totalPedidos = 0;
        pagados = 0;
        pendientes = 0;
    }

    private void calcularTotalDinero(Orden orden) {

        if (orden != null && orden.getTotal() != null && !orden.getTotal().isEmpty()) {

            try {
                totalDinero += Integer.parseInt(orden.getTotal().trim());
            } catch (NumberFormatException e) {
                Log.e("AdminActivity", "Error parseando total: " + orden.getTotal(), e);
            }
        }
    }

    private void actualizarResumen() {

        txtTotalVentas.setText(
                String.format(Locale.getDefault(), "Total vendido: $%d", totalDinero)
        );

        txtTotalPedidos.setText(
                String.format(Locale.getDefault(), "Pedidos: %d", totalPedidos)
        );

        txtPagados.setText(
                String.format(Locale.getDefault(), "Pagados: %d", pagados)
        );

        txtPendientes.setText(
                String.format(Locale.getDefault(), "Pendientes: %d", pendientes)
        );
    }
}