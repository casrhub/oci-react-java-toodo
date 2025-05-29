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

  @Test
  void testCreateAndGetSubTarea() throws Exception {
    Tarea tarea = new Tarea();
    tarea.setTareaId(1L);

    SubTarea sub = new SubTarea();
    sub.setSubTareaId(1L);
    sub.setTarea(tarea);
    sub.setTitulo("Nueva SubTarea");
    sub.setDescripcion("Descripción");
    sub.setEstado("EN_PROCESO");
    sub.setHorasEstimadas(new BigDecimal("5"));
    sub.setHorasReales(new BigDecimal("2"));
    sub.setFechaCreacion(OffsetDateTime.parse("2024-04-03T14:25:00Z"));
    sub.setDeadline(OffsetDateTime.parse("2024-04-10T14:25:00Z"));

    Mockito.when(tareaRepository.findById(1L)).thenReturn(Optional.of(tarea));
    Mockito.when(subTareaService.save(Mockito.any(SubTarea.class))).thenReturn(sub);
    Mockito.when(subTareaService.findById(1L)).thenReturn(Optional.of(sub));

    Map<String, Object> payload = new LinkedHashMap<>();
    payload.put("tareaId", 1);
    payload.put("titulo", sub.getTitulo());
    payload.put("descripcion", sub.getDescripcion());
    payload.put("estado", sub.getEstado());
    payload.put("horasEstimadas", sub.getHorasEstimadas());
    payload.put("horasReales", sub.getHorasReales());
    payload.put("fechaCreacion", sub.getFechaCreacion().toString());
    payload.put("deadline", sub.getDeadline().toString());

    String jsonPayload = objectMapper.writeValueAsString(payload);

    mockMvc
        .perform(post("/subtareas").contentType(MediaType.APPLICATION_JSON).content(jsonPayload))
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
    Tarea tarea1 = new Tarea();
    tarea1.setTareaId(1L);
    Tarea tarea2 = new Tarea();
    tarea2.setTareaId(2L);

    SubTarea s1 = new SubTarea();
    s1.setSubTareaId(1L);
    s1.setTarea(tarea1);
    s1.setTitulo("S1");
    SubTarea s2 = new SubTarea();
    s2.setSubTareaId(2L);
    s2.setTarea(tarea2);
    s2.setTitulo("S2");

    Mockito.when(subTareaService.findAll()).thenReturn(Arrays.asList(s1, s2));
    Mockito.when(subTareaService.findByTareaId(1L)).thenReturn(Collections.singletonList(s1));

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
