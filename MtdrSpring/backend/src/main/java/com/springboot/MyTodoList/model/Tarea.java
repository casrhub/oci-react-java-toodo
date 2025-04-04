package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "TAREAS", schema = "ADMIN") // Replace schema if needed
public class Tarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAREA_ID")
    private Long tareaId;

    @Column(name = "USUARIO_ID")
    private Long usuarioId;

    @Column(name = "PROYECTO_ID")
    private Long proyectoId;

    @Column(name = "EQUIPO_ID")
    private Long equipoId;

    @Column(name = "TITULO")
    private String titulo;

    @Column(name = "DESCRIPCION", columnDefinition = "CLOB")
    private String descripcion;

    @Column(name = "ESTADO")
    private String estado;

    @Column(name = "HORAS_ESTIMADAS", precision = 5, scale = 2)
    private BigDecimal horasEstimadas;

    @Column(name = "HORAS_REALES", precision = 5, scale = 2)
    private BigDecimal horasReales;

    @Column(name = "FECHA_CREACION")
    private OffsetDateTime fechaCreacion;

    @Column(name = "DEADLINE")
    private OffsetDateTime deadline;

    // Constructors
    public Tarea() {}

    // Getters & Setters
    public Long getTareaId() {
        return tareaId;
    }

    public void setTareaId(Long tareaId) {
        this.tareaId = tareaId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public Long getProyectoId() {
        return proyectoId;
    }

    public void setProyectoId(Long proyectoId) {
        this.proyectoId = proyectoId;
    }

    public Long getEquipoId() {
        return equipoId;
    }

    public void setEquipoId(Long equipoId) {
        this.equipoId = equipoId;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getHorasEstimadas() {
        return horasEstimadas;
    }

    public void setHorasEstimadas(BigDecimal horasEstimadas) {
        this.horasEstimadas = horasEstimadas;
    }

    public BigDecimal getHorasReales() {
        return horasReales;
    }

    public void setHorasReales(BigDecimal horasReales) {
        this.horasReales = horasReales;
    }

    public OffsetDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(OffsetDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public OffsetDateTime getDeadline() {
        return deadline;
    }

    public void setDeadline(OffsetDateTime deadline) {
        this.deadline = deadline;
    }

    @ManyToOne
    @JoinColumn(name = "SPRINT_ID")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Sprint sprint;

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }
}
