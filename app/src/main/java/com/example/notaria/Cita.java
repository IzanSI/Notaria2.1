package com.example.notaria;

import java.util.Date;

public class Cita {
    private int id;  // Agregar el identificador único
    private String notario;
    private String sala;
    private Date fecha;
    private String hora;
    private String descripcion;

    // Constructor actualizado para incluir el id
    public Cita(int id, String notario, String sala, Date fecha, String hora, String descripcion) {
        this.id = id;
        this.notario = notario;
        this.sala = sala;
        this.fecha = fecha;
        this.hora = hora;
        this.descripcion = descripcion;
    }

    // Getter y setter para el id
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNotario() {
        return notario;
    }

    public String getSala() {
        return sala;
    }

    public Date getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
