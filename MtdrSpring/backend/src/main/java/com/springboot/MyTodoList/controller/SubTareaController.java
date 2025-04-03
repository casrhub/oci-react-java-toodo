package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.SubTarea;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.service.SubTareaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.springboot.MyTodoList.repository.TareaRepository;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/subtareas")
public class SubTareaController {

    private static final Logger logger = LoggerFactory.getLogger(SubTareaController.class);
    @Autowired
private TareaRepository tareaRepository;



    @Autowired
    private SubTareaService subTareaService;

    @GetMapping
    public List<SubTarea> getAll() {
        logger.debug("Fetching all subtareas");
        return subTareaService.findAll();
    }

    @GetMapping("/byTarea/{tareaId}")
    public List<SubTarea> getByTarea(@PathVariable Long tareaId) {
        return subTareaService.findByTareaId(tareaId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SubTarea> getById(@PathVariable Long id) {
        return subTareaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

  @PostMapping
public SubTarea create(@RequestBody Map<String, Object> payload) {
    Long tareaId = Long.valueOf(payload.get("tareaId").toString());
    Tarea tarea = tareaRepository.findById(tareaId).orElseThrow(() -> new RuntimeException("Tarea not found"));

    SubTarea subTarea = new SubTarea();
    subTarea.setTarea(tarea);
    subTarea.setTitulo((String) payload.get("titulo"));
    subTarea.setDescripcion((String) payload.get("descripcion"));
    subTarea.setEstado((String) payload.get("estado"));
    subTarea.setHorasEstimadas(new BigDecimal(payload.get("horasEstimadas").toString()));
    subTarea.setHorasReales(new BigDecimal(payload.get("horasReales").toString()));

    if (payload.get("fechaCreacion") != null)
        subTarea.setFechaCreacion(OffsetDateTime.parse((String) payload.get("fechaCreacion")));

    if (payload.get("deadline") != null)
        subTarea.setDeadline(OffsetDateTime.parse((String) payload.get("deadline")));

    return subTareaService.save(subTarea);
}


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return subTareaService.delete(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
