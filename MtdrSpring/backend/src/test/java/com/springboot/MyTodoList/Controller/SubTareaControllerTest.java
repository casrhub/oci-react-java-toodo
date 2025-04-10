package com.springboot.MyTodoList.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.springboot.MyTodoList.controller.SubTareaController;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.SubTareaService;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.UsuarioService;
import com.springboot.MyTodoList.repository.*;
import com.springboot.MyTodoList.model.SubTarea;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;

@AutoConfigureMockMvc
@WebMvcTest(SubTareaController.class)
public class SubTareaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TareaService tareaService;

    @MockBean
    private SubTareaService subTareaService;

    @MockBean
    private SprintService sprintService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private TareaRepository tareaRepository;

    @MockBean
    private SubTareaRepository subTareaRepository;

    @MockBean
    private SprintRepository sprintRepository;

    @MockBean
    private UsuariosRepository usuariosRepository;

    @Test
    public void testTSubTareas() throws Exception {
        // Arrange: create a fake SubTarea
        SubTarea mockSubTarea = new SubTarea();
        mockSubTarea.setSubTareaId(1L);
        mockSubTarea.setTitulo("Mock Tarea");
        mockSubTarea.setDescripcion("Test Description");
        mockSubTarea.setEstado("EN_PROCESO");
        mockSubTarea.setHorasEstimadas(new BigDecimal("3.5"));
        mockSubTarea.setHorasReales(new BigDecimal("2.0"));
        mockSubTarea.setFechaCreacion(OffsetDateTime.now());
        mockSubTarea.setDeadline(OffsetDateTime.now().plusDays(7));

        List<SubTarea> mockList = Collections.singletonList(mockSubTarea);
        when(subTareaService.findAll()).thenReturn(mockList);

        // Act
        MvcResult mvcResult = mockMvc.perform(
                MockMvcRequestBuilders.get("/subtareas")
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        // Assert
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Should return 200 OK");

        String jsonResponse = mvcResult.getResponse().getContentAsString();
        List<SubTarea> responseList = objectMapper.readValue(jsonResponse, new TypeReference<List<SubTarea>>() {});

        assertEquals(1, responseList.size(), "Should return one SubTarea");

        SubTarea returned = responseList.get(0);
        assertEquals("Mock Tarea", returned.getTitulo());
        assertEquals("Test Description", returned.getDescripcion());
        assertEquals("EN_PROCESO", returned.getEstado());
        assertEquals(new BigDecimal("3.5"), returned.getHorasEstimadas());
    }
}