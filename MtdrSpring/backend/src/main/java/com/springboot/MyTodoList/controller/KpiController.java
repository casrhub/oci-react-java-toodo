package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Kpi;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/kpis")
public class KpiController {

  private static final Map<Long, Kpi> kpiStore = new ConcurrentHashMap<>();
  private static final AtomicLong idGenerator = new AtomicLong(1);

  @PostMapping("/crear")
  public ResponseEntity<Kpi> createKpi(@RequestBody Kpi kpi) {
    long id = idGenerator.getAndIncrement();
    kpi.setKpiId(id);
    kpiStore.put(id, kpi);
    return new ResponseEntity<>(kpi, HttpStatus.CREATED);
  }

  @GetMapping("/{kpiId}")
  public ResponseEntity<Kpi> getKpiById(@PathVariable("kpiId") Long kpiId) {
    Kpi kpi = kpiStore.get(kpiId);
    if (kpi != null) {
      return ResponseEntity.ok(kpi);
    }
    return ResponseEntity.notFound().build();
  }

  @PutMapping("/{kpiId}")
  public ResponseEntity<Kpi> updateKpi(
      @PathVariable("kpiId") Long kpiId, @RequestBody Kpi updatedKpi) {
    Kpi existing = kpiStore.get(kpiId);
    if (existing != null) {
      updatedKpi.setKpiId(kpiId);
      kpiStore.put(kpiId, updatedKpi);
      return ResponseEntity.ok(updatedKpi);
    }
    return ResponseEntity.notFound().build();
  }

  @GetMapping
  public ResponseEntity<List<Kpi>> getAllKpis(
      @RequestParam(value = "usuarioId", required = false) Long usuarioId) {
    List<Kpi> kpis = new ArrayList<>(kpiStore.values());
    if (usuarioId != null) {
      kpis =
          kpis.stream()
              .filter(kpi -> kpi.getUsuarioId() != null && kpi.getUsuarioId().equals(usuarioId))
              .collect(Collectors.toList());
    }
    return ResponseEntity.ok(kpis);
  }

  public Map<Long, Kpi> getKpiStore() {
    return kpiStore;
  }

  public void resetIdGenerator() {
    idGenerator.set(1);
  }
}
