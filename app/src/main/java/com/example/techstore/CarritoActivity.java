package com.example.techstore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.*;

public class CarritoActivity extends AppCompatActivity {

    RecyclerView recycler;
    CarritoAdapter adapter;
    List<Carrito> lista;

    FirebaseFirestore db;
    FirebaseAuth auth;

    TextView txtTotal;
    Button btnPagar, btnVolver;

    int totalGlobal = 0;

    // 🔥 UBICACIÓN
    FusedLocationProviderClient fusedLocationClient;
    double lat = 0;
    double lng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        recycler = findViewById(R.id.recyclerCarrito);
        txtTotal = findViewById(R.id.txtTotal);
        btnPagar = findViewById(R.id.btnPagar);
        btnVolver = findViewById(R.id.btnVolver);

        recycler.setLayoutManager(new LinearLayoutManager(this));

        lista = new ArrayList<>();
        adapter = new CarritoAdapter(lista);
        recycler.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // 🔥 INICIAR UBICACIÓN
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        obtenerUbicacion();

        cargarCarrito();

        btnVolver.setOnClickListener(v -> finish());

        btnPagar.setOnClickListener(v -> {

            if (totalGlobal <= 0) {
                Toast.makeText(this, "Carrito vacío", Toast.LENGTH_SHORT).show();
                return;
            }

            if (auth.getCurrentUser() == null) {
                Toast.makeText(this, "Debes iniciar sesión", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = auth.getCurrentUser().getUid();

            HashMap<String, Object> orden = new HashMap<>();
            orden.put("total", String.valueOf(totalGlobal));
            orden.put("estado", "pendiente");
            orden.put("userId", userId);
            orden.put("timestamp", System.currentTimeMillis());

            db.collection("ordenes")
                    .add(orden)
                    .addOnSuccessListener(doc -> {

                        String ordenId = doc.getId();

                        Intent intent = new Intent(this, PagoActivity.class);
                        intent.putExtra("ordenId", ordenId);
                        intent.putExtra("total", String.valueOf(totalGlobal));

                        // 🔥 ENVIAR UBICACIÓN
                        intent.putExtra("lat", lat);
                        intent.putExtra("lng", lng);

                        startActivity(intent);
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Error creando orden", Toast.LENGTH_SHORT).show()
                    );
        });
    }

    // 🔥 OBTENER UBICACIÓN
    private void obtenerUbicacion() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        lat = location.getLatitude();
                        lng = location.getLongitude();
                    }
                });
    }

    // 🔥 RESPUESTA PERMISOS
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacion();
            } else {
                Toast.makeText(this, "Permiso de ubicación requerido", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void cargarCarrito() {

        db.collection("carrito")
                .get()
                .addOnSuccessListener(query -> {

                    lista.clear();
                    totalGlobal = 0;

                    for (QueryDocumentSnapshot doc : query) {

                        Carrito p = doc.toObject(Carrito.class);

                        if (p == null) continue;

                        if (p.cantidad <= 0) p.cantidad = 1;

                        int precio = 0;
                        try {
                            precio = Integer.parseInt(p.precio);
                        } catch (Exception e) {
                            precio = 0;
                        }

                        totalGlobal += precio * p.cantidad;

                        lista.add(p);
                    }

                    txtTotal.setText("Total: $" + totalGlobal);
                    adapter.notifyDataSetChanged();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCarrito();
    }
}