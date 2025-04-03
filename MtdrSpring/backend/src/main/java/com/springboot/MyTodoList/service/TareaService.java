package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.SubTarea;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.SubTareaRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TareaService {

    @Autowired
    private TareaRepository tareaRepository;

    @Autowired
    private SubTareaRepository subTareaRepository;

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
    
    private void createDefaultSubTareas(Tarea tarea) {
        BigDecimal estimated = tarea.getHorasEstimadas();
        if (estimated == null || estimated.compareTo(new BigDecimal("4")) <= 0) return;
    
        BigDecimal remaining = estimated.subtract(new BigDecimal("4"));
        int numberOfSubtasks = remaining.divide(new BigDecimal("4"), 0, BigDecimal.ROUND_UP).intValue();
    
        for (int i = 0; i < numberOfSubtasks; i++) {
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
        return tareaRepository.findById(id)
                .map(tarea -> {
                    tarea.setTitulo(newData.getTitulo());
                    tarea.setDescripcion(newData.getDescripcion());
                    tarea.setEstado(newData.getEstado());
                    tarea.setHorasEstimadas(newData.getHorasEstimadas());
                    tarea.setHorasReales(newData.getHorasReales());
                    tarea.setDeadline(newData.getDeadline());
                    return tareaRepository.save(tarea);
                })
                .orElse(null);
    }

    public Tarea markAsComplete(Long id, String estado, BigDecimal horasReales) {
        Optional<Tarea> tareaOpt = tareaRepository.findById(id);
        if (tareaOpt.isPresent()) {
            Tarea tarea = tareaOpt.get();
            tarea.setEstado(estado);
            tarea.setHorasReales(horasReales);
            return tareaRepository.save(tarea);
        }
        return null;
    }

    public Tarea updateDeadline(Long id, OffsetDateTime deadline) {
        Optional<Tarea> tareaOpt = tareaRepository.findById(id);
        if (tareaOpt.isPresent()) {
            Tarea tarea = tareaOpt.get();
            tarea.setDeadline(deadline);
            return tareaRepository.save(tarea);
        }
        return null;
    }
} 
