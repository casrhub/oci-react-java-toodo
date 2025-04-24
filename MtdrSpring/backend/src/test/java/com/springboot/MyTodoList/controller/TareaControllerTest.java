package com.springboot.MyTodoList.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.MyTodoList.model.Tarea;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/**
 * Tests for TareaController covering CRUD, assign, complete, deadline, KPIs,
 * summaries, and user-specific fetch.
 */
@SpringBootTest
@AutoConfigureMockMvc
class TareaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Tarea testTarea;

    @BeforeEach
    void setUp() {
        testTarea = new Tarea();
        testTarea.setTitulo("Tarea de prueba");
        testTarea.setDescripcion("Descripción inicial de prueba");
    }

    @Test
    void testGetAllTareas() throws Exception {
        mockMvc.perform(get("/tareas").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testCreateTarea() throws Exception {
        String tareaJson = objectMapper.writeValueAsString(testTarea);
        MvcResult mvcResult = mockMvc.perform(post("/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tareaJson)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        Tarea created = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), Tarea.class);
        assertNotNull(created.getTareaId());
        assertEquals(testTarea.getTitulo(), created.getTitulo());
    }

    @Test
    void testGetTareaById_NotFound() throws Exception {
        mockMvc.perform(get("/tareas/9999").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateTarea() throws Exception {
        // Create
        String tareaJson = objectMapper.writeValueAsString(testTarea);
        MvcResult createResult = mockMvc.perform(post("/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tareaJson))
                .andExpect(status().isOk())
                .andReturn();
        Tarea created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Tarea.class);

        // Update
        created.setTitulo("Titulo actualizado");
        String updatedJson = objectMapper.writeValueAsString(created);
        MvcResult updateResult = mockMvc.perform(put("/tareas/" + created.getTareaId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(updatedJson))
                .andExpect(status().isOk())
                .andReturn();

        Tarea updated = objectMapper.readValue(updateResult.getResponse().getContentAsString(), Tarea.class);
        assertEquals("Titulo actualizado", updated.getTitulo());
    }

    @Test
    void testDeleteTarea() throws Exception {
        // Create
        String tareaJson = objectMapper.writeValueAsString(testTarea);
        MvcResult createResult = mockMvc.perform(post("/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tareaJson))
                .andExpect(status().isOk())
                .andReturn();
        Tarea created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Tarea.class);

        // Delete
        mockMvc.perform(delete("/tareas/" + created.getTareaId()))
                .andExpect(status().isOk());

        // Verify gone
        mockMvc.perform(get("/tareas/" + created.getTareaId()))
                .andExpect(status().isNotFound());
    }

    @Test
    void testMarkAsComplete() throws Exception {
        // Create
        String tareaJson = objectMapper.writeValueAsString(testTarea);
        MvcResult createResult = mockMvc.perform(post("/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tareaJson))
                .andExpect(status().isOk())
                .andReturn();
        Tarea created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Tarea.class);

        // Complete payload
        Map<String, Object> payload = new HashMap<>();
        payload.put("estado", "COMPLETADO");
        payload.put("horasReales", new BigDecimal("3.5"));
        String payloadJson = objectMapper.writeValueAsString(payload);

        // Complete endpoint
        MvcResult completeResult = mockMvc.perform(put("/tareas/" + created.getTareaId() + "/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
                .andExpect(status().isOk())
                .andReturn();

        Tarea updated = objectMapper.readValue(completeResult.getResponse().getContentAsString(), Tarea.class);
        assertEquals("COMPLETADO", updated.getEstado());
        assertEquals(new BigDecimal("3.5"), updated.getHorasReales());
    }

    @Test
    void testUpdateDeadline() throws Exception {
        // Create
        String tareaJson = objectMapper.writeValueAsString(testTarea);
        MvcResult createResult = mockMvc.perform(post("/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tareaJson))
                .andExpect(status().isOk())
                .andReturn();
        Tarea created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Tarea.class);

        // Deadline payload
        OffsetDateTime newDeadline = OffsetDateTime.now().plusDays(3);
        Map<String, String> payload = Map.of("deadline", newDeadline.toString());
        String payloadJson = objectMapper.writeValueAsString(payload);

        // Update deadline
        MvcResult dlResult = mockMvc.perform(put("/tareas/" + created.getTareaId() + "/deadline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
                .andExpect(status().isOk())
                .andReturn();

        Tarea updated = objectMapper.readValue(dlResult.getResponse().getContentAsString(), Tarea.class);
        assertEquals(newDeadline.toInstant(), updated.getDeadline().toInstant());
    }

    @Test
    void testUpdateDeadline_BadRequest() throws Exception {
        String badJson = objectMapper.writeValueAsString(Map.of("deadline", "not-a-date"));
        mockMvc.perform(put("/tareas/1234/deadline")
                .contentType(MediaType.APPLICATION_JSON)
                .content(badJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateAssignee() throws Exception {
        // Create
        String tareaJson = objectMapper.writeValueAsString(testTarea);
        MvcResult createResult = mockMvc.perform(post("/tareas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(tareaJson))
                .andExpect(status().isOk())
                .andReturn();
        Tarea created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Tarea.class);

        // Assignee payload
        Map<String, Long> payload = Map.of("usuarioId", 42L);
        String payloadJson = objectMapper.writeValueAsString(payload);

        // PATCH assignee
        mockMvc.perform(patch("/tareas/" + created.getTareaId() + "/assignee")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payloadJson))
                .andExpect(status().isOk());

        // Verify change
        MvcResult getResult = mockMvc.perform(get("/tareas/" + created.getTareaId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Tarea updated = objectMapper.readValue(getResult.getResponse().getContentAsString(), Tarea.class);
        assertEquals(42L, updated.getUsuarioId());
    }

    @Test
    void testUpdateAssignee_BadRequest() throws Exception {
        mockMvc.perform(patch("/tareas/1/assignee")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetHorasByEquipoAndSprint_Empty() throws Exception {
        mockMvc.perform(get("/tareas/equipo/99/sprint/88/horas-trabajadas"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void testGetCompletedTareasByEquipoAndSprint_Empty() throws Exception {
        mockMvc.perform(get("/tareas/equipo/77/sprint/66/tareas-completadas"))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }

    @Test
    void testResumenPorUsuario_Empty() throws Exception {
        mockMvc.perform(get("/tareas/usuario/55/summary"))
                .andExpect(status().isOk())
                .andExpect(content().json("{}"));
    }

    @Test
    void testGetTareasByUsuario_Empty() throws Exception {
        mockMvc.perform(get("/tareas/user/123"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}
