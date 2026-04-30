package com.example.techstore;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MisProductosActivity extends AppCompatActivity {

    Button btnVolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_productos);

        btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> finish());
    }
}