package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.service.SprintService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sprints")
public class SprintController {

  @Autowired private SprintService sprintService;

  @GetMapping
  public List<Sprint> getAllSprints() {
    return sprintService.findAllSprints();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Sprint> getSprintById(@PathVariable Long id) {
    return sprintService
        .findSprintById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public Sprint createSprint(@RequestBody Sprint sprint) {
    return sprintService.createSprint(sprint);
  }

  @PutMapping("/{id}")
  public ResponseEntity<Sprint> updateSprint(@PathVariable Long id, @RequestBody Sprint updatedSprint) {
    Sprint result = sprintService.updateSprint(id, updatedSprint);
    return result != null ? ResponseEntity.ok(result) : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteSprint(@PathVariable Long id) {
    boolean deleted = sprintService.deleteSprint(id);
    return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
  }
}
