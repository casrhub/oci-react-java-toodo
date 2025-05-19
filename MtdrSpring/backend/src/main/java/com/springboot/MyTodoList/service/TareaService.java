// src/main/java/com/springboot/MyTodoList/service/TareaService.java
package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.SubTarea;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.SubTareaRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Service class for managing Tarea (Task) entities.
 * Provides CRUD operations, task management, and KPI calculations.
 */
@Service
public class TareaService {

  private static final BigDecimal MAX_ESTIMATED_HOURS = new BigDecimal("4");
  private static final String STATUS_COMPLETED = "completado";
  private static final String STATUS_IN_PROGRESS = "en progreso";
  private static final String STATUS_PENDING = "pendiente";

  private final TareaRepository tareaRepository;
  private final SubTareaRepository subTareaRepository;

  /**
   * Constructor for TareaService.
   * @param tareaRepository Repository for Tarea entities
   * @param subTareaRepository Repository for SubTarea entities
   */
  public TareaService(TareaRepository tareaRepository, SubTareaRepository subTareaRepository) {
    this.tareaRepository = tareaRepository;
    this.subTareaRepository = subTareaRepository;
  }

  /**
   * Retrieves all tasks.
   * @return List of all tasks
   */
  public List<Tarea> findAllTasks() {
    return tareaRepository.findAll();
  }

  /**
   * Retrieves all tasks for a specific user.
   * @param usuarioId The ID of the user
   * @return List of tasks assigned to the user
   */
  public List<Tarea> findTasksByUserId(Long usuarioId) {
    Assert.notNull(usuarioId, "User ID must not be null");
    return tareaRepository.findByUsuarioId(usuarioId);
  }

  /**
   * Retrieves a task by its ID.
   * @param id The ID of the task to find
   * @return Optional containing the task if found
   */
  public Optional<Tarea> findTaskById(Long id) {
    Assert.notNull(id, "Task ID must not be null");
    return tareaRepository.findById(id);
  }

  /**
   * Creates a new task.
   * @param task The task to create
   * @return The created task
   */
  public Tarea createTask(Tarea task) {
    Assert.notNull(task, "Task must not be null");
    validateTask(task);
    
    BigDecimal estimated = task.getHorasEstimadas();
    if (estimated != null && estimated.compareTo(MAX_ESTIMATED_HOURS) > 0) {
      task.setHorasEstimadas(MAX_ESTIMATED_HOURS);
    }

    Tarea saved = tareaRepository.save(task);
    createDefaultSubtasks(saved);
    return saved;
  }

  /**
   * Updates an existing task.
   * @param id The ID of the task to update
   * @param updatedTask The updated task data
   * @return Optional containing the updated task if found
   */
  public Optional<Tarea> updateTask(Long id, Tarea updatedTask) {
    Assert.notNull(id, "Task ID must not be null");
    Assert.notNull(updatedTask, "Updated task must not be null");
    validateTask(updatedTask);

    return tareaRepository.findById(id)
        .map(task -> {
          task.setTitulo(updatedTask.getTitulo());
          task.setDescripcion(updatedTask.getDescripcion());
          task.setEstado(updatedTask.getEstado());
          task.setUsuarioId(updatedTask.getUsuarioId());
          task.setHorasEstimadas(updatedTask.getHorasEstimadas());
          task.setHorasReales(updatedTask.getHorasReales());
          task.setDeadline(updatedTask.getDeadline());
          task.setEquipoId(updatedTask.getEquipoId());
          task.setProyectoId(updatedTask.getProyectoId());
          return tareaRepository.save(task);
        });
  }

  /**
   * Updates the assignee of a task.
   * @param id The ID of the task
   * @param usuarioId The ID of the new assignee
   * @return Optional containing the updated task if found
   */
  public Optional<Tarea> updateTaskAssignee(Long id, Long usuarioId) {
    Assert.notNull(id, "Task ID must not be null");
    Assert.notNull(usuarioId, "User ID must not be null");

    return tareaRepository.findById(id)
        .map(task -> {
          task.setUsuarioId(usuarioId);
          return tareaRepository.save(task);
        });
  }

  /**
   * Marks a task as complete.
   * @param id The ID of the task
   * @param estado The new status
   * @param horasReales The actual hours spent
   * @return Optional containing the updated task if found
   */
  public Optional<Tarea> markTaskAsComplete(Long id, String estado, BigDecimal horasReales) {
    Assert.notNull(id, "Task ID must not be null");
    Assert.notNull(estado, "Status must not be null");
    Assert.notNull(horasReales, "Actual hours must not be null");

    return tareaRepository.findById(id)
        .map(task -> {
          task.setEstado(estado);
          task.setHorasReales(horasReales);
          return tareaRepository.save(task);
        });
  }

