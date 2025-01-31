package com.example.notaria;

import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class VerCitasActivity extends AppCompatActivity implements CitaAdapter.OnCitaDeleteListener {

    private RecyclerView recyclerView;
    private CitaAdapter citaAdapter;
    private ArrayList<Cita> citas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_citas);

        // Configurar el RecyclerView
        recyclerView = findViewById(R.id.recyclerViewCitas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Iniciar la tarea para obtener citas desde el servidor
        new ObtenerCitasTask().execute("http://192.168.123.219/notaria/get_citas.php");
    }

    // Clase interna para manejar la petición HTTP en segundo plano
    private class ObtenerCitasTask extends AsyncTask<String, Void, ArrayList<Cita>> {

        @Override
        protected ArrayList<Cita> doInBackground(String... urls) {
            ArrayList<Cita> citas = new ArrayList<>();

            try {
                // Configurar la conexión HTTP
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Leer la respuesta del servidor
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                int data = reader.read();
                while (data != -1) {
                    stringBuilder.append((char) data);
                    data = reader.read();
                }
                reader.close();

                // Convertir la respuesta en un array JSON
                JSONArray jsonArray = new JSONArray(stringBuilder.toString());

                // Recorrer el JSON y extraer los datos de cada cita
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject citaJson = jsonArray.getJSONObject(i);

                    // Obtener el id de la cita
                    int id = citaJson.getInt("id");  // Asegúrate de que el JSON tenga un campo 'id'

                    // Obtener otros campos de la cita
                    String notario = citaJson.getString("notario");
                    String sala = citaJson.getString("sala");
                    String fecha = citaJson.getString("fecha");
                    String hora = citaJson.getString("hora");
                    String descripcion = citaJson.getString("descripcion");

                    // Convertir la fecha de String a un objeto Date
                    java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd");
                    java.util.Date parsedDate = dateFormat.parse(fecha);

                    // Crear una nueva cita con el id y otros detalles
                    citas.add(new Cita(id, notario, sala, parsedDate, hora, descripcion));
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return citas;
        }

        @Override
        protected void onPostExecute(ArrayList<Cita> citas) {
            super.onPostExecute(citas);

            if (citas != null && !citas.isEmpty()) {
                // Mostrar las citas en el RecyclerView con el adaptador
                VerCitasActivity.this.citas = citas;  // Guardar las citas
                citaAdapter = new CitaAdapter(citas, VerCitasActivity.this);
                recyclerView.setAdapter(citaAdapter);
            } else {
                Toast.makeText(VerCitasActivity.this, "No se encontraron citas.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onCitaDelete(int id, int position) {
        // Realizar la eliminación en el servidor (enviar una petición HTTP)
        new EliminarCitaTask().execute("http://192.168.123.219/notaria/eliminar_cita.php", String.valueOf(id));

        // Eliminar la cita localmente del RecyclerView
        citas.remove(position);
        citaAdapter.notifyItemRemoved(position);

        Toast.makeText(this, "Cita eliminada", Toast.LENGTH_SHORT).show();
    }

    // Clase interna para eliminar una cita en el servidor
    private class EliminarCitaTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                // Configurar la conexión HTTP para eliminar la cita
                String urlString = params[0] + "?id=" + params[1];
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                // Leer la respuesta del servidor
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                StringBuilder stringBuilder = new StringBuilder();
                int data = reader.read();
                while (data != -1) {
                    stringBuilder.append((char) data);
                    data = reader.read();
                }
                reader.close();

                // Retornar el resultado de la operación
                return stringBuilder.toString();

            } catch (Exception e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            // Aquí puedes manejar la respuesta del servidor si es necesario
            if (result.equals("Error")) {
                Toast.makeText(VerCitasActivity.this, "Error al eliminar la cita en el servidor", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(VerCitasActivity.this, "Cita eliminada correctamente del servidor", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
