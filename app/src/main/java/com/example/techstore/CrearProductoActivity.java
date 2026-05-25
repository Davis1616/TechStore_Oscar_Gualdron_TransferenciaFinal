package com.example.techstore;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CrearProductoActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etPrecio;
    private EditText etDescripcion;
    private EditText etImagenUrl;

    private FirebaseFirestore db;

    private String vendedorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_agregar_producto);

        etNombre = findViewById(R.id.etNombre);

        etPrecio = findViewById(R.id.etPrecio);

        etDescripcion = findViewById(R.id.etDescripcion);

        etImagenUrl = findViewById(R.id.etImagenUrl);

        Button btnGuardar =
                findViewById(R.id.btnGuardar);

        Button btnVolver =
                findViewById(R.id.btnVolver);

        db = FirebaseFirestore.getInstance();

        FirebaseAuth auth =
                FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    getString(
                            R.string.debes_iniciar_sesion
                    ),
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }

        vendedorId =
                auth.getCurrentUser().getUid();

        btnVolver.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v ->
                guardarProducto()
        );
    }

    private void guardarProducto() {

        String nombre =
                etNombre.getText()
                        .toString()
                        .trim();

        String precio =
                etPrecio.getText()
                        .toString()
                        .trim();

        String descripcion =
                etDescripcion.getText()
                        .toString()
                        .trim();

        String imagenUrl =
                etImagenUrl.getText()
                        .toString()
                        .trim();

        if (nombre.isEmpty()
                || precio.isEmpty()
                || descripcion.isEmpty()
                || imagenUrl.isEmpty()) {

            Toast.makeText(
                    this,
                    getString(
                            R.string.completa_campos
                    ),
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        HashMap<String, Object> producto =
                new HashMap<>();

        producto.put("nombre", nombre);

        producto.put("precio", precio);

        producto.put(
                "descripcion",
                descripcion
        );

        producto.put("imagen", imagenUrl);

        producto.put(
                "vendedorId",
                vendedorId
        );

        db.collection("productos")
                .add(producto)
                .addOnSuccessListener(doc -> {

                    Toast.makeText(
                            this,
                            getString(
                                    R.string.producto_guardado
                            ),
                            Toast.LENGTH_SHORT
                    ).show();

                    limpiarCampos();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                getString(
                                        R.string.error_guardar_producto
                                ),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void limpiarCampos() {

        etNombre.setText("");

        etPrecio.setText("");

        etDescripcion.setText("");

        etImagenUrl.setText("");
    }
}