  /**
   * Updates the deadline of a task.
   * @param id The ID of the task
   * @param deadline The new deadline
   * @return Optional containing the updated task if found
   */
  public Optional<Tarea> updateTaskDeadline(Long id, OffsetDateTime deadline) {
    Assert.notNull(id, "Task ID must not be null");
    Assert.notNull(deadline, "Deadline must not be null");

    return tareaRepository.findById(id)
        .map(task -> {
          task.setDeadline(deadline);
          return tareaRepository.save(task);
        });
  }

  /**
   * Deletes a task by its ID.
   * @param id The ID of the task to delete
   * @return true if the task was deleted, false if it didn't exist
   */
  public boolean deleteTask(Long id) {
    Assert.notNull(id, "Task ID must not be null");
    if (tareaRepository.existsById(id)) {
      tareaRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /**
   * Creates default subtasks for a task with more than 4 estimated hours.
   * @param task The parent task
   */
  private void createDefaultSubtasks(Tarea task) {
    BigDecimal estimated = task.getHorasEstimadas();
    if (estimated == null || estimated.compareTo(MAX_ESTIMATED_HOURS) <= 0) {
      return;
    }

    BigDecimal remaining = estimated.subtract(MAX_ESTIMATED_HOURS);
    int numberOfSubtasks = remaining.divide(MAX_ESTIMATED_HOURS, 0, BigDecimal.ROUND_UP).intValue();

    for (int i = 0; i < numberOfSubtasks; i++) {
      BigDecimal subHours = remaining.subtract(new BigDecimal(i * 4));
      if (subHours.compareTo(MAX_ESTIMATED_HOURS) > 0) {
        subHours = MAX_ESTIMATED_HOURS;
      }

      SubTarea subtask = new SubTarea();
      subtask.setTarea(task);
      subtask.setTitulo("Subtarea " + (i + 1));
      subtask.setDescripcion("Descripción pendiente");
      subtask.setEstado(STATUS_PENDING);
      subtask.setHorasEstimadas(subHours);
      subtask.setHorasReales(BigDecimal.ZERO);
      subtask.setFechaCreacion(OffsetDateTime.now());

      subTareaRepository.save(subtask);
    }
  }

  /**
   * Validates a task entity.
   * @param task The task to validate
   * @throws IllegalArgumentException if the task is invalid
   */
  private void validateTask(Tarea task) {
    Assert.hasText(task.getTitulo(), "Task title must not be empty");
    Assert.notNull(task.getEstado(), "Task status must not be null");
    Assert.notNull(task.getUsuarioId(), "User ID must not be null");
  }

  // KPI Methods
  /**
   * Calculates KPIs for a specific user.
   * @param usuarioId The ID of the user
   * @return Map containing various KPI metrics
   */
  public Map<String, Object> calculateUserKPIs(Long usuarioId) {
    Assert.notNull(usuarioId, "User ID must not be null");
    List<Tarea> userTasks = findTasksByUserId(usuarioId);
    Map<String, Object> kpis = new HashMap<>();

    long totalTasks = userTasks.size();
    long completedTasks = countTasksByStatus(userTasks, STATUS_COMPLETED);
    long inProgressTasks = countTasksByStatus(userTasks, STATUS_IN_PROGRESS);
    long pendingTasks = countTasksByStatus(userTasks, STATUS_PENDING);

    BigDecimal totalEstimatedHours = calculateTotalHours(userTasks, Tarea::getHorasEstimadas);
    BigDecimal totalRealHours = calculateTotalHours(userTasks, Tarea::getHorasReales);
    BigDecimal completedEstimatedHours = calculateCompletedHours(userTasks, Tarea::getHorasEstimadas);
    BigDecimal completedRealHours = calculateCompletedHours(userTasks, Tarea::getHorasReales);

    double completionRate = totalTasks > 0 ? (completedTasks * 100.0) / totalTasks : 0.0;

    kpis.put("totalTasks", totalTasks);
    kpis.put("completedTasks", completedTasks);
    kpis.put("inProgressTasks", inProgressTasks);
    kpis.put("pendingTasks", pendingTasks);
    kpis.put("completionRate", Math.round(completionRate * 100.0) / 100.0);
    kpis.put("totalEstimatedHours", totalEstimatedHours);
    kpis.put("totalRealHours", totalRealHours);
    kpis.put("completedEstimatedHours", completedEstimatedHours);
    kpis.put("completedRealHours", completedRealHours);

    return kpis;
  }

  private long countTasksByStatus(List<Tarea> tasks, String status) {
    return tasks.stream()
        .filter(t -> status.equalsIgnoreCase(t.getEstado()))
        .count();
  }

  private BigDecimal calculateTotalHours(List<Tarea> tasks, java.util.function.Function<Tarea, BigDecimal> hoursExtractor) {
    return tasks.stream()
        .map(t -> hoursExtractor.apply(t) != null ? hoursExtractor.apply(t) : BigDecimal.ZERO)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  private BigDecimal calculateCompletedHours(List<Tarea> tasks, java.util.function.Function<Tarea, BigDecimal> hoursExtractor) {
    return tasks.stream()
        .filter(t -> STATUS_COMPLETED.equalsIgnoreCase(t.getEstado()))
        .map(t -> hoursExtractor.apply(t) != null ? hoursExtractor.apply(t) : BigDecimal.ZERO)
        .reduce(BigDecimal.ZERO, BigDecimal::add);
  }

  // Team and Sprint KPI Methods
  public BigDecimal getTeamSprintRealHours(Long equipoId, Long sprintId) {
    Assert.notNull(equipoId, "Team ID must not be null");
    Assert.notNull(sprintId, "Sprint ID must not be null");
    return tareaRepository.sumHorasRealesByEquipoAndSprint(equipoId, sprintId);
  }

  public Long countTeamSprintCompletedTasks(Long equipoId, Long sprintId) {
    Assert.notNull(equipoId, "Team ID must not be null");
    Assert.notNull(sprintId, "Sprint ID must not be null");
    return tareaRepository.countCompletedTareasByEquipoAndSprint(equipoId, sprintId);
  }

  public BigDecimal getUserSprintRealHours(Long usuarioId, Long sprintId) {
    Assert.notNull(usuarioId, "User ID must not be null");
    Assert.notNull(sprintId, "Sprint ID must not be null");
    return tareaRepository.sumHorasRealesByUsuarioAndSprint(usuarioId, sprintId);
  }

  public Long countUserSprintCompletedTasks(Long usuarioId, Long sprintId) {
    Assert.notNull(usuarioId, "User ID must not be null");
    Assert.notNull(sprintId, "Sprint ID must not be null");
    return tareaRepository.countCompletedTareasByUsuarioAndSprint(usuarioId, sprintId);
  }

  public Map<String, Long> getTeamSummary(Long equipoId) {
    Assert.notNull(equipoId, "Team ID must not be null");
    long assigned = tareaRepository.countByEquipo(equipoId);
    long completedBefore = tareaRepository.countCompletedBeforeDeadlineTeam(equipoId);
    long completedAfter = tareaRepository.countCompletedAfterDeadlineTeam(equipoId);

    return Map.of(
        "assigned", assigned,
        "completedBefore", completedBefore,
        "completedAfter", completedAfter);
  }

  public Map<String, Long> getUserSummary(Long usuarioId) {
    Assert.notNull(usuarioId, "User ID must not be null");
    long assigned = tareaRepository.countByUsuario(usuarioId);
    long completedBefore = tareaRepository.countCompletedBeforeDeadline(usuarioId);
    long completedAfter = tareaRepository.countCompletedAfterDeadline(usuarioId);

    return Map.of(
        "assigned", assigned,
        "completedBefore", completedBefore,
        "completedAfter", completedAfter);
  }

  public BigDecimal getUserSprintEstimatedHours(Long usuarioId, Long sprintId) {
    Assert.notNull(usuarioId, "User ID must not be null");
    Assert.notNull(sprintId, "Sprint ID must not be null");
    return tareaRepository.sumHorasEstimadasByUsuarioAndSprint(usuarioId, sprintId);
  }

  public Long countUserSprintTasksBeforeDeadline(Long usuarioId, Long sprintId) {
    Assert.notNull(usuarioId, "User ID must not be null");
    Assert.notNull(sprintId, "Sprint ID must not be null");
    return tareaRepository.countCompletedTareasBeforeDeadlineByUsuarioAndSprint(usuarioId, sprintId);
  }

  public Long countUserSprintTasksAfterDeadline(Long usuarioId, Long sprintId) {
    Assert.notNull(usuarioId, "User ID must not be null");
    Assert.notNull(sprintId, "Sprint ID must not be null");
    return tareaRepository.countCompletedTareasAfterDeadlineByUsuarioAndSprint(usuarioId, sprintId);
  }

  public Long countUserSprintAssignedTasks(Long usuarioId, Long sprintId) {
    Assert.notNull(usuarioId, "User ID must not be null");
    Assert.notNull(sprintId, "Sprint ID must not be null");
    return tareaRepository.countAsignedTareasByUsuarioAndSprint(usuarioId, sprintId);
  }

  public BigDecimal getTeamSprintUserRealHours(Long equipoId, Long sprintId, Long usuarioId) {
    Assert.notNull(equipoId, "Team ID must not be null");
    Assert.notNull(sprintId, "Sprint ID must not be null");
    Assert.notNull(usuarioId, "User ID must not be null");
    return tareaRepository.sumHorasRealesByEquipoAndSprintAndUsuario(equipoId, sprintId, usuarioId);
  }
}
