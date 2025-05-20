package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.SubTarea;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubTareaRepository extends JpaRepository<SubTarea, Long> {
  List<SubTarea> findByTarea_TareaId(Long tareaId);
}
