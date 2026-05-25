package com.example.techstore;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class HistorialActivity extends AppCompatActivity {

    private HistorialAdapter adapter;

    private final List<Orden> lista =
            new ArrayList<>();

    private FirebaseFirestore db;

    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_historial);

        RecyclerView recyclerHistorial =
                findViewById(R.id.recyclerHistorial);

        recyclerHistorial.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new HistorialAdapter(lista);

        recyclerHistorial.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        FirebaseAuth auth =
                FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {

            finish();

            return;
        }

        uid = auth.getCurrentUser().getUid();

        cargarHistorial();
    }

    private void cargarHistorial() {

        db.collection("ordenes")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(query -> {

                    int cantidadAnterior =
                            lista.size();

                    lista.clear();

                    for (QueryDocumentSnapshot doc : query) {

                        Orden orden =
                                doc.toObject(Orden.class);

                        lista.add(orden);
                    }

                    if (cantidadAnterior > 0) {

                        adapter.notifyItemRangeRemoved(
                                0,
                                cantidadAnterior
                        );
                    }

                    adapter.notifyItemRangeInserted(
                            0,
                            lista.size()
                    );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                getString(
                                        R.string.error_cargar_historial
                                ),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}