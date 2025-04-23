package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Tarea;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TareaRepository extends JpaRepository<Tarea, Long> {
    // Horas trabajadas por equipo por sprint
    @Query(value = "SELECT COALESCE(SUM(t.horas_reales), 0) " +
               "FROM ADMIN.TAREAS t " +
               "WHERE t.estado = 'completado' " +
               "AND t.equipo_id = :equipoId " +
               "AND t.sprint_id = :sprintId", 
       nativeQuery = true)
BigDecimal sumHorasRealesByEquipoAndSprint(@Param("equipoId") Long equipoId,
                                           @Param("sprintId") Long sprintId);

    // Tareas completadas por equipo por sprint
    @Query(value = "SELECT COUNT(*) " +
               "FROM ADMIN.TAREAS t " +
               "WHERE t.estado = 'completado' " +
               "AND t.equipo_id = :equipoId " +
               "AND t.sprint_id = :sprintId", 
       nativeQuery = true)
Long countCompletedTareasByEquipoAndSprint(@Param("equipoId") Long equipoId,
                                           @Param("sprintId") Long sprintId);

    // Horas trabajadas por usuario en un sprint
    @Query(value = "SELECT COALESCE(SUM(t.horas_reales), 0) " +
               "FROM ADMIN.TAREAS t " +
               "WHERE t.estado = 'completado' " +
               "AND t.usuario_id = :usuarioId " +
               "AND t.sprint_id = :sprintId", 
       nativeQuery = true)
BigDecimal sumHorasRealesByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
                                            @Param("sprintId") Long sprintId);

    // Tareas completadas por usuario en un sprint
    @Query(value = "SELECT COUNT(*) " +
    "FROM ADMIN.TAREAS t " +
    "WHERE t.estado = 'completado' " +
    "AND t.usuario_id = :usuarioId " +
    "AND t.sprint_id = :sprintId", 
    nativeQuery = true)
    Long countCompletedTareasByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
                                @Param("sprintId") Long sprintId);

}