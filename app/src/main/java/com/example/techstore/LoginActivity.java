package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);

        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnIrRegistro = findViewById(R.id.btnIrRegistro);
        Button btnRecuperar = findViewById(R.id.btnRecuperar);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> iniciarSesion());

        btnIrRegistro.setOnClickListener(v ->
                startActivity(new Intent(this, RegistroActivity.class))
        );

        btnRecuperar.setOnClickListener(v -> recuperarContrasena());
    }

    private void iniciarSesion() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.completa_campos),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (!task.isSuccessful() || mAuth.getCurrentUser() == null) {
                        Toast.makeText(this,
                                getString(R.string.error_login),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String uid = mAuth.getCurrentUser().getUid();
                    cargarRolUsuario(uid);
                });
    }

    private void cargarRolUsuario(String uid) {

        db.collection("usuarios")
                .document(uid)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) {
                        Toast.makeText(this,
                                "Usuario no existe en Firestore",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String rol = doc.getString("rol");

                    if (rol == null || rol.isEmpty()) {
                        Toast.makeText(this,
                                "Error: usuario sin rol",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Intent intent;

                    if (rol.equals("admin")) {
                        intent = new Intent(this, AdminActivity.class);

                    } else if (rol.equals("vendedor")) {
                        intent = new Intent(this, VendedorActivity.class);

                    } else {
                        intent = new Intent(this, CatalogoActivity.class);
                    }

                    startActivity(intent);
                    finish();

                })
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error Firestore: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show()
                );
    }

    private void recuperarContrasena() {

        String correo = etEmail.getText().toString().trim();

        if (correo.isEmpty()) {
            Toast.makeText(this,
                    getString(R.string.ingresa_correo),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.sendPasswordResetEmail(correo)
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(this,
                                getString(R.string.correo_enviado),
                                Toast.LENGTH_LONG).show()
                )
                .addOnFailureListener(e ->
                        Toast.makeText(this,
                                "Error: " + e.getMessage(),
                                Toast.LENGTH_LONG).show()
                );
    }
}