package com.example.techstore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditarProductoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_editar_producto);

        Button btnVolver =
                findViewById(R.id.btnVolver);

        Button btnGuardar =
                findViewById(R.id.btnGuardar);

        btnVolver.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v ->
                Toast.makeText(
                        this,
                        getString(
                                R.string.producto_actualizado
                        ),
                        Toast.LENGTH_SHORT
                ).show()
        );
    }
}