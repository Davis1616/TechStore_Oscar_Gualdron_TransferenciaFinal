package com.example.techstore;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.*;

public class AdminActivity extends AppCompatActivity {

    private Button btnVolver;
    private RecyclerView recyclerVentas;

    private TextView txtTotalVentas, txtTotalPedidos, txtPagados, txtPendientes;

    private List<Orden> lista;
    private HistorialAdapter adapter;
    private FirebaseFirestore db;

    private int totalDinero = 0;
    private int totalPedidos = 0;
    private int pagados = 0;
    private int pendientes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        btnVolver = findViewById(R.id.btnVolver);
        recyclerVentas = findViewById(R.id.recyclerVentas);

        txtTotalVentas = findViewById(R.id.txtTotalVentas);
        txtTotalPedidos = findViewById(R.id.txtTotalPedidos);
        txtPagados = findViewById(R.id.txtPagados);
        txtPendientes = findViewById(R.id.txtPendientes);

        if (btnVolver != null) {
            btnVolver.setOnClickListener(v -> finish());
        }

        recyclerVentas.setLayoutManager(new LinearLayoutManager(this));

        lista = new ArrayList<>();
        adapter = new HistorialAdapter(lista);
        recyclerVentas.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        cargarVentas();
    }

    private void cargarVentas() {

        db.collection("ordenes")
                .get()
                .addOnSuccessListener(query -> {

                    lista.clear();

                    totalDinero = 0;
                    totalPedidos = 0;
                    pagados = 0;
                    pendientes = 0;

                    for (QueryDocumentSnapshot doc : query) {

                        Orden o = doc.toObject(Orden.class);

                        if (o != null) {

                            lista.add(o);
                            totalPedidos++;

                            // 🔥 PROTECCIÓN TOTAL
                            if (o.total != null && !o.total.isEmpty()) {
                                try {
                                    totalDinero += Integer.parseInt(o.total.trim());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            if ("pagado".equals(o.estado)) {
                                pagados++;
                            } else {
                                pendientes++;
                            }
                        }
                    }

                    txtTotalVentas.setText("Total vendido: $" + totalDinero);
                    txtTotalPedidos.setText("Pedidos: " + totalPedidos);
                    txtPagados.setText("Pagados: " + pagados);
                    txtPendientes.setText("Pendientes: " + pendientes);

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando ventas", Toast.LENGTH_SHORT).show()
                );
    }
}