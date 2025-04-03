package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.SubTarea;
import com.springboot.MyTodoList.repository.SubTareaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubTareaService {

    @Autowired
    private SubTareaRepository repository;

    public List<SubTarea> findAll() {
        return repository.findAll();
    }

    public Optional<SubTarea> findById(Long id) {
        return repository.findById(id);
    }

    public List<SubTarea> findByTareaId(Long tareaId) {
        return repository.findByTarea_TareaId(tareaId);
    }

    public SubTarea save(SubTarea subTarea) {
        return repository.save(subTarea);
    }

    public boolean delete(Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }
}