package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Tarea;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    // You can add custom queries here later if needed
}
