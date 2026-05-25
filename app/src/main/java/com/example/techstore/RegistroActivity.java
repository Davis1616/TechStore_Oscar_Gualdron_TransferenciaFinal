package com.example.techstore;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etEmail;
    private EditText etPassword;
    private Spinner spinnerRol;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spinnerRol = findViewById(R.id.spinnerRol);

        findViewById(R.id.btnVolver)
                .setOnClickListener(v -> finish());

        findViewById(R.id.btnRegistrar)
                .setOnClickListener(v -> registrarUsuario());

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        String[] roles = {
                getString(R.string.comprador),
                getString(R.string.vendedor)
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                roles
        );

        spinnerRol.setAdapter(adapter);
    }

    private void registrarUsuario() {

        String nombre = etNombre.getText().toString().trim();

        String email = etEmail.getText().toString().trim();

        String pass = etPassword.getText().toString().trim();

        String rol = spinnerRol.getSelectedItem() != null
                ? spinnerRol.getSelectedItem().toString()
                : getString(R.string.comprador);

        if (nombre.isEmpty() || email.isEmpty() || pass.isEmpty()) {

            Toast.makeText(
                    this,
                    getString(R.string.completa_campos),
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnSuccessListener(authResult -> {

                    if (mAuth.getCurrentUser() == null) {
                        return;
                    }

                    String uid = mAuth.getCurrentUser().getUid();

                    HashMap<String, Object> user = new HashMap<>();

                    user.put("nombre", nombre);
                    user.put("email", email);
                    user.put("rol", rol);

                    db.collection("usuarios")
                            .document(uid)
                            .set(user)
                            .addOnSuccessListener(unused -> {

                                Toast.makeText(
                                        this,
                                        getString(R.string.registro_exitoso),
                                        Toast.LENGTH_SHORT
                                ).show();

                                finish();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(
                                            this,
                                            getString(
                                                    R.string.error_guardando_usuario,
                                                    e.getMessage()
                                            ),
                                            Toast.LENGTH_SHORT
                                    ).show()
                            );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                getString(
                                        R.string.error_generico,
                                        e.getMessage()
                                ),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}