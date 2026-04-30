package com.example.techstore;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegistroActivity extends AppCompatActivity {

    EditText etNombre, etEmail, etPassword;
    Spinner spinnerRol;
    Button btnRegistrar, btnVolver;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spinnerRol = findViewById(R.id.spinnerRol);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnVolver = findViewById(R.id.btnVolver);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String[] roles = {"comprador", "vendedor"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, roles);
        spinnerRol.setAdapter(adapter);

        btnVolver.setOnClickListener(v -> finish());

        btnRegistrar.setOnClickListener(v -> registrar());
    }

    private void registrar() {

        String nombre = etNombre.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPassword.getText().toString();
        String rol = spinnerRol.getSelectedItem().toString();

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Completa todo", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {

                    String uid = mAuth.getCurrentUser().getUid();

                    HashMap<String, Object> user = new HashMap<>();
                    user.put("nombre", nombre);
                    user.put("email", email);
                    user.put("rol", rol);

                    db.collection("usuarios").document(uid)
                            .set(user);

                    Toast.makeText(this, "Registrado ✅", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show()
                );
    }
}