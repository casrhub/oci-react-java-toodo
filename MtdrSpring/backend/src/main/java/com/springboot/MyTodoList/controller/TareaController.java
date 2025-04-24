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
    return tareaService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Tarea> getTareaById(@PathVariable Long id) {
    return tareaService
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public Tarea createTarea(@RequestBody Tarea tarea) {
    return tareaService.save(tarea);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Tarea> updateTarea(@PathVariable Long id, @RequestBody Tarea newData) {
    Tarea updated = tareaService.update(id, newData);
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTarea(@PathVariable Long id) {
    boolean deleted = tareaService.deleteById(id);
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

    Tarea updated = tareaService.updateAssignee(id, usuarioId);
    return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
  }

  /* ---------- completar ---------- */

  @PutMapping("/{id}/complete")
  public ResponseEntity<Tarea> markAsComplete(
      @PathVariable Long id, @RequestBody Map<String, Object> payload) {
    try {
      String estado = (String) payload.get("estado");

      Object horasObj = payload.get("horasReales");
      BigDecimal horasReales = null;

      if (horasObj instanceof Number) {
        horasReales = new BigDecimal(((Number) horasObj).toString());
      } else if (horasObj instanceof String) {
        horasReales = new BigDecimal((String) horasObj);
      }

      Tarea updated = tareaService.markAsComplete(id, estado, horasReales);
      return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();

    } catch (Exception e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /* ---------- deadline ---------- */

  @PutMapping("/{id}/deadline")
  public ResponseEntity<Tarea> updateDeadline(
      @PathVariable Long id, @RequestBody Map<String, String> payload) {
    String deadlineStr = payload.get("deadline");

    try {
      OffsetDateTime deadline = OffsetDateTime.parse(deadlineStr);
      Tarea updated = tareaService.updateDeadline(id, deadline);

      return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    } catch (DateTimeParseException e) {
      return ResponseEntity.badRequest().build();
    }
  }

  /* ---------- Get tasks by user ---------- */
  @GetMapping("/user/{usuarioId}")
  public ResponseEntity<List<Tarea>> getTareasByUsuario(@PathVariable Long usuarioId) {
    List<Tarea> tareas = tareaService.findByUsuarioId(usuarioId);
    return ResponseEntity.ok(tareas);
  }
}
