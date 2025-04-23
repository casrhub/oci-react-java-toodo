package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.service.TareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tareas")
public class TareaController {

    @Autowired
    private TareaService tareaService;

    @GetMapping
    public List<Tarea> getAllTareas() {
        return tareaService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tarea> getTareaById(@PathVariable Long id) {
        return tareaService.findById(id)
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

 @PutMapping("/{id}/complete")
public ResponseEntity<Tarea> markAsComplete(@PathVariable Long id, @RequestBody Map<String, Object> payload) {
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

        return updated != null
            ? ResponseEntity.ok(updated)
            : ResponseEntity.notFound().build();

    } catch (Exception e) {
        return ResponseEntity.badRequest().build();
    }
}

//deadline PUT controller

@PutMapping("/{id}/deadline")
public ResponseEntity<Tarea> updateDeadline(@PathVariable Long id, @RequestBody Map<String, String> payload) {
    String deadlineStr = payload.get("deadline");

    try {
        OffsetDateTime deadline = OffsetDateTime.parse(deadlineStr);
        Tarea updated = tareaService.updateDeadline(id, deadline);

        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    } catch (DateTimeParseException e) {
        return ResponseEntity.badRequest().build();
    }
}

// Horas trabajadas por equipo por sprint
// RUTA EJEMPLO:
// GET /tareas/equipo/1/sprint/3/horas-trabajadas
@GetMapping("/equipo/{equipoId}/sprint/{sprintId}/horas-trabajadas")
public ResponseEntity<BigDecimal> getHorasByEquipoAndSprint(@PathVariable Long equipoId,
                                                             @PathVariable Long sprintId) {
    BigDecimal horas = tareaService.getHorasRealesByEquipoAndSprint(equipoId, sprintId);
    return ResponseEntity.ok(horas != null ? horas : BigDecimal.ZERO);
}

// Tareas completadas por equipo por sprint
// RUTA EJEMPLO:
// GET /tareas/equipo/1/sprint/2/tareas-completadas
@GetMapping("/equipo/{equipoId}/sprint/{sprintId}/tareas-completadas")
public ResponseEntity<Long> getCompletedTareasByEquipoAndSprint(@PathVariable Long equipoId,
                                                                 @PathVariable Long sprintId) {
    Long count = tareaService.countCompletedTareasByEquipoAndSprint(equipoId, sprintId);
    return ResponseEntity.ok(count != null ? count : 0L);
}

// Horas trabajadas por usuario en un sprint   
// RUTA EJEMPLO:
// GET /tareas/usuario/7/sprint/2/horas-trabajadas 
@GetMapping("/usuario/{usuarioId}/sprint/{sprintId}/horas-trabajadas")
public ResponseEntity<BigDecimal> getHorasByUsuarioAndSprint(@PathVariable Long usuarioId,
                                                              @PathVariable Long sprintId) {
    BigDecimal horas = tareaService.sumHorasRealesByUsuarioAndSprint(usuarioId, sprintId);
    return ResponseEntity.ok(horas != null ? horas : BigDecimal.ZERO);
}

// Tareas completadas por usuario en un sprint
@GetMapping("/usuario/{usuarioId}/sprint/{sprintId}/tareas-completadas")
public ResponseEntity<Long> getCompletedTareasByUsuarioAndSprint(@PathVariable Long usuarioId,
                                                                 @PathVariable Long sprintId) {
    Long count = tareaService.countCompletedTareasByUsuarioAndSprint(usuarioId, sprintId);
    return ResponseEntity.ok(count != null ? count : 0L);
}

@GetMapping("/usuario/{usuarioId}/summary")
public Map<String, Long> resumenPorUsuario(@PathVariable Long usuarioId) {
    return tareaService.resumenPorUsuario(usuarioId);
}

@GetMapping("/equipo/{equipoId}/summary")
public Map<String, Long> resumenPorEquipo(@PathVariable Long equipoId) {
    return tareaService.resumenPorEquipo(equipoId);
}
}
