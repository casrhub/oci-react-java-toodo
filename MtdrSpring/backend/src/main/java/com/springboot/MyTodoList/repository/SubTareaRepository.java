package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.SubTarea;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubTareaRepository extends JpaRepository<SubTarea, Long> {
    List<SubTarea> findByTarea_TareaId(Long tareaId);
}