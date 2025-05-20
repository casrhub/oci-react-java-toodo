package com.springboot.MyTodoList.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.MyTodoList.model.SubTarea;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.TareaRepository;
import com.springboot.MyTodoList.service.SubTareaService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(
        controllers = SubTareaController.class,
        properties = "spring.mvc.pathmatch.matching-strategy=ant_path_matcher")
@AutoConfigureMockMvc(addFilters = false)
public class SubTareaControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private SubTareaService subTareaService;
  @MockBean private TareaRepository tareaRepository;

  @Configuration
  static class TestConfig {
    static final SubTareaController controller = new SubTareaController();

    @Bean
    public SubTareaController subTareaController() {
      return controller;
    }

    public static SubTareaController getController() {
      return controller;
    }
  }

  @BeforeEach
  void resetMocks() {
    Mockito.reset(subTareaService, tareaRepository);
  }

  private Tarea buildTarea(Long id) {
    Tarea tarea = new Tarea();
    tarea.setTareaId(id);
    return tarea;
  }

  private SubTarea buildSubTarea(
          Long subTareaId,
          Tarea tarea,
          String titulo,
          String descripcion,
          String estado,
          BigDecimal horasEstimadas,
          BigDecimal horasReales,
          OffsetDateTime fechaCreacion,
          OffsetDateTime deadline) {
    SubTarea sub = new SubTarea();
    sub.setSubTareaId(subTareaId);
    sub.setTarea(tarea);
    sub.setTitulo(titulo);
    sub.setDescripcion(descripcion);
    sub.setEstado(estado);
    sub.setHorasEstimadas(horasEstimadas);
    sub.setHorasReales(horasReales);
    sub.setFechaCreacion(fechaCreacion);
    sub.setFechaLimite(deadline);
    return sub;
  }

  private SubTarea buildSubTarea(Long subTareaId, Long tareaId, String titulo) {
    return buildSubTarea(
            subTareaId,
            buildTarea(tareaId),
            titulo,
            null,
            null,
            null,
            null,
            null,
            null);
  }

  private Map<String, Object> buildPayload(SubTarea sub) {
    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("tareaId", sub.getTarea().getTareaId());
    payload.put("titulo", sub.getTitulo());
    payload.put("descripcion", sub.getDescripcion());
    payload.put("estado", sub.getEstado());
    payload.put("horasEstimadas", sub.getHorasEstimadas());
    payload.put("horasReales", sub.getHorasReales());
    payload.put("fechaCreacion", sub.getFechaCreacion().toString());
    payload.put("deadline", sub.getFechaLimite().toString());
    return payload;
  }

  @Test
  void testCreateAndGetSubTarea() throws Exception {
    Tarea tarea = buildTarea(1L);
    SubTarea sub =
            buildSubTarea(
                    1L,
                    tarea,
                    "Nueva SubTarea",
                    "Descripción",
                    "EN_PROCESO",
                    new BigDecimal("5"),
                    new BigDecimal("2"),
                    OffsetDateTime.parse("2024-04-03T14:25:00Z"),
                    OffsetDateTime.parse("2024-04-10T14:25:00Z"));

    Mockito.when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
    Mockito.when(subTareaService.createSubtask(Mockito.any(SubTarea.class))).thenReturn(sub);
    Mockito.when(subTareaService.findSubtaskById(1L)).thenReturn(Optional.of(sub));

    String jsonPayload = objectMapper.writeValueAsString(buildPayload(sub));

    mockMvc
            .perform(post("/subtareas")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(jsonPayload))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.subTareaId").value(1))
            .andExpect(jsonPath("$.titulo").value("Nueva SubTarea"));

    mockMvc
            .perform(get("/subtareas/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.subTareaId").value(1))
            .andExpect(jsonPath("$.titulo").value("Nueva SubTarea"));
  }

  @Test
  void testGetAllAndFilterByTarea() throws Exception {
    SubTarea s1 = buildSubTarea(1L, 1L, "S1");
    SubTarea s2 = buildSubTarea(2L, 2L, "S2");

    Mockito.when(subTareaService.findAllSubtasks()).thenReturn(Arrays.asList(s1, s2));
    Mockito.when(subTareaService.findSubtasksByTaskId(1L)).thenReturn(Collections.singletonList(s1));

    mockMvc
            .perform(get("/subtareas"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));

    mockMvc
            .perform(get("/subtareas").param("tareaId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1))
            .andExpect(jsonPath("$[0].subTareaId").value(1));
  }
}
