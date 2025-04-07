package com.springboot.MyTodoList.model;

import java.time.OffsetDateTime;

public class Kpi {
    private Long kpiId;
    private Long usuarioId;
    private String nombreKpi;
    private String descripcion;
    private double valorActual;
    private double meta;
    private OffsetDateTime fechaRegistro;

    public Long getKpiId() {
        return kpiId;
    }

    public void setKpiId(Long kpiId) {
        this.kpiId = kpiId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNombreKpi() {
        return nombreKpi;
    }

    public void setNombreKpi(String nombreKpi) {
        this.nombreKpi = nombreKpi;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getValorActual() {
        return valorActual;
    }

    public void setValorActual(double valorActual) {
        this.valorActual = valorActual;
    }

    public double getMeta() {
        return meta;
    }

    public void setMeta(double meta) {
        this.meta = meta;
    }

    public OffsetDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(OffsetDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }
}
