package com.springboot.MyTodoList.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.service.TareaService;
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
    controllers = TareaController.class,
    properties = "spring.mvc.pathmatch.matching-strategy=ant_path_matcher")
@AutoConfigureMockMvc(addFilters = false)
public class TareaControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;

  @MockBean private TareaService tareaService;

  @Configuration
  static class TestConfig {
    static final TareaController controller = new TareaController();

    @Bean
    public TareaController tareaController() {
      return controller;
    }

    public static TareaController getController() {
      return controller;
    }
  }

  @BeforeEach
  void resetMocks() {
    Mockito.reset(tareaService);
  }

  private Tarea build(Long id, Long usuarioId, String titulo) {
    Tarea t = new Tarea();
    t.setTareaId(id);
    t.setUsuarioId(usuarioId);
    t.setTitulo(titulo);
    t.setDescripcion("Desc " + titulo);
    t.setEstado("EN_PROCESO");
    t.setHorasEstimadas(new BigDecimal("4.0"));
    t.setHorasReales(new BigDecimal("2.0"));
    t.setFechaCreacion(OffsetDateTime.parse("2024-04-03T14:25:00Z"));
    t.setDeadline(OffsetDateTime.parse("2024-04-10T14:25:00Z"));
    return t;
  }

  @Test
  void testCreateAndGetTarea() throws Exception {
    Tarea nueva = build(null, 1L, "Nueva Tarea");
    Tarea creada = build(1L, 1L, "Nueva Tarea");

    Mockito.when(tareaService.save(Mockito.any(Tarea.class))).thenReturn(creada);
    Mockito.when(tareaService.findById(1L)).thenReturn(Optional.of(creada));

    String jsonPayload = objectMapper.writeValueAsString(nueva);

    mockMvc
        .perform(post("/tareas").contentType(MediaType.APPLICATION_JSON).content(jsonPayload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tareaId").value(1))
        .andExpect(jsonPath("$.titulo").value("Nueva Tarea"));

    mockMvc
        .perform(get("/tareas/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.tareaId").value(1))
        .andExpect(jsonPath("$.titulo").value("Nueva Tarea"));
  }

  @Test
  void testGetAllTareasAndByUsuario() throws Exception {
    Tarea t1 = build(1L, 1L, "T1");
    Tarea t2 = build(2L, 2L, "T2");

    Mockito.when(tareaService.findAll()).thenReturn(Arrays.asList(t1, t2));
    Mockito.when(tareaService.findByUsuarioId(1L)).thenReturn(Collections.singletonList(t1));

    mockMvc
        .perform(get("/tareas"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(2));

    mockMvc
        .perform(get("/tareas/user/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.length()").value(1))
        .andExpect(jsonPath("$[0].tareaId").value(1));
  }

  @Test
  void testUpdateAssignee() throws Exception {
    Tarea existente = build(1L, 1L, "Titulo");
    Tarea actualizado = build(1L, 42L, "Titulo");

    Mockito.when(tareaService.updateAssignee(1L, 42L)).thenReturn(actualizado);

    String payload = objectMapper.writeValueAsString(Map.of("usuarioId", 42));

    mockMvc
        .perform(
            patch("/tareas/1/assignee").contentType(MediaType.APPLICATION_JSON).content(payload))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.usuarioId").value(42));
  }

  @Test
  void testDeleteTarea() throws Exception {
    Mockito.when(tareaService.deleteById(1L)).thenReturn(true);

    mockMvc.perform(delete("/tareas/1")).andExpect(status().isOk());

    Mockito.when(tareaService.findById(1L)).thenReturn(Optional.empty());
    mockMvc.perform(get("/tareas/1")).andExpect(status().isNotFound());
  }
}
