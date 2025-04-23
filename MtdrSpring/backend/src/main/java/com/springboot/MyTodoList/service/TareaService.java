// src/main/java/com/springboot/MyTodoList/service/TareaService.java
package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.SubTarea;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.SubTareaRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TareaService {

  @Autowired private TareaRepository tareaRepository;

  @Autowired private SubTareaRepository subTareaRepository;

  /* ---------------- Lectura / creación ---------------- */

  public List<Tarea> findAll() {
    return tareaRepository.findAll();
  }

  public Optional<Tarea> findById(Long id) {
    return tareaRepository.findById(id);
  }

  public Tarea save(Tarea tarea) {
    BigDecimal estimated = tarea.getHorasEstimadas();
    if (estimated != null && estimated.compareTo(new BigDecimal("4")) > 0) {
      tarea.setHorasEstimadas(new BigDecimal("4"));
    }

    Tarea saved = tareaRepository.save(tarea);
    createDefaultSubTareas(saved);
    return saved;
  }

  /* ---------------- Sub-tareas por defecto ---------------- */

  private void createDefaultSubTareas(Tarea tarea) {
    BigDecimal estimated = tarea.getHorasEstimadas();
    if (estimated == null || estimated.compareTo(new BigDecimal("4")) <= 0) return;

    BigDecimal remaining = estimated.subtract(new BigDecimal("4"));
    int number = remaining.divide(new BigDecimal("4"), 0, BigDecimal.ROUND_UP).intValue();

    for (int i = 0; i < number; i++) {
      BigDecimal subHours = remaining.subtract(new BigDecimal(i * 4));
      if (subHours.compareTo(new BigDecimal("4")) > 0) subHours = new BigDecimal("4");

      SubTarea sub = new SubTarea();
      sub.setTarea(tarea);
      sub.setTitulo("Subtarea " + (i + 1));
      sub.setDescripcion("Descripción pendiente");
      sub.setEstado("pendiente");
      sub.setHorasEstimadas(subHours);
      sub.setHorasReales(BigDecimal.ZERO);
      sub.setFechaCreacion(OffsetDateTime.now());

      subTareaRepository.save(sub);
    }
  }

  /* ---------------- Borrado ---------------- */

  public boolean deleteById(Long id) {
    if (tareaRepository.existsById(id)) {
      tareaRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /* ---------------- Actualizaciones ---------------- */

  /** PUT completo */
  public Tarea update(Long id, Tarea newData) {
    return tareaRepository
        .findById(id)
        .map(
            t -> {
              t.setTitulo(newData.getTitulo());
              t.setDescripcion(newData.getDescripcion());
              t.setEstado(newData.getEstado());
              t.setUsuarioId(newData.getUsuarioId()); // ← aseguramos que no se pierda
              t.setHorasEstimadas(newData.getHorasEstimadas());
              t.setHorasReales(newData.getHorasReales());
              t.setDeadline(newData.getDeadline());
              t.setEquipoId(newData.getEquipoId());
              t.setProyectoId(newData.getProyectoId());
              return tareaRepository.save(t);
            })
        .orElse(null);
  }

  /** PATCH: sólo cambia el desarrollador asignado */
  public Tarea updateAssignee(Long id, Long usuarioId) {
    return tareaRepository
        .findById(id)
        .map(
            t -> {
              t.setUsuarioId(usuarioId);
              return tareaRepository.save(t);
            })
        .orElse(null);
  }

  public Tarea markAsComplete(Long id, String estado, BigDecimal horasReales) {
    return tareaRepository
        .findById(id)
        .map(
            t -> {
              t.setEstado(estado);
              t.setHorasReales(horasReales);
              return tareaRepository.save(t);
            })
        .orElse(null);
  }

  public Tarea updateDeadline(Long id, OffsetDateTime deadline) {
    return tareaRepository
        .findById(id)
        .map(
            t -> {
              t.setDeadline(deadline);
              return tareaRepository.save(t);
            })
        .orElse(null);
  }
}
