package com.example.techstore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.*;
import com.google.firebase.firestore.FirebaseFirestore;

public class UbicacionActivity extends AppCompatActivity {

    TextView txtUbicacion;
    Button btnContinuar, btnUbicar;

    FusedLocationProviderClient fusedLocationClient;

    double latitud = 0;
    double longitud = 0;

    String ordenId;
    String total;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ubicacion);

        txtUbicacion = findViewById(R.id.txtUbicacion);
        btnContinuar = findViewById(R.id.btnContinuar);
        btnUbicar = findViewById(R.id.btnUbicar);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        db = FirebaseFirestore.getInstance();

        // 🔥 RECIBIR DATOS DE LA ORDEN
        ordenId = getIntent().getStringExtra("ordenId");
        total = getIntent().getStringExtra("total");

        // BOTÓN OBTENER UBICACIÓN
        btnUbicar.setOnClickListener(v -> obtenerUbicacion());

        // CONTINUAR AL PAGO
        btnContinuar.setOnClickListener(v -> {

            if (latitud == 0 && longitud == 0) {
                Toast.makeText(this, "Primero obtén tu ubicación", Toast.LENGTH_SHORT).show();
                return;
            }

            if (ordenId == null) {
                Toast.makeText(this, "Error: orden no encontrada", Toast.LENGTH_SHORT).show();
                return;
            }

            // 🔥 GUARDAR UBICACIÓN EN FIRESTORE (REQUERIMIENTO)
            db.collection("ordenes")
                    .document(ordenId)
                    .update(
                            "lat", latitud,
                            "lng", longitud
                    )
                    .addOnSuccessListener(aVoid -> {

                        // 🚀 IR A PAGO
                        Intent i = new Intent(UbicacionActivity.this, PagoActivity.class);
                        i.putExtra("ordenId", ordenId);
                        i.putExtra("total", total);
                        startActivity(i);

                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error guardando ubicación", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    private void obtenerUbicacion() {

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {

                    if (location != null) {
                        mostrarUbicacion(location);
                    } else {
                        Toast.makeText(this, "No se pudo obtener ubicación", Toast.LENGTH_SHORT).show();
                    }

                });
    }

    private void mostrarUbicacion(Location location) {
        latitud = location.getLatitude();
        longitud = location.getLongitude();

        txtUbicacion.setText("Lat: " + latitud + "\nLng: " + longitud);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100 && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            obtenerUbicacion();
        }
    }
}