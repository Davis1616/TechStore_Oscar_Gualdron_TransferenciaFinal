package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin, btnIrRegistro, btnRecuperar;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnIrRegistro = findViewById(R.id.btnIrRegistro);
        btnRecuperar = findViewById(R.id.btnRecuperar); // 🔥 AGREGA ESTE BOTÓN EN XML

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // 🔥 LOGIN
        btnLogin.setOnClickListener(v -> {

            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {

                        if (task.isSuccessful()) {

                            String uid = mAuth.getCurrentUser().getUid();

                            db.collection("usuarios").document(uid)
                                    .get()
                                    .addOnSuccessListener(doc -> {

                                        String rol = doc.getString("rol");

                                        Intent intent;

                                        if ("admin".equals(rol)) {
                                            intent = new Intent(this, AdminActivity.class);
                                        } else if ("vendedor".equals(rol)) {
                                            intent = new Intent(this, VendedorActivity.class);
                                        } else {
                                            intent = new Intent(this, CatalogoActivity.class);
                                        }

                                        startActivity(intent);
                                        finish();
                                    });

                        } else {
                            Toast.makeText(this, "Login error", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        //  IR A REGISTRO
        btnIrRegistro.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class))
        );

        // RECUPERAR CONTRASEÑA
        btnRecuperar.setOnClickListener(v -> {

            String correo = etEmail.getText().toString().trim();

            if (correo.isEmpty()) {
                Toast.makeText(this, "Ingresa tu correo", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(correo)
                    .addOnSuccessListener(aVoid ->
                            Toast.makeText(this, "Correo enviado ✅", Toast.LENGTH_LONG).show()
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                    );
        });
    }
}