package com.example.techstore;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class CrearProductoActivity extends AppCompatActivity {

    EditText etNombre, etPrecio, etDescripcion, etImagenUrl;
    Button btnGuardar, btnVolver;

    FirebaseFirestore db;
    FirebaseAuth auth;

    String vendedorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);
        etImagenUrl = findViewById(R.id.etImagenUrl);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnVolver = findViewById(R.id.btnVolver);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() == null) {
            Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        vendedorId = auth.getCurrentUser().getUid();

        btnVolver.setOnClickListener(v -> finish());

        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void guardarProducto() {

        String nombre = etNombre.getText().toString().trim();
        String precio = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String imagenUrl = etImagenUrl.getText().toString().trim();

        if (nombre.isEmpty() || precio.isEmpty() || descripcion.isEmpty() || imagenUrl.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> producto = new HashMap<>();
        producto.put("nombre", nombre);
        producto.put("precio", precio);
        producto.put("descripcion", descripcion);
        producto.put("imagen", imagenUrl); // 🔥 IMPORTANTE: mismo nombre que usas en adapter
        producto.put("vendedorId", vendedorId);

        db.collection("productos")
                .add(producto)
                .addOnSuccessListener(doc -> {
                    Toast.makeText(this, "Producto guardado ✅", Toast.LENGTH_SHORT).show();
                    limpiarCampos();
                })
                .addOnFailureListener(e ->
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                );
    }

    private void limpiarCampos() {
        etNombre.setText("");
        etPrecio.setText("");
        etDescripcion.setText("");
        etImagenUrl.setText("");
    }
}