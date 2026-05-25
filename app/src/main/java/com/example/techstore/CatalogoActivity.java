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

    private RecyclerView recyclerProductos;
    private ProductoAdapter adapter;

    private final List<Producto> lista = new ArrayList<>();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalogo);

        Button btnCarrito = findViewById(R.id.btnCarrito);
        Button btnVolver = findViewById(R.id.btnVolver);

        recyclerProductos = findViewById(R.id.recyclerProductos);

        btnVolver.setOnClickListener(v -> finish());

        btnCarrito.setOnClickListener(v ->
                startActivity(new Intent(this, CarritoActivity.class))
        );

        recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerProductos.setHasFixedSize(true);

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

                    int tamanoAnterior = lista.size();

                    lista.clear();

                    if (tamanoAnterior > 0) {
                        adapter.notifyItemRangeRemoved(0, tamanoAnterior);
                    }

                    int posicionInicio = lista.size();

                    for (QueryDocumentSnapshot doc : query) {

                        Producto productoFirebase =
                                doc.toObject(Producto.class);

                        if (productoFirebase.getImagen() == null) {
                            productoFirebase.setImagen("");
                        }

                        lista.add(productoFirebase);
                    }

                    adapter.notifyItemRangeInserted(
                            posicionInicio,
                            lista.size()
                    );

                    recyclerProductos.setAlpha(1f);

                    if (lista.isEmpty()) {

                        Toast.makeText(
                                this,
                                getString(R.string.no_hay_productos),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                })
                .addOnFailureListener(e -> {

                    recyclerProductos.setAlpha(1f);

                    Toast.makeText(
                            this,
                            getString(
                                    R.string.error_cargar_productos,
                                    e.getMessage()
                            ),
                            Toast.LENGTH_LONG
                    ).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarProductos();
    }
}