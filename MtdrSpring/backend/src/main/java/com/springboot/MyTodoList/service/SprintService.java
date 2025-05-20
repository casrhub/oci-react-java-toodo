package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.repository.SprintRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Service class for managing Sprint entities.
 * Provides CRUD operations and business logic for Sprint management.
 */
@Service
public class SprintService {

  private final SprintRepository sprintRepository;

  /**
   * Constructor for SprintService.
   * @param sprintRepository Repository for Sprint entities
   */
  public SprintService(SprintRepository sprintRepository) {
    this.sprintRepository = sprintRepository;
  }

  /**
   * Retrieves all sprints.
   * @return List of all sprints
   */
  public List<Sprint> findAllSprints() {
    return sprintRepository.findAll();
  }

  /**
   * Retrieves a sprint by its ID.
   * @param id The ID of the sprint to find
   * @return Optional containing the sprint if found
   */
  public Optional<Sprint> findSprintById(Long id) {
    Assert.notNull(id, "Sprint ID must not be null");
    return sprintRepository.findById(id);
  }

  /**
   * Creates a new sprint.
   * @param sprint The sprint to create
   * @return The created sprint
   */
  public Sprint createSprint(Sprint sprint) {
    Assert.notNull(sprint, "Sprint must not be null");
    return sprintRepository.save(sprint);
  }

  public Sprint updateSprint(Long id, Sprint updatedSprint) {
    return sprintRepository
        .findById(id)
        .map(
            existing -> {
              existing.setNombre(updatedSprint.getNombre());
              existing.setProyectoId(updatedSprint.getProyectoId());
              existing.setFechaInicio(updatedSprint.getFechaInicio());
              existing.setFechaFin(updatedSprint.getFechaFin());
              existing.setEstado(updatedSprint.getEstado());
              return sprintRepository.save(existing);
            })
        .orElse(null);
  }

  /**
   * Deletes a sprint by its ID.
   * @param id The ID of the sprint to delete
   * @return true if the sprint was deleted, false if it didn't exist
   */
  public boolean deleteSprint(Long id) {
    Assert.notNull(id, "Sprint ID must not be null");
    if (sprintRepository.existsById(id)) {
      sprintRepository.deleteById(id);
      return true;
    }
    return false;
  }


}
