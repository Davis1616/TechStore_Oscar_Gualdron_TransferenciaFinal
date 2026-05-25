package com.example.techstore;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CarritoActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_CODE = 1;

    private CarritoAdapter adapter;
    private final List<Carrito> lista = new ArrayList<>();

    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private TextView txtTotal;

    private int totalGlobal = 0;

    private FusedLocationProviderClient fusedLocationClient;

    private double lat = 0;
    private double lng = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carrito);

        RecyclerView recycler =
                findViewById(R.id.recyclerCarrito);

        txtTotal =
                findViewById(R.id.txtTotal);

        Button btnPagar =
                findViewById(R.id.btnPagar);

        Button btnVolver =
                findViewById(R.id.btnVolver);

        recycler.setLayoutManager(
                new LinearLayoutManager(this)
        );

        adapter = new CarritoAdapter(lista);

        recycler.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        auth = FirebaseAuth.getInstance();

        fusedLocationClient =
                LocationServices.getFusedLocationProviderClient(this);

        obtenerUbicacion();

        cargarCarrito();

        btnVolver.setOnClickListener(v -> finish());

        btnPagar.setOnClickListener(v -> realizarPago());
    }

    private void realizarPago() {

        if (totalGlobal <= 0) {

            Toast.makeText(
                    this,
                    getString(R.string.carrito_vacio),
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        if (auth.getCurrentUser() == null) {

            Toast.makeText(
                    this,
                    getString(R.string.debes_iniciar_sesion),
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        String userId =
                auth.getCurrentUser().getUid();

        HashMap<String, Object> orden =
                new HashMap<>();

        orden.put(
                "total",
                String.valueOf(totalGlobal)
        );

        orden.put(
                "estado",
                "pendiente"
        );

        orden.put(
                "userId",
                userId
        );

        orden.put(
                "timestamp",
                System.currentTimeMillis()
        );

        db.collection("ordenes")
                .add(orden)
                .addOnSuccessListener(doc -> {

                    String ordenId = doc.getId();

                    Intent intent =
                            new Intent(
                                    this,
                                    PagoActivity.class
                            );

                    intent.putExtra(
                            "ordenId",
                            ordenId
                    );

                    intent.putExtra(
                            "total",
                            String.valueOf(totalGlobal)
                    );

                    intent.putExtra("lat", lat);

                    intent.putExtra("lng", lng);

                    startActivity(intent);
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                getString(R.string.error_creando_orden),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    private void obtenerUbicacion() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },
                    LOCATION_PERMISSION_CODE
            );

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

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {

        super.onRequestPermissionsResult(
                requestCode,
                permissions,
                grantResults
        );

        if (requestCode == LOCATION_PERMISSION_CODE) {

            if (grantResults.length > 0
                    && grantResults[0]
                    == PackageManager.PERMISSION_GRANTED) {

                obtenerUbicacion();

            } else {

                Toast.makeText(
                        this,
                        getString(R.string.permiso_ubicacion),
                        Toast.LENGTH_SHORT
                ).show();
            }
        }
    }

    private void cargarCarrito() {

        db.collection("carrito")
                .get()
                .addOnSuccessListener(query -> {

                    int cantidadAnterior =
                            lista.size();

                    lista.clear();

                    totalGlobal = 0;

                    for (QueryDocumentSnapshot doc : query) {

                        Carrito producto =
                                doc.toObject(Carrito.class);

                        if (producto.getCantidad() <= 0) {

                            producto.setCantidad(1);
                        }

                        int precio;

                        try {

                            precio = Integer.parseInt(
                                    producto.getPrecio()
                            );

                        } catch (NumberFormatException e) {

                            precio = 0;
                        }

                        totalGlobal +=
                                precio * producto.getCantidad();

                        lista.add(producto);
                    }

                    txtTotal.setText(
                            getString(
                                    R.string.total_con_precio,
                                    totalGlobal
                            )
                    );

                    if (cantidadAnterior > 0) {

                        adapter.notifyItemRangeRemoved(
                                0,
                                cantidadAnterior
                        );
                    }

                    adapter.notifyItemRangeInserted(
                            0,
                            lista.size()
                    );
                })
                .addOnFailureListener(e ->
                        Toast.makeText(
                                this,
                                getString(R.string.error_cargar_carrito),
                                Toast.LENGTH_SHORT
                        ).show()
                );
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarCarrito();
    }
}