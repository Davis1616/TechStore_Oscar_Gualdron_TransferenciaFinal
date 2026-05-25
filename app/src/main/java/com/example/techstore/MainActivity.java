package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity {

    private Button btnComprador;
    private Button btnVendedor;
    private Button btnAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inicializarVistas();
        configurarBotones();
    }

    private void inicializarVistas() {

        btnComprador = findViewById(R.id.btnComprador);
        btnVendedor = findViewById(R.id.btnVendedor);
        btnAdmin = findViewById(R.id.btnAdmin);
    }

    private void configurarBotones() {

        btnComprador.setOnClickListener(v ->
                abrirPantalla(CatalogoActivity.class)
        );

        btnVendedor.setOnClickListener(v ->
                abrirPantalla(VendedorActivity.class)
        );

        btnAdmin.setOnClickListener(v ->
                abrirPantalla(AdminActivity.class)
        );
    }

    private void abrirPantalla(Class<?> destino) {
        startActivity(new Intent(this, destino));
    }
}