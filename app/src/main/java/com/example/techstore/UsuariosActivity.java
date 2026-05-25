package com.example.techstore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UsuariosActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuarios);

        RecyclerView recycler = findViewById(R.id.recyclerUsuarios);
        Button btnVolver = findViewById(R.id.btnVolver);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        List<Usuario> lista = new ArrayList<>();
        UsuarioAdapter adapter = new UsuarioAdapter(lista);
        recycler.setAdapter(adapter);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnVolver.setOnClickListener(v -> finish());

        db.collection("usuarios")
                .get()
                .addOnSuccessListener(query -> {

                    lista.clear();

                    for (QueryDocumentSnapshot doc : query) {
                        Usuario u = doc.toObject(Usuario.class);
                        lista.add(u);
                    }

                    adapter.notifyItemRangeChanged(0, lista.size());
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                "Error al cargar usuarios",
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}