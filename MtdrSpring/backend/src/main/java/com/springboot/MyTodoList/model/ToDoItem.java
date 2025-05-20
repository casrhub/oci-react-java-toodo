package com.springboot.MyTodoList.model;

import java.time.OffsetDateTime;
import javax.persistence.*;

@Entity
@Table(name = "TODOITEM")
public class ToDoItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "DESCRIPTION")
    private String descripcion;

    @Column(name = "CREATION_TS")
    private OffsetDateTime fechaCreacion;

    @Column(name = "DONE")
    private boolean completado;

    @Column(name = "DEADLINE", nullable = true)
    private OffsetDateTime fechaLimite;

    // Constructor base (requerido por JPA)
    public ToDoItem() {}

    // Constructor completo
    public ToDoItem(int id, String descripcion, OffsetDateTime fechaCreacion, boolean completado, OffsetDateTime fechaLimite) {
        this.id = id;
        this.descripcion = descripcion;
        this.fechaCreacion = fechaCreacion;
        this.completado = completado;
        this.fechaLimite = fechaLimite;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public boolean isCompletado() {
        return completado;
    }

    public void setCompletado(boolean completado) {
        this.completado = completado;
    }

    public OffsetDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(OffsetDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    @Override
    public String toString() {
        return "ToDoItem{" +
                "id=" + id +
                ", descripcion='" + descripcion + '\'' +
                ", fechaCreacion=" + fechaCreacion +
                ", completado=" + completado +
                ", fechaLimite=" + fechaLimite +
                '}';
    }
}
