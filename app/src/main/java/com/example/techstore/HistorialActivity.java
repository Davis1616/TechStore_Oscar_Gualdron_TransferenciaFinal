package com.example.techstore;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.*;

public class HistorialActivity extends AppCompatActivity {

    RecyclerView recyclerHistorial;
    HistorialAdapter adapter;
    List<Orden> lista;

    FirebaseFirestore db;
    FirebaseAuth auth;

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historial);

        recyclerHistorial = findViewById(R.id.recyclerHistorial);
        recyclerHistorial.setLayoutManager(new LinearLayoutManager(this));

        lista = new ArrayList<>();
        adapter = new HistorialAdapter(lista);
        recyclerHistorial.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

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

                    lista.clear();

                    for (QueryDocumentSnapshot doc : query) {

                        Orden o = doc.toObject(Orden.class);

                        if (o != null) {
                            lista.add(o);
                        }
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error cargando historial", Toast.LENGTH_SHORT).show()
                );
    }
}