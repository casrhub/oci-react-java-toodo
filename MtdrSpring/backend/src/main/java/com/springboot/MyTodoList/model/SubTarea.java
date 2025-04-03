package com.springboot.MyTodoList.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
@Entity
@Table(name = "SUB_TAREAS", schema = "ADMIN")
public class SubTarea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SUB_TAREA_ID")
    private Long subTareaId;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAREA_ID", nullable = false)
    private Tarea tarea;

    private String titulo;
    private String descripcion;
    private String estado;

    @Column(name = "HORAS_ESTIMADAS")
    private BigDecimal horasEstimadas;

    @Column(name = "HORAS_REALES")
    private BigDecimal horasReales;

    @Column(name = "FECHA_CREACION")
    private OffsetDateTime fechaCreacion;

    private OffsetDateTime deadline;

    // Getters and setters
    public Long getSubTareaId() { return subTareaId; }
    public void setSubTareaId(Long subTareaId) { this.subTareaId = subTareaId; }

    public Tarea getTarea() { return tarea; }
    public void setTarea(Tarea tarea) { this.tarea = tarea; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public BigDecimal getHorasEstimadas() { return horasEstimadas; }
    public void setHorasEstimadas(BigDecimal horasEstimadas) { this.horasEstimadas = horasEstimadas; }

    public BigDecimal getHorasReales() { return horasReales; }
    public void setHorasReales(BigDecimal horasReales) { this.horasReales = horasReales; }

    public OffsetDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(OffsetDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }

    public OffsetDateTime getDeadline() { return deadline; }
    public void setDeadline(OffsetDateTime deadline) { this.deadline = deadline; }
}
