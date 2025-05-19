package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Tarea;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TareaRepository extends JpaRepository<Tarea, Long> {

    /* ---------- KPI QUERIES PER TEAM & SPRINT ---------- */

    // Total actual hours logged by a team in a sprint
    @Query(value = "SELECT COALESCE(SUM(t.horas_reales), 0) " +
            "FROM ADMIN.TAREAS t " +
            "WHERE t.estado = 'completado' " +
            "AND t.equipo_id = :equipoId " +
            "AND t.sprint_id = :sprintId", nativeQuery = true)
    BigDecimal sumHorasRealesByEquipoAndSprint(@Param("equipoId") Long equipoId,
            @Param("sprintId") Long sprintId);

    // Completed tasks for a team in a sprint
    @Query(value = "SELECT COUNT(*) " +
            "FROM ADMIN.TAREAS t " +
            "WHERE t.estado = 'completado' " +
            "AND t.equipo_id = :equipoId " +
            "AND t.sprint_id = :sprintId", nativeQuery = true)
    Long countCompletedTareasByEquipoAndSprint(@Param("equipoId") Long equipoId,
            @Param("sprintId") Long sprintId);

    /* ---------- KPI QUERIES PER USER & SPRINT ---------- */

    // Total actual hours logged by a user in a sprint
    @Query(value = "SELECT COALESCE(SUM(t.horas_reales), 0) " +
            "FROM ADMIN.TAREAS t " +
            "WHERE t.estado = 'completado' " +
            "AND t.usuario_id = :usuarioId " +
            "AND t.sprint_id = :sprintId", nativeQuery = true)
    BigDecimal sumHorasRealesByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
            @Param("sprintId") Long sprintId);

    // Completed tasks by a user in a sprint
    @Query(value = "SELECT COUNT(*) " +
            "FROM ADMIN.TAREAS t " +
            "WHERE t.estado = 'completado' " +
            "AND t.usuario_id = :usuarioId " +
            "AND t.sprint_id = :sprintId", nativeQuery = true)
    Long countCompletedTareasByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
            @Param("sprintId") Long sprintId);

    /* ---------- GLOBAL USER KPI QUERIES ---------- */

    @Query(value = "SELECT COUNT(*) FROM ADMIN.TAREAS WHERE USUARIO_ID = :usuarioId", nativeQuery = true)
    Long countByUsuario(@Param("usuarioId") Long usuarioId);

    @Query(value = "SELECT COUNT(*) FROM ADMIN.TAREAS " +
            "WHERE USUARIO_ID = :usuarioId " +
            "AND ESTADO = 'completado' " +
            "AND DEADLINE IS NOT NULL " +
            "AND DEADLINE >= SYSTIMESTAMP", nativeQuery = true)
    Long countCompletedBeforeDeadline(@Param("usuarioId") Long usuarioId);

    @Query(value = "SELECT COUNT(*) FROM ADMIN.TAREAS " +
            "WHERE USUARIO_ID = :usuarioId " +
            "AND ESTADO = 'completado' " +
            "AND DEADLINE IS NOT NULL " +
            "AND DEADLINE < SYSTIMESTAMP", nativeQuery = true)
    Long countCompletedAfterDeadline(@Param("usuarioId") Long usuarioId);

    /* ---------- GLOBAL TEAM KPI QUERIES ---------- */

    @Query(value = "SELECT COUNT(*) FROM ADMIN.TAREAS WHERE EQUIPO_ID = :equipoId", nativeQuery = true)
    Long countByEquipo(@Param("equipoId") Long equipoId);

    @Query(value = "SELECT COUNT(*) FROM ADMIN.TAREAS " +
            "WHERE EQUIPO_ID = :equipoId " +
            "AND ESTADO = 'completado' " +
            "AND DEADLINE IS NOT NULL " +
            "AND DEADLINE >= SYSTIMESTAMP", nativeQuery = true)
    Long countCompletedBeforeDeadlineTeam(@Param("equipoId") Long equipoId);

    @Query(value = "SELECT COUNT(*) FROM ADMIN.TAREAS " +
            "WHERE EQUIPO_ID = :equipoId " +
            "AND ESTADO = 'completado' " +
            "AND DEADLINE IS NOT NULL " +
            "AND DEADLINE < SYSTIMESTAMP", nativeQuery = true)
    Long countCompletedAfterDeadlineTeam(@Param("equipoId") Long equipoId);

    /* ---------- ADDITIONAL USER-SPRINT KPI QUERIES ---------- */

    // Total estimated hours (completed tasks) for a user in a sprint
    @Query(value = "SELECT COALESCE(SUM(t.horas_estimadas), 0) " +
            "FROM ADMIN.TAREAS t " +
            "WHERE t.estado = 'completado' " +
            "AND t.usuario_id = :usuarioId " +
            "AND t.sprint_id = :sprintId", nativeQuery = true)
    BigDecimal sumHorasEstimadasByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
            @Param("sprintId") Long sprintId);

    // Completed tasks after deadline for a user in a sprint
    @Query(value = "SELECT COUNT(*) " +
            "FROM ADMIN.TAREAS t " +
            "WHERE t.estado = 'completado' " +
            "AND t.usuario_id = :usuarioId " +
            "AND t.sprint_id = :sprintId " +
            "AND t.fecha_finalizacion > t.deadline", nativeQuery = true)
    Long countCompletedTareasAfterDeadlineByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
            @Param("sprintId") Long sprintId);

    // Completed tasks before deadline for a user in a sprint
    @Query(value = "SELECT COUNT(*) " +
            "FROM ADMIN.TAREAS t " +
            "WHERE t.estado = 'completado' " +
            "AND t.usuario_id = :usuarioId " +
            "AND t.sprint_id = :sprintId " +
            "AND t.fecha_finalizacion < t.deadline", nativeQuery = true)
    Long countCompletedTareasBeforeDeadlineByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
            @Param("sprintId") Long sprintId);

    // Assigned tasks for a user in a sprint
    @Query(value = "SELECT COUNT(*) " +
            "FROM ADMIN.TAREAS t " +
            "WHERE t.usuario_id = :usuarioId " +
            "AND t.sprint_id = :sprintId", nativeQuery = true)
    Long countAsignedTareasByUsuarioAndSprint(@Param("usuarioId") Long usuarioId,
            @Param("sprintId") Long sprintId);

    /* ---------- SIMPLE FINDERS ---------- */

    List<Tarea> findByUsuarioId(Long usuarioId);

    // Total actual hours by team, sprint, and user
    @Query(value = "SELECT COALESCE(SUM(t.horas_reales), 0) " +
            "FROM ADMIN.TAREAS t " +
            "WHERE t.estado = 'completado' " +
            "AND t.equipo_id = :equipoId " +
            "AND t.sprint_id = :sprintId " +
            "AND t.usuario_id = :usuarioId", nativeQuery = true)
    BigDecimal sumHorasRealesByEquipoAndSprintAndUsuario(@Param("equipoId") Long equipoId,
            @Param("sprintId") Long sprintId,
            @Param("usuarioId") Long usuarioId);
}
