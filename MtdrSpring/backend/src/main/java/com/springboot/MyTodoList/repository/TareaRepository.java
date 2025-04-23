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


    // Horas estimadas por usuario en un sprint (de tareas completadas)
    @Query(value = "SELECT COALESCE(SUM(t.horas_estimadas), 0) " +
               "FROM ADMIN.TAREAS t " +
               "WHERE t.estado = 'completado' " +
               "AND t.usuario_id = :usuarioId " +
               "AND t.sprint_id = :sprintId", 
       nativeQuery = true)
BigDecimal sumHorasEstimadasByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
                                            @Param("sprintId") Long sprintId);

    // Tareas completadas despues del deadline por usuario en un sprint
    @Query(value = "SELECT COUNT(*) " +
    "FROM ADMIN.TAREAS t " +
    "WHERE t.estado = 'completado' " +
    "AND t.usuario_id = :usuarioId " +
    "AND t.sprint_id = :sprintId " +
    "AND t.fecha_finalizacion > t.deadline", 
    nativeQuery = true)
    Long countCompletedTareasAfterDeadlineByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
                                @Param("sprintId") Long sprintId);

    // Tareas completadas antes del deadline por usuario en un sprint
    @Query(value = "SELECT COUNT(*) " +
    "FROM ADMIN.TAREAS t " +
    "WHERE t.estado = 'completado' " +
    "AND t.usuario_id = :usuarioId " +
    "AND t.sprint_id = :sprintId " +
    "AND t.fecha_finalizacion < t.deadline", 
    nativeQuery = true)
    Long countCompletedTareasBeforeDeadlineByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
                                @Param("sprintId") Long sprintId);


   // Tareas asignadas por usuario en un sprint
   @Query(value = "SELECT COUNT(*) " +
   "FROM ADMIN.TAREAS t " +
   "WHERE t.usuario_id = :usuarioId " +
   "AND t.sprint_id = :sprintId", 
   nativeQuery = true)
   Long countAsignedTareasByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
                               @Param("sprintId") Long sprintId);
}