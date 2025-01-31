package com.example.notaria;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Referencias a los botones
        Button btnCrearCita = findViewById(R.id.btnCrearCita);
        Button btnVerCitas = findViewById(R.id.btnVerCitas);
        Button btnConfiguracion = findViewById(R.id.btnConfiguracion);

        // Navegar a la actividad de Crear Cita
        btnCrearCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CrearCitaActivity.class);
                startActivity(intent);
            }
        });

        btnVerCitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VerCitasActivity.class);
                startActivity(intent);
            }
        });
    }
}
