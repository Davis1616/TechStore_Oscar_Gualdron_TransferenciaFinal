package com.example.techstore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.*;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsuariosActivity extends AppCompatActivity {

    RecyclerView recycler;
    UsuarioAdapter adapter;
    List<Usuario> lista;
    FirebaseFirestore db;
    Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        //  Referencias UI
        recycler = findViewById(R.id.recyclerUsuarios);
        btnVolver = findViewById(R.id.btnVolver);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        //  Botón volver
        btnVolver.setOnClickListener(v -> finish());

        //  Lista + adapter
        lista = new ArrayList<>();
        adapter = new UsuarioAdapter(lista);
        recycler.setAdapter(adapter);

        // 🔹 Firebase
        db = FirebaseFirestore.getInstance();

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        db.collection("usuarios")
                .get()
                .addOnSuccessListener(query -> {

                    lista.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        Usuario u = doc.toObject(Usuario.class);
                        lista.add(u);
                    }

                    adapter.notifyDataSetChanged();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show()
                );
    }
}