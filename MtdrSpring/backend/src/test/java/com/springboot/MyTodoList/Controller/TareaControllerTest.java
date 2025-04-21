package com.springboot.MyTodoList.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.MyTodoList.model.Tarea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Ejemplo de test para TareaController con @SpringBootTest
 * y @AutoConfigureMockMvc.
 * Las referencias a getId() se han sustituido por getTareaId(),
 * para coincidir con el método de tu clase Tarea.
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

        /**
         * Test para GET /tareas.
         * Verifica simplemente que se obtenga HTTP 200 (OK).
         */
        @Test
        void testGetAllTareas() throws Exception {
                mockMvc.perform(get("/tareas")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());
        }

        /**
         * Test para POST /tareas.
         * Crea una Tarea y verifica que contenga un tareaId.
         */
        @Test
        void testCreateTarea() throws Exception {
                String tareaJson = objectMapper.writeValueAsString(testTarea);

                MvcResult mvcResult = mockMvc.perform(post("/tareas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tareaJson)
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()) // O .isCreated(), depende de tu implementación
                                .andReturn();

                String responseBody = mvcResult.getResponse().getContentAsString();
                Tarea created = objectMapper.readValue(responseBody, Tarea.class);

                assertNotNull(created.getTareaId(), "La Tarea creada debe tener un tareaId autogenerado");
                assertEquals(testTarea.getTitulo(), created.getTitulo(), "El título debe coincidir con el enviado");
        }

        /**
         * Test para GET /tareas/{id} con un ID no existente.
         * Espera un HTTP 404 (Not Found).
         */
        @Test
        void testGetTareaById_NotFound() throws Exception {
                mockMvc.perform(get("/tareas/9999")
                                .accept(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        /**
         * Test para PUT /tareas/{id}.
         * Crea primero la Tarea, luego la modifica y confirma la actualización.
         */
        @Test
        void testUpdateTarea() throws Exception {
                // 1) Crear la Tarea
                String tareaJson = objectMapper.writeValueAsString(testTarea);
                MvcResult createResult = mockMvc.perform(post("/tareas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tareaJson))
                                .andExpect(status().isOk())
                                .andReturn();

                Tarea created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Tarea.class);

                // 2) Actualizar campo
                created.setTitulo("Titulo actualizado");
                String updatedJson = objectMapper.writeValueAsString(created);

                // 3) PUT /tareas/{id} usando getTareaId()
                MvcResult updateResult = mockMvc.perform(put("/tareas/" + created.getTareaId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(updatedJson))
                                .andExpect(status().isOk())
                                .andReturn();

                // 4) Comprobar la respuesta
                Tarea updated = objectMapper.readValue(updateResult.getResponse().getContentAsString(), Tarea.class);
                assertEquals("Titulo actualizado", updated.getTitulo(), "El titulo debe haberse actualizado");
        }

        /**
         * Test para DELETE /tareas/{id}.
         * Crea la Tarea, la elimina, y verifica que GET posterior devuelva 404.
         */
        @Test
        void testDeleteTarea() throws Exception {
                // 1) Crear la Tarea
                String tareaJson = objectMapper.writeValueAsString(testTarea);
                MvcResult createResult = mockMvc.perform(post("/tareas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tareaJson))
                                .andExpect(status().isOk())
                                .andReturn();

                Tarea created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Tarea.class);

                // 2) DELETE /tareas/{id}
                mockMvc.perform(delete("/tareas/" + created.getTareaId()))
                                .andExpect(status().isOk());

                // 3) Intentar recuperar la misma Tarea (404)
                mockMvc.perform(get("/tareas/" + created.getTareaId()))
                                .andExpect(status().isNotFound());
        }

        /**
         * Test para PUT /tareas/{id}/complete.
         * Marca la tarea como COMPLETADO y asigna horasReales.
         */
        @Test
        void testMarkAsComplete() throws Exception {
                // 1) Crear la Tarea
                String tareaJson = objectMapper.writeValueAsString(testTarea);
                MvcResult createResult = mockMvc.perform(post("/tareas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tareaJson))
                                .andExpect(status().isOk())
                                .andReturn();

                Tarea created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Tarea.class);

                // 2) Armar el payload
                Map<String, Object> payload = new HashMap<>();
                payload.put("estado", "COMPLETADO");
                payload.put("horasReales", new BigDecimal("3.5"));
                String payloadJson = objectMapper.writeValueAsString(payload);

                // 3) PUT /tareas/{id}/complete
                MvcResult completeResult = mockMvc.perform(put("/tareas/" + created.getTareaId() + "/complete")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadJson))
                                .andExpect(status().isOk())
                                .andReturn();

                // 4) Validar la respuesta
                Tarea updated = objectMapper.readValue(completeResult.getResponse().getContentAsString(), Tarea.class);
                assertEquals("COMPLETADO", updated.getEstado(), "La tarea debe haberse marcado como COMPLETADO");
                assertEquals(new BigDecimal("3.5"), updated.getHorasReales(),
                                "Debe reflejar las horas reales enviadas");
        }

        /**
         * Test para PUT /tareas/{id}/deadline.
         * Cambia la fecha límite (deadline).
         */
        @Test
        void testUpdateDeadline() throws Exception {
                // 1) Crear la Tarea
                String tareaJson = objectMapper.writeValueAsString(testTarea);
                MvcResult createResult = mockMvc.perform(post("/tareas")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(tareaJson))
                                .andExpect(status().isOk())
                                .andReturn();

                Tarea created = objectMapper.readValue(createResult.getResponse().getContentAsString(), Tarea.class);

                // 2) Payload con nueva fecha
                OffsetDateTime newDeadline = OffsetDateTime.now().plusDays(3);
                Map<String, String> payload = new HashMap<>();
                payload.put("deadline", newDeadline.toString());
                String payloadJson = objectMapper.writeValueAsString(payload);

                // 3) PUT /tareas/{id}/deadline
                MvcResult deadlineResult = mockMvc.perform(put("/tareas/" + created.getTareaId() + "/deadline")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadJson))
                                .andExpect(status().isOk())
                                .andReturn();

                // 4) Validar la respuesta
                Tarea updated = objectMapper.readValue(deadlineResult.getResponse().getContentAsString(), Tarea.class);
                assertEquals(newDeadline.toInstant(), updated.getDeadline().toInstant(),
                                "La fecha límite debe haberse actualizado correctamente");
        }

        /**
         * Test para PUT /tareas/{id}/deadline con fecha en formato inválido...
         * Esperamos HTTP 400 (Bad Request).
         */
        @Test
        void testUpdateDeadline_BadRequest() throws Exception {
                Map<String, String> payload = new HashMap<>();
                payload.put("deadline", "fecha-invalida");
                String payloadJson = objectMapper.writeValueAsString(payload);

                mockMvc.perform(put("/tareas/9999/deadline")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(payloadJson))
                                .andExpect(status().isBadRequest());
        }
}
