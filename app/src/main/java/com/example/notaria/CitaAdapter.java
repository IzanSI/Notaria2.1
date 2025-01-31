package com.example.notaria;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CitaAdapter extends RecyclerView.Adapter<CitaAdapter.CitaViewHolder> {
    private ArrayList<Cita> citas;
    private OnCitaDeleteListener deleteListener;

    public CitaAdapter(ArrayList<Cita> citas, OnCitaDeleteListener deleteListener) {
        this.citas = citas;
        this.deleteListener = deleteListener;
    }

    @Override
    public CitaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_cita, parent, false);
        return new CitaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CitaViewHolder holder, int position) {
        Cita cita = citas.get(position);
        holder.notarioTextView.setText(cita.getNotario());
        holder.salaTextView.setText(cita.getSala());

        // Formatear la fecha
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        holder.fechaTextView.setText(dateFormat.format(cita.getFecha()));

        // Mostrar la hora y descripción
        holder.horaTextView.setText(cita.getHora());
        holder.descripcionTextView.setText(cita.getDescripcion());

        // Configurar el botón de eliminar
        holder.btnEliminar.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onCitaDelete(cita.getId(), position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    public static class CitaViewHolder extends RecyclerView.ViewHolder {
        public TextView notarioTextView;
        public TextView salaTextView;
        public TextView fechaTextView;
        public TextView horaTextView;
        public TextView descripcionTextView;
        public Button btnEliminar;

        public CitaViewHolder(View itemView) {
            super(itemView);
            notarioTextView = itemView.findViewById(R.id.textViewNotario);
            salaTextView = itemView.findViewById(R.id.textViewSala);
            fechaTextView = itemView.findViewById(R.id.textViewFecha);
            horaTextView = itemView.findViewById(R.id.textViewHora);
            descripcionTextView = itemView.findViewById(R.id.textViewDescripcion);
            btnEliminar = itemView.findViewById(R.id.btnEliminarCita);
        }
    }

    // Interfaz para comunicar la eliminación al Activity o Fragment
    public interface OnCitaDeleteListener {
        void onCitaDelete(int id, int position);
    }
}
