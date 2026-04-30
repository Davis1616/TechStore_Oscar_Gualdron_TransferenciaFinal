package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class CatalogoActivity extends AppCompatActivity {

    RecyclerView recyclerProductos;
    ProductoAdapter adapter;
    List<producto> lista; //

    FirebaseFirestore db;

    Button btnCarrito, btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        btnCarrito = findViewById(R.id.btnCarrito);
        btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> finish());

        btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, CarritoActivity.class))
        );

        recyclerProductos = findViewById(R.id.recyclerProductos);

        GridLayoutManager gridLayout = new GridLayoutManager(this, 2);
        recyclerProductos.setLayoutManager(gridLayout);
        recyclerProductos.setHasFixedSize(true);

        lista = new ArrayList<>();
        adapter = new ProductoAdapter(lista);
        recyclerProductos.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        cargarProductos();
    }

    private void cargarProductos() {

        recyclerProductos.setAlpha(0.3f);

        db.collection("productos")
                .get()
                .addOnSuccessListener(query -> {

                    lista.clear();

                    for (QueryDocumentSnapshot doc : query) {

                        producto p = doc.toObject(producto.class); // 🔥 CAMBIO AQUÍ

                        if (p != null) {

                            if (p.imagen == null) {
                                p.imagen = "";
                            }

                            lista.add(p);
                        }
                    }

                    adapter.notifyDataSetChanged();
                    recyclerProductos.setAlpha(1f);

                    if (lista.isEmpty()) {
                        Toast.makeText(this, "No hay productos en Firebase", Toast.LENGTH_LONG).show();
                    }

                })
                .addOnFailureListener(e -> {
                    recyclerProductos.setAlpha(1f);
                    Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos();
    }
}