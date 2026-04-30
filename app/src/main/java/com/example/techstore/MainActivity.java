package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnComprador, btnVendedor, btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar botones
        btnComprador = findViewById(R.id.btnComprador);
        btnVendedor = findViewById(R.id.btnVendedor);
        btnAdmin = findViewById(R.id.btnAdmin);

        // Eventos de click
        btnComprador.setOnClickListener(v -> abrirCatalogo());
        btnVendedor.setOnClickListener(v -> abrirVendedor());
        btnAdmin.setOnClickListener(v -> abrirAdmin());
    }

    private void abrirCatalogo() {
        Intent intent = new Intent(MainActivity.this, CatalogoActivity.class);
        startActivity(intent);
    }

    private void abrirVendedor() {
        Intent intent = new Intent(MainActivity.this, VendedorActivity.class);
        startActivity(intent);
    }

    private void abrirAdmin() {
        Intent intent = new Intent(MainActivity.this, AdminActivity.class);
        startActivity(intent);
    }
}