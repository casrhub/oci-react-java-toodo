package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.repository.SprintRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SprintService {

    @Autowired
    private SprintRepository sprintRepository;

    public List<Sprint> findAll() {
        return sprintRepository.findAll();
    }

    public Optional<Sprint> findById(Long id) {
        return sprintRepository.findById(id);
    }

    public Sprint save(Sprint sprint) {
        return sprintRepository.save(sprint);
    }

    public Sprint update(Long id, Sprint updatedSprint) {
        return sprintRepository.findById(id).map(existing -> {
            existing.setNombre(updatedSprint.getNombre());
            existing.setProyectoId(updatedSprint.getProyectoId());
            existing.setFechaInicio(updatedSprint.getFechaInicio());
            existing.setFechaFin(updatedSprint.getFechaFin());
            existing.setEstado(updatedSprint.getEstado());
            return sprintRepository.save(existing);
        }).orElse(null);
    }

    public boolean delete(Long id) {
        if (sprintRepository.existsById(id)) {
            sprintRepository.deleteById(id);
            return true;
        }
        return false;
    }
}