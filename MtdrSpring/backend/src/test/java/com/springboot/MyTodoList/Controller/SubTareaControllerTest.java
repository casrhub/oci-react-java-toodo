package com.springboot.MyTodoList.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;


@SpringBootTest
@AutoConfigureMockMvc
@WebMvcTest(SubTareaController.class)
public class SubTareaControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TareaService tareaService;
    @MockBean
    private SubTareaService subTareaService;
    @MockBean
    private SprintService sprintService;

    @Test
    public void testGreetingOptionalQueryStringParam() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/subtareas").accept(MediaType.APPLICATION_JSON)).andReturn();
        mockMvc.perform(get("/subtareas")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].sub_tarea_id").exists())
            .andExpect(jsonPath("$[0].tarea_id").exists())
            .andExpect(jsonPath("$[0].titulo").isString())
            .andExpect(jsonPath("$[0].descripcion").isString())
            .andExpect(jsonPath("$[0].estado").isString())
            .andExpect(jsonPath("$[0].horas_estimadas").isNumber())
            .andExpect(jsonPath("$[0].horas_reales").isNumber())
            .andExpect(jsonPath("$[0].fecha_creacion").isString())
            .andExpect(jsonPath("$[0].deadline").isString());
 
        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status");
        
    }


}