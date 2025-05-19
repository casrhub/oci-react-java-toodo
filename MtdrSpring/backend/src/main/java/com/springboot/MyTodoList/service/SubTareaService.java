package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.SubTarea;
import com.springboot.MyTodoList.repository.SubTareaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Service class for managing SubTarea (Subtask) entities.
 * Provides CRUD operations and business logic for subtask management.
 */
@Service
public class SubTareaService {

  private final SubTareaRepository repository;

  /**
   * Constructor for SubTareaService.
   * @param repository Repository for SubTarea entities
   */
  public SubTareaService(SubTareaRepository repository) {
    this.repository = repository;
  }

  /**
   * Retrieves all subtasks.
   * @return List of all subtasks
   */
  public List<SubTarea> findAllSubtasks() {
    return repository.findAll();
  }

  /**
   * Retrieves a subtask by its ID.
   * @param id The ID of the subtask to find
   * @return Optional containing the subtask if found
   */
  public Optional<SubTarea> findSubtaskById(Long id) {
    Assert.notNull(id, "Subtask ID must not be null");
    return repository.findById(id);
  }

  /**
   * Retrieves all subtasks for a specific task.
   * @param tareaId The ID of the parent task
   * @return List of subtasks for the specified task
   */
  public List<SubTarea> findSubtasksByTaskId(Long tareaId) {
    Assert.notNull(tareaId, "Task ID must not be null");
    return repository.findByTarea_TareaId(tareaId);
  }

  /**
   * Creates a new subtask.
   * @param subtask The subtask to create
   * @return The created subtask
   */
  public SubTarea createSubtask(SubTarea subtask) {
    Assert.notNull(subtask, "Subtask must not be null");
    validateSubtask(subtask);
    return repository.save(subtask);
  }

  /**
   * Updates an existing subtask.
   * @param id The ID of the subtask to update
   * @param updatedSubtask The updated subtask data
   * @return Optional containing the updated subtask if found
   */
  public Optional<SubTarea> updateSubtask(Long id, SubTarea updatedSubtask) {
    Assert.notNull(id, "Subtask ID must not be null");
    Assert.notNull(updatedSubtask, "Updated subtask must not be null");
    validateSubtask(updatedSubtask);

    return repository.findById(id)
        .map(existing -> {
          existing.setNombre(updatedSubtask.getNombre());
          existing.setDescripcion(updatedSubtask.getDescripcion());
          existing.setEstado(updatedSubtask.getEstado());
          existing.setTarea(updatedSubtask.getTarea());
          return repository.save(existing);
        });
  }

  /**
   * Deletes a subtask by its ID.
   * @param id The ID of the subtask to delete
   * @return true if the subtask was deleted, false if it didn't exist
   */
  public boolean deleteSubtask(Long id) {
    Assert.notNull(id, "Subtask ID must not be null");
    if (repository.existsById(id)) {
      repository.deleteById(id);
      return true;
    }
    return false;
  }

  /**
   * Validates a subtask entity.
   * @param subtask The subtask to validate
   * @throws IllegalArgumentException if the subtask is invalid
   */
  private void validateSubtask(SubTarea subtask) {
    Assert.hasText(subtask.getNombre(), "Subtask name must not be empty");
    Assert.notNull(subtask.getTarea(), "Parent task must not be null");
    Assert.notNull(subtask.getEstado(), "Status must not be null");
  }
}
