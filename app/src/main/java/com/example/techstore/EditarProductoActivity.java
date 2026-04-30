package com.example.techstore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditarProductoActivity extends AppCompatActivity {

    Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_producto);

        btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> finish());

        // Simulación de guardado
        Button btnGuardar = findViewById(R.id.btnGuardar);
        if (btnGuardar != null) {
            btnGuardar.setOnClickListener(v ->
                    Toast.makeText(this, "Producto actualizado", Toast.LENGTH_SHORT).show());
        }
    }
}