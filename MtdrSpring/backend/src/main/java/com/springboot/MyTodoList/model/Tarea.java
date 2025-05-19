package com.springboot.MyTodoList.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "TAREAS", schema = "ADMIN")
public class Tarea {

    public Tarea(Long tareaId, String titulo, String descripcion, String estadoTarea) {
        this.tareaId = tareaId;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.estadoTarea = estadoTarea;
    }

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

    @Column(name = "estadoTarea")
    private String estadoTarea;

    @Column(name = "HORAS_ESTIMADAS", precision = 5, scale = 2)
    private BigDecimal horasEstimadas;

    @Column(name = "HORAS_REALES", precision = 5, scale = 2)
    private BigDecimal horasReales;

    @Column(name = "FECHA_CREACION")
    private OffsetDateTime fechaCreacion;

    @Column(name = "DEADLINE")
    private OffsetDateTime fechaLimite;

    @ManyToOne
    @JoinColumn(name = "SPRINT_ID")
    @JsonBackReference
    private Sprint sprint;

    // Constructor por defecto (requerido por JPA)
    public Tarea() {}

    // Getters y Setters
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

    public String getestadoTarea() {
        return estadoTarea;
    }

    public void setestadoTarea(String estadoTarea) {
        this.estadoTarea = estadoTarea;
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

    public OffsetDateTime getFechaLimite() {
        return fechaLimite;
    }

    public void setFechaLimite(OffsetDateTime fechaLimite) {
        this.fechaLimite = fechaLimite;
    }

    public Sprint getSprint() {
        return sprint;
    }

    public void setSprint(Sprint sprint) {
        this.sprint = sprint;
    }

    @JsonProperty("sprintId")
    public Long getSprintId() {
        return sprint != null ? sprint.getSprintId() : null;
    }
}
