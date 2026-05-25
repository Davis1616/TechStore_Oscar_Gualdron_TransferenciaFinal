package com.example.techstore;

import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class AgregarProductoActivity extends AppCompatActivity {

    private EditText etNombre;
    private EditText etPrecio;
    private EditText etDescripcion;

    private Button btnImagen;
    private Button btnGuardar;

    private Uri imageUri;

    private FirebaseFirestore db;
    private FirebaseStorage storage;

    private ActivityResultLauncher<String> seleccionarImagenLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agregar_producto);

        inicializarVistas();

        db = FirebaseFirestore.getInstance();
        storage = FirebaseStorage.getInstance();

        configurarSelectorImagen();

        btnImagen.setOnClickListener(v ->
                seleccionarImagenLauncher.launch("image/*")
        );

        btnGuardar.setOnClickListener(v -> guardarProducto());
    }

    private void inicializarVistas() {

        etNombre = findViewById(R.id.etNombre);
        etPrecio = findViewById(R.id.etPrecio);
        etDescripcion = findViewById(R.id.etDescripcion);

        btnImagen = findViewById(R.id.btnImagen);
        btnGuardar = findViewById(R.id.btnGuardar);
    }

    private void configurarSelectorImagen() {

        seleccionarImagenLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {

                    if (uri != null) {

                        imageUri = uri;

                        Toast.makeText(
                                this,
                                getString(R.string.imagen_seleccionada),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }

    private void guardarProducto() {

        String nombre = etNombre.getText().toString().trim();
        String precio = etPrecio.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();

        if (nombre.isEmpty()
                || precio.isEmpty()
                || descripcion.isEmpty()
                || imageUri == null) {

            Toast.makeText(
                    this,
                    getString(R.string.completar_campos),
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        StorageReference referencia = storage.getReference()
                .child("productos/" + System.currentTimeMillis());

        referencia.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        referencia.getDownloadUrl().addOnSuccessListener(uri -> {

                            HashMap<String, Object> producto = new HashMap<>();

                            producto.put("nombre", nombre);
                            producto.put("precio", precio);
                            producto.put("descripcion", descripcion);
                            producto.put("imagen", uri.toString());

                            db.collection("productos")
                                    .add(producto)
                                    .addOnSuccessListener(documentReference -> {

                                        Toast.makeText(
                                                this,
                                                getString(R.string.producto_guardado),
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        finish();
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(
                                                    this,
                                                    getString(R.string.error_guardar_producto),
                                                    Toast.LENGTH_SHORT
                                            ).show()
                                    );
                        })
                )
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                getString(R.string.error_subir_imagen),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }
}