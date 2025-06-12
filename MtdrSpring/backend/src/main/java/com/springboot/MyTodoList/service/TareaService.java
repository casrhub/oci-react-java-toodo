package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.SubTarea;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.SubTareaRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TareaService {

  @Autowired private TareaRepository tareaRepository;
  @Autowired private SubTareaRepository subTareaRepository;

  public List<Tarea> findAll() {
    return tareaRepository.findAll();
  }

  public List<Tarea> findByUsuarioId(Long usuarioId) {
    return tareaRepository.findByUsuarioId(usuarioId);
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

  public boolean deleteById(Long id) {
    if (tareaRepository.existsById(id)) {
      tareaRepository.deleteById(id);
      return true;
    }
    return false;
  }

  public Tarea update(Long id, Tarea newData) {
    return tareaRepository
        .findById(id)
        .map(
            t -> {
              t.setTitulo(newData.getTitulo());
              t.setDescripcion(newData.getDescripcion());
              t.setEstado(newData.getEstado());
              t.setUsuarioId(newData.getUsuarioId());
              t.setHorasEstimadas(newData.getHorasEstimadas());
              t.setHorasReales(newData.getHorasReales());
              t.setDeadline(newData.getDeadline());
              t.setEquipoId(newData.getEquipoId());
              t.setProyectoId(newData.getProyectoId());
              return tareaRepository.save(t);
            })
        .orElse(null);
  }

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

  public BigDecimal getHorasRealesByEquipoAndSprint(Long equipoId, Long sprintId) {
    return tareaRepository.sumHorasRealesByEquipoAndSprint(equipoId, sprintId);
  }

  public Long countCompletedTareasByEquipoAndSprint(Long equipoId, Long sprintId) {
    return tareaRepository.countCompletedTareasByEquipoAndSprint(equipoId, sprintId);
  }

  public BigDecimal sumHorasRealesByUsuarioAndSprint(Long usuarioId, Long sprintId) {
    return tareaRepository.sumHorasRealesByUsuarioAndSprint(usuarioId, sprintId);
  }

  public Long countCompletedTareasByUsuarioAndSprint(Long usuarioId, Long sprintId) {
    return tareaRepository.countCompletedTareasByUsuarioAndSprint(usuarioId, sprintId);
  }

  public Map<String, Long> resumenPorEquipo(Long equipoId) {
    long asignadas = tareaRepository.countByEquipo(equipoId);
    long antes = tareaRepository.countCompletedBeforeDeadlineTeam(equipoId);
    long despues = tareaRepository.countCompletedAfterDeadlineTeam(equipoId);

    return Map.of(
        "asignadas", asignadas,
        "completadasAntes", antes,
        "completadasDespues", despues);
  }

  public Map<String, Long> resumenPorUsuario(Long usuarioId) {
    long asignadas = tareaRepository.countByUsuario(usuarioId);
    long antes = tareaRepository.countCompletedBeforeDeadline(usuarioId);
    long despues = tareaRepository.countCompletedAfterDeadline(usuarioId);

    return Map.of(
        "asignadas", asignadas,
        "completadasAntes", antes,
        "completadasDespues", despues);
  }

  public BigDecimal sumHorasEstimadasByUsuarioAndSprint(Long usuarioId, Long sprintId) {
    return tareaRepository.sumHorasEstimadasByUsuarioAndSprint(usuarioId, sprintId);
  }

  public Long countCompletedTareasBeforeDeadlineByUsuarioAndSprint(Long usuarioId, Long sprintId) {
    return tareaRepository.countCompletedTareasBeforeDeadlineByUsuarioAndSprint(
        usuarioId, sprintId);
  }

  public Long countCompletedTareasAfterDeadlineByUsuarioAndSprint(Long usuarioId, Long sprintId) {
    return tareaRepository.countCompletedTareasAfterDeadlineByUsuarioAndSprint(usuarioId, sprintId);
  }

  public Long countAsignedTareasByUsuarioAndSprint(Long usuarioId, Long sprintId) {
    return tareaRepository.countAsignedTareasByUsuarioAndSprint(usuarioId, sprintId);
  }

  public Map<String, Object> calculateUserKPIs(Long usuarioId) {
    List<Tarea> userTasks = findByUsuarioId(usuarioId);
    Map<String, Object> kpis = new HashMap<>();

    long totalTasks = userTasks.size();
    long completedTasks =
        userTasks.stream().filter(t -> "completado".equalsIgnoreCase(t.getEstado())).count();
    long inProgressTasks =
        userTasks.stream().filter(t -> "en progreso".equalsIgnoreCase(t.getEstado())).count();
    long pendingTasks =
        userTasks.stream().filter(t -> "pendiente".equalsIgnoreCase(t.getEstado())).count();

    BigDecimal totalEstimatedHours =
        userTasks.stream()
            .map(t -> t.getHorasEstimadas() != null ? t.getHorasEstimadas() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalRealHours =
        userTasks.stream()
            .filter(t -> t.getHorasReales() != null)
            .map(Tarea::getHorasReales)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal completedEstimatedHours =
        userTasks.stream()
            .filter(t -> "completado".equalsIgnoreCase(t.getEstado()))
            .map(t -> t.getHorasEstimadas() != null ? t.getHorasEstimadas() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal completedRealHours =
        userTasks.stream()
            .filter(t -> "completado".equalsIgnoreCase(t.getEstado()))
            .map(t -> t.getHorasReales() != null ? t.getHorasReales() : BigDecimal.ZERO)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

    double completionRate = totalTasks > 0 ? (completedTasks * 100.0) / totalTasks : 0.0;

    kpis.put("totalTasks", totalTasks);
    kpis.put("completedTasks", completedTasks);
    kpis.put("inProgressTasks", inProgressTasks);
    kpis.put("pendingTasks", pendingTasks);
    kpis.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
    kpis.put("totalEstimatedHours", totalEstimatedHours);
    kpis.put("totalRealHours", totalRealHours);
    kpis.put("completedEstimatedHours", completedEstimatedHours);
    kpis.put("completedRealHours", completedRealHours);

    return kpis;
  }

  public BigDecimal sumHorasRealesByEquipoAndSprintAndUsuario(
      Long equipoId, Long sprintId, Long usuarioId) {
    return tareaRepository.sumHorasRealesByEquipoAndSprintAndUsuario(equipoId, sprintId, usuarioId);
  }
}
