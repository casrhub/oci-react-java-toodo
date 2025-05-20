package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.service.TareaService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tareas")
public class TareaController {

  @Autowired private TareaService tareaService;

  /* ---------- CRUD básico ---------- */

  @GetMapping
  public List<Tarea> getAllTareas() {
    return tareaService.findAllTasks();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Tarea> getTareaById(@PathVariable Long id) {
    return tareaService
        .findTaskById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public Tarea createTarea(@RequestBody Tarea tarea) {
    return tareaService.createTask(tarea);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Tarea> updateTarea(@PathVariable Long id, @RequestBody Tarea newData) {
    Tarea updatedTarea = tareaService.updateTask(id, newData);
    return updatedTarea != null ? ResponseEntity.ok(updatedTarea) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTarea(@PathVariable Long id) {
    boolean deleted = tareaService.deleteTask(id);
    return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
  }

  /* ---------- PATCH SOLO ASIGNACIÓN ---------- */

  /** Cambia únicamente el usuario asignado sin tocar otros campos. */
  @PatchMapping("/{id}/assignee")
  public ResponseEntity<Tarea> updateAssignee(
      @PathVariable Long id, @RequestBody Map<String, Long> payload) {
    Long usuarioId = payload.get("usuarioId");
    if (usuarioId == null) {
      return ResponseEntity.badRequest().build();
    }

    Tarea updatedTarea = tareaService.updateTaskAssignee(id, usuarioId);
    return updatedTarea != null ? ResponseEntity.ok(updatedTarea) : ResponseEntity.notFound().build();
  }

  /* ---------- completar ---------- */

  @PutMapping("/{id}/complete")
  public ResponseEntity<Tarea> markAsComplete(
      @PathVariable Long id, @RequestBody Map<String, Object> payload) {
    try {
      String estado = (String) payload.get("estado");

      Object horasObject = payload.get("horasReales");
      BigDecimal horasReales = null;

      if (horasObject instanceof Number) {
        horasReales = new BigDecimal(((Number) horasObject).toString());
      } else if (horasObject instanceof String) {
        horasReales = new BigDecimal((String) horasObject);
      }

      Tarea updatedTarea = tareaService.markTaskAsComplete(id, estado, horasReales);
      return updatedTarea != null ? ResponseEntity.ok(updatedTarea) : ResponseEntity.notFound().build();

    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /* ---------- deadline ---------- */

  @PutMapping("/{id}/deadline")
  public ResponseEntity<Tarea> updateDTareaeadline(
      @PathVariable Long id, @RequestBody Map<String, String> payload) {
    String deadlineStr = payload.get("deadline");

    try {
      OffsetDateTime deadline = OffsetDateTime.parse(deadlineStr);
      Tarea updatedTarea = tareaService.updateTaskDeadline(id, deadline);

      return updatedTarea != null ? ResponseEntity.ok(updatedTarea) : ResponseEntity.notFound().build();
    } catch (DateTimeParseException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /* ---------- KPIs ---------- */

  @GetMapping("/equipo/{equipoId}/sprint/{sprintId}/horas-trabajadas")
  public ResponseEntity<BigDecimal> getHorasByEquipoAndSprint(
      @PathVariable Long equipoId, @PathVariable Long sprintId) {
    BigDecimal horas = tareaService.getTeamSprintRealHours(equipoId, sprintId);
    return ResponseEntity.ok(horas != null ? horas : BigDecimal.ZERO);
  }

  @GetMapping("/equipo/{equipoId}/sprint/{sprintId}/tareas-completadas")
  public ResponseEntity<Long> getCompletedTareasByEquipoAndSprint(
      @PathVariable Long equipoId, @PathVariable Long sprintId) {
    Long count = tareaService.countTeamSprintCompletedTasks(equipoId, sprintId);
    return ResponseEntity.ok(count != null ? count : 0L);
  }

  @GetMapping("/usuario/{usuarioId}/sprint/{sprintId}/tareas-completadas")
  public ResponseEntity<Long> getCompletedTareasByUsuarioAndSprint(
      @PathVariable Long usuarioId, @PathVariable Long sprintId) {
    Long count = tareaService.countUserSprintCompletedTasks(usuarioId, sprintId);
    return ResponseEntity.ok(count != null ? count : 0L);
  }

  @GetMapping("/usuario/{usuarioId}/summary")
  public Map<String, Long> resumenPorUsuario(@PathVariable Long usuarioId) {
    return tareaService.getUserSummary(usuarioId);
  }

  @GetMapping("/equipo/{equipoId}/summary")
  public Map<String, Long> resumenPorEquipo(@PathVariable Long equipoId) {
    return tareaService.getTeamSummary(equipoId);
  }

  @GetMapping("/usuario/{usuarioId}/sprint/{sprintId}/horas-trabajadas")
  public ResponseEntity<BigDecimal> getHorasByUsuarioAndSprint(
      @PathVariable Long usuarioId, @PathVariable Long sprintId) {
    BigDecimal horasReales = tareaService.getUserSprintRealHours(usuarioId, sprintId);
    return ResponseEntity.ok(horasReales != null ? horasReales : BigDecimal.ZERO);
  }

  @GetMapping("/usuario/{usuarioId}/sprint/{sprintId}/horas-estimadas")
  public ResponseEntity<BigDecimal> getHorasEstimadasByUsuarioAndSprint(
      @PathVariable Long usuarioId, @PathVariable Long sprintId) {
    BigDecimal horasEstimadas = tareaService.getUserSprintEstimatedHours(usuarioId, sprintId);
    return ResponseEntity.ok(horasEstimadas != null ? horasEstimadas : BigDecimal.ZERO);
  }

  @GetMapping("/usuario/{usuarioId}/sprint/{sprintId}/tareas-completadas-antes-deadline")
  public ResponseEntity<Long> getCompletedTareasBeforeDeadlineByUsuarioAndSprint(
      @PathVariable Long usuarioId, @PathVariable Long sprintId) {
    Long countCompletedTareas =
        tareaService.countUserSprintTasksBeforeDeadline(usuarioId, sprintId);
    return ResponseEntity.ok(countCompletedTareas != null ? countCompletedTareas : 0L);
  }

  @GetMapping("/usuario/{usuarioId}/sprint/{sprintId}/tareas-completadas-despues-deadline")
  public ResponseEntity<Long> getCompletedTareasAfterDeadlineByUsuarioAndSprint(
      @PathVariable Long usuarioId, @PathVariable Long sprintId) {
    Long countCompletedTareas =
        tareaService.countUserSprintTasksAfterDeadline(usuarioId, sprintId);
    return ResponseEntity.ok(countCompletedTareas != null ? countCompletedTareas : 0L);
  }

  @GetMapping("/usuario/{usuarioId}/sprint/{sprintId}/tareas-asignadas")
  public ResponseEntity<Long> getAsignedTareasByUsuarioAndSprint(
      @PathVariable Long usuarioId, @PathVariable Long sprintId) {
    Long countAssigned = tareaService.countUserSprintAssignedTasks(usuarioId, sprintId);
    return ResponseEntity.ok(countAssigned != null ? countAssigned : 0L);
  }

  /* ---------- Get tasks by user (from dev) ---------- */
  @GetMapping("/user/{usuarioId}")
  public ResponseEntity<List<Tarea>> getTareasByUsuario(@PathVariable Long usuarioId) {
    List<Tarea> tareas = tareaService.findTasksByUserId(usuarioId);
    return ResponseEntity.ok(tareas);
  }

  @GetMapping("/equipo/{equipoId}/sprint/{sprintId}/usuario/{usuarioId}/horas-trabajadas")
  public ResponseEntity<BigDecimal> getHorasByEquipoSprintAndUsuario(
      @PathVariable Long equipoId, @PathVariable Long sprintId, @PathVariable Long usuarioId) {
    BigDecimal horasReales = tareaService.getTeamSprintUserRealHours(equipoId, sprintId, usuarioId);
    return ResponseEntity.ok(horasReales != null ? horasReales : BigDecimal.ZERO);
  }
}
