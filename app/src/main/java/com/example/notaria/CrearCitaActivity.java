package com.example.notaria;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class CrearCitaActivity extends AppCompatActivity {

    private TextView tvFechaSeleccionada, tvHoraSeleccionada;
    private EditText etNombreNotario, etNumeroSala, etDescripcion;
    private Button btnSeleccionarFecha, btnSeleccionarHora, btnGuardarCita;

    private String fechaSeleccionada = "";
    private String horaSeleccionada = "";
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_cita);

        // Inicializar vistas
        tvFechaSeleccionada = findViewById(R.id.tvFechaSeleccionada);
        tvHoraSeleccionada = findViewById(R.id.tvHoraSeleccionada);
        etNombreNotario = findViewById(R.id.etNombreNotario);
        etNumeroSala = findViewById(R.id.etNumeroSala);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnSeleccionarFecha = findViewById(R.id.btnSeleccionarFecha);
        btnSeleccionarHora = findViewById(R.id.btnSeleccionarHora);
        btnGuardarCita = findViewById(R.id.btnGuardarCita);

        // Selector de fecha con restricción (no fechas pasadas)
        btnSeleccionarFecha.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year1, month1, dayOfMonth) -> {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year1, month1, dayOfMonth);

                if (selectedDate.before(Calendar.getInstance())) {
                    Toast.makeText(this, "No puedes seleccionar una fecha pasada", Toast.LENGTH_SHORT).show();
                } else {
                    fechaSeleccionada = dateFormat.format(selectedDate.getTime());
                    tvFechaSeleccionada.setText(fechaSeleccionada);
                }
            }, year, month, day);

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()); // Restringir fechas pasadas
            datePickerDialog.show();
        });

        // Selector de hora
        btnSeleccionarHora.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute1) -> {
                horaSeleccionada = hourOfDay + ":" + (minute1 < 10 ? "0" + minute1 : minute1);
                tvHoraSeleccionada.setText(horaSeleccionada);
            }, hour, minute, true);
            timePickerDialog.show();
        });

        // Botón Guardar Cita
        btnGuardarCita.setOnClickListener(v -> {
            String notario = etNombreNotario.getText().toString();
            String sala = etNumeroSala.getText().toString();
            String descripcion = etDescripcion.getText().toString();

            if (notario.isEmpty() || sala.isEmpty() || fechaSeleccionada.isEmpty() || horaSeleccionada.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                verificarSalaYGuardar(notario, sala, fechaSeleccionada, horaSeleccionada, descripcion);
            }
        });
    }

    private void verificarSalaYGuardar(String notario, String sala, String fecha, String hora, String descripcion) {
        new Thread(() -> {
            try {
                URL url = new URL("http://192.168.123.219/notaria/save.php");

                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String parametros = "notario=" + notario +
                        "&sala=" + sala +
                        "&fecha=" + fecha +
                        "&hora=" + hora +
                        "&descripcion=" + descripcion;

                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(parametros);
                writer.flush();
                writer.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String respuesta = reader.readLine();
                reader.close();
                conn.disconnect();

                runOnUiThread(() -> {
                    if (respuesta.equals("sala_ocupada")) {
                        Toast.makeText(this, "Error: La sala ya está ocupada en ese horario", Toast.LENGTH_SHORT).show();
                    } else if (respuesta.equals("cita_guardada")) {
                        Toast.makeText(this, "Cita guardada correctamente", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "guardar la cita", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        }).start();
    }
}
