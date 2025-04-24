package com.springboot.MyTodoList.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.MyTodoList.model.SubTarea;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.repository.*;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.SubTareaService;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.UsuarioService;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@AutoConfigureMockMvc
@WebMvcTest(SubTareaController.class)
public class SubTareaControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @MockBean private TareaService tareaService;

  @MockBean private SubTareaService subTareaService;

  @MockBean private SprintService sprintService;

  @MockBean private UsuarioService usuarioService;

  @MockBean private TareaRepository tareaRepository;

  @MockBean private SubTareaRepository subTareaRepository;

  @MockBean private SprintRepository sprintRepository;

  @MockBean private UsuariosRepository usuariosRepository;

  @Test
  public void testGetAllSubTareas() throws Exception {
    SubTarea mockSubTarea = new SubTarea();
    mockSubTarea.setSubTareaId(1L);
    mockSubTarea.setTitulo("Mock SubTarea");
    mockSubTarea.setDescripcion("Test Description");
    mockSubTarea.setEstado("EN_PROCESO");
    mockSubTarea.setHorasEstimadas(new BigDecimal("3.5"));
    mockSubTarea.setHorasReales(new BigDecimal("2.0"));
    mockSubTarea.setFechaCreacion(OffsetDateTime.now());
    mockSubTarea.setDeadline(OffsetDateTime.now().plusDays(7));

    List<SubTarea> mockList = Collections.singletonList(mockSubTarea);
    when(subTareaService.findAll()).thenReturn(mockList);

    MvcResult mvcResult =
        mockMvc
            .perform(MockMvcRequestBuilders.get("/subtareas").accept(MediaType.APPLICATION_JSON))
            .andReturn();

    assertEquals(
        HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Should return 200 OK");

    String jsonResponse = mvcResult.getResponse().getContentAsString();
    List<SubTarea> responseList =
        objectMapper.readValue(jsonResponse, new TypeReference<List<SubTarea>>() {});

    assertEquals(1, responseList.size(), "Should return SubTarea");

    SubTarea returned = responseList.get(0);
    assertEquals("Mock SubTarea", returned.getTitulo());
    assertEquals("Test Description", returned.getDescripcion());
    assertEquals("EN_PROCESO", returned.getEstado());
    assertEquals(new BigDecimal("3.5"), returned.getHorasEstimadas());
  }

  @Test
  public void testGetSubTareaByTareaID() throws Exception {
    SubTarea mockSubTarea = new SubTarea();
    mockSubTarea.setSubTareaId(1L);
    mockSubTarea.setTitulo("Mock SubTarea");
    mockSubTarea.setDescripcion("Test Description");
    mockSubTarea.setEstado("EN_PROCESO");
    mockSubTarea.setHorasEstimadas(new BigDecimal("3.5"));
    mockSubTarea.setHorasReales(new BigDecimal("2.0"));
    mockSubTarea.setFechaCreacion(OffsetDateTime.now());
    mockSubTarea.setDeadline(OffsetDateTime.now().plusDays(7));

    List<SubTarea> mockList = Collections.singletonList(mockSubTarea);

    when(subTareaService.findByTareaId(1L)).thenReturn(mockList);

    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get(
                        "/subtareas/byTarea/{tareaId}", 1L) // Providing a valid tareaId
                    .accept(MediaType.APPLICATION_JSON))
            .andReturn();

    assertEquals(
        HttpStatus.OK.value(),
        mvcResult.getResponse().getStatus(),
        "Validate endpoint returns 200 OK status");

    String jsonResponse = mvcResult.getResponse().getContentAsString();
    List<SubTarea> responseList =
        objectMapper.readValue(jsonResponse, new TypeReference<List<SubTarea>>() {});

    assertEquals(1, responseList.size(), "Should return one SubTarea");

    SubTarea returned = responseList.get(0);
    assertEquals("Mock SubTarea", returned.getTitulo());
    assertEquals("Test Description", returned.getDescripcion());
    assertEquals("EN_PROCESO", returned.getEstado());
    assertEquals(new BigDecimal("3.5"), returned.getHorasEstimadas());
  }

  @Test
  public void testGetSubTareaByID() throws Exception {
    SubTarea mockSubTarea = new SubTarea();
    mockSubTarea.setSubTareaId(1L);
    mockSubTarea.setTitulo("Mock SubTarea");
    mockSubTarea.setDescripcion("Single SubTarea Test");
    mockSubTarea.setEstado("EN_PROCESO");
    mockSubTarea.setHorasEstimadas(new BigDecimal("4.0"));
    mockSubTarea.setHorasReales(new BigDecimal("3.0"));
    mockSubTarea.setFechaCreacion(OffsetDateTime.now());
    mockSubTarea.setDeadline(OffsetDateTime.now().plusDays(7));

    when(subTareaService.findById(1L)).thenReturn(Optional.of(mockSubTarea));

    MvcResult mvcResult =
        mockMvc
            .perform(
                MockMvcRequestBuilders.get("/subtareas/{id}", 1L)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.subTareaId").value(1))
            .andExpect(jsonPath("$.titulo").value("Mock SubTarea"))
            .andExpect(jsonPath("$.descripcion").value("Single SubTarea Test"))
            .andExpect(jsonPath("$.estado").value("EN_PROCESO"))
            .andExpect(jsonPath("$.horasEstimadas").value(4.0))
            .andExpect(jsonPath("$.horasReales").value(3.0))
            .andReturn();

    String jsonResponse = mvcResult.getResponse().getContentAsString();
    SubTarea returned = objectMapper.readValue(jsonResponse, SubTarea.class);

    assertEquals("Mock SubTarea", returned.getTitulo());
    assertEquals("EN_PROCESO", returned.getEstado());
    assertEquals(new BigDecimal("4.0"), returned.getHorasEstimadas());
  }

  @Test
  public void testCreateSubTarea() throws Exception {
    // Mock Tarea (parent)
    Tarea mockTarea = new Tarea();
    mockTarea.setTareaId(1L);

    // Mock SubTarea
    SubTarea mockSubTarea = new SubTarea();
    mockSubTarea.setSubTareaId(10L);
    mockSubTarea.setTarea(mockTarea);
    mockSubTarea.setTitulo("Nueva SubTarea");
    mockSubTarea.setDescripcion("Descripcion de prueba");
    mockSubTarea.setEstado("EN_PROCESO");
    mockSubTarea.setHorasEstimadas(new BigDecimal("5.0"));
    mockSubTarea.setHorasReales(new BigDecimal("2.0"));
    mockSubTarea.setFechaCreacion(OffsetDateTime.now());
    mockSubTarea.setDeadline(OffsetDateTime.now().plusDays(7));

    when(tareaRepository.findById(1L)).thenReturn(Optional.of(mockTarea));
    when(subTareaService.save(any(SubTarea.class))).thenReturn(mockSubTarea);

    Map<String, Object> payload = new HashMap<>();
    payload.put("subTareaId", 1L);
    payload.put("tareaId", 1);
    payload.put("titulo", "Nueva SubTarea");
    payload.put("descripcion", "Descripcion de prueba");
    payload.put("estado", "EN_PROCESO");
    payload.put("horasEstimadas", 5.0);
    payload.put("horasReales", 2.0);
    payload.put("fechaCreacion", mockSubTarea.getFechaCreacion().toString());
    payload.put("deadline", mockSubTarea.getDeadline().toString());

    System.out.println("Payload before serialization: " + payload); // DEBUG

    String jsonPayload = objectMapper.writeValueAsString(payload);

    System.out.println("JSON Payload: " + jsonPayload); // DEBUG

    // Perform the POST request
    mockMvc
        .perform(post("/subtareas").contentType(MediaType.APPLICATION_JSON).content(jsonPayload))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.subTareaId").value(10))
        .andExpect(jsonPath("$.titulo").value("Nueva SubTarea"))
        .andExpect(jsonPath("$.descripcion").value("Descripcion de prueba"))
        .andExpect(jsonPath("$.estado").value("EN_PROCESO"))
        .andExpect(jsonPath("$.horasEstimadas").value(5.0))
        .andExpect(jsonPath("$.horasReales").value(2.0));
  }

  @Test
  public void testDeleteSubTarea_Success() throws Exception {
    Long subTareaId = 1L;

    // Mocking the service to return true (successful)
    when(subTareaService.delete(subTareaId)).thenReturn(true);

    mockMvc
        .perform(MockMvcRequestBuilders.delete("/subtareas/{id}", subTareaId))
        .andExpect(status().isOk());

    verify(subTareaService, times(1)).delete(subTareaId);
  }

  @Test
  public void testDeleteSubTarea_NotFound() throws Exception {
    Long subTareaId = 99L;

    // Mocking the service to return false (not found)
    when(subTareaService.delete(subTareaId)).thenReturn(false);

    mockMvc
        .perform(MockMvcRequestBuilders.delete("/subtareas/{id}", subTareaId))
        .andExpect(status().isNotFound());

    verify(subTareaService, times(1)).delete(subTareaId);
  }
}
