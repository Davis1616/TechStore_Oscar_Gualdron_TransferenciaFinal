package com.example.techstore;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class AgregarProductoActivity extends AppCompatActivity {

    EditText etNombre, etPrecio, etDescripcion;
    Button btnImagen, btnGuardar;

    Uri imageUri;

    FirebaseFirestore db;
    FirebaseStorage storage;

    private static final int PICK_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        // Referencias
        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnImagen = findViewById(R.id.btnImagen);
        btnGuardar = findViewById(R.id.btnGuardar);

        // Firebase
        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        // Seleccionar imagen
        btnImagen.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, PICK_IMAGE);
        });

        // Guardar producto
        btnGuardar.setOnClickListener(v -> {

            String nombre = etNombre.getText().toString().trim();
            String precio = etPrecio.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            // Validaciones
            if (nombre.isEmpty() || precio.isEmpty() || descripcion.isEmpty() || imageUri == null) {
                Toast.makeText(this, "Completa todos los campos y selecciona imagen", Toast.LENGTH_SHORT).show();
                return;
            }

            StorageReference ref = storage.getReference()
                    .child("productos/" + System.currentTimeMillis());

            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot ->
                            ref.getDownloadUrl().addOnSuccessListener(uri -> {

                                HashMap<String, Object> producto = new HashMap<>();
                                producto.put("nombre", nombre);
                                producto.put("precio", precio);
                                producto.put("descripcion", descripcion);
                                producto.put("imagen", uri.toString());

                                db.collection("productos")
                                        .add(producto)
                                        .addOnSuccessListener(doc -> {
                                            Toast.makeText(this, "Producto guardado", Toast.LENGTH_SHORT).show();
                                            finish();
                                        })
                                        .addOnFailureListener(e ->
                                                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                                        );

                            })
                    )
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error al subir imagen", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    // Recibir imagen
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show();
        }
    }
}