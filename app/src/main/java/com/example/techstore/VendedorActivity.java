package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class VendedorActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendedor);

        LinearLayout cardCrear = findViewById(R.id.cardCrear);
        LinearLayout cardMis = findViewById(R.id.cardMis);
        android.widget.Button btnCerrar = findViewById(R.id.btnVolver);

        cardCrear.setOnClickListener(v ->
                startActivity(new Intent(this, CrearProductoActivity.class))
        );

        cardMis.setOnClickListener(v ->
                startActivity(new Intent(this, MisProductosActivity.class))
        );

        btnCerrar.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });
    }
}