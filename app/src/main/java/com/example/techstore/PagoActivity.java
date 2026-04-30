package com.example.techstore;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class PagoActivity extends AppCompatActivity {

    WebView webView;
    FirebaseFirestore db;

    String total;
    String ordenId;
    double lat, lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        webView = new WebView(this);
        setContentView(webView);

        db = FirebaseFirestore.getInstance();

        // 🔥 RECIBIR DATOS CORRECTOS
        total = getIntent().getStringExtra("total");
        ordenId = getIntent().getStringExtra("ordenId");
        lat = getIntent().getDoubleExtra("lat", 0);
        lng = getIntent().getDoubleExtra("lng", 0);

        // 🔥 SIMULACIÓN ePayco
        String html = "<html><body style='text-align:center;margin-top:200px;'>"
                + "<h2>ePayco</h2>"
                + "<p>Procesando pago...</p>"
                + "</body></html>";

        webView.loadData(html, "text/html", "UTF-8");

        new Handler().postDelayed(this::confirmarPago, 3000);
    }

    private void confirmarPago() {

        if (ordenId == null) return;

        // 🔥 SOLO ACTUALIZAR (NO CREAR OTRA)
        HashMap<String, Object> update = new HashMap<>();
        update.put("estado", "pagado");
        update.put("lat", lat);
        update.put("lng", lng);
        update.put("fecha", System.currentTimeMillis());

        db.collection("ordenes")
                .document(ordenId)
                .update(update)
                .addOnSuccessListener(unused -> {

                    webView.loadData(
                            "<h2 style='text-align:center;margin-top:200px;'>Transacción realizada exitosamente ✅</h2>",
                            "text/html",
                            "UTF-8"
                    );

                    limpiarCarrito();

                    new Handler().postDelayed(() -> {
                        startActivity(new Intent(this, HistorialActivity.class));
                        finish();
                    }, 2000);
                });
    }

    private void limpiarCarrito() {
        db.collection("carrito")
                .get()
                .addOnSuccessListener(query -> {
                    for (var doc : query) {
                        doc.getReference().delete();
                    }
                });
    }
}