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
import com.springboot.MyTodoList.repository.SprintRepository;
import com.springboot.MyTodoList.repository.SubTareaRepository;
import com.springboot.MyTodoList.repository.TareaRepository;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.SubTareaService;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.UsuarioService;
import com.springboot.MyTodoList.repository.*;;


//@SpringBootTest
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
    @MockBean
    private UsuarioService usuarioService; 

    @MockBean
    private TareaRepository tareaRepository;  // new
    @MockBean
    private SubTareaRepository subTareaRepository;  // new
    @MockBean
    private SprintRepository sprintRepository;  // new
    @MockBean
    private UsuariosRepository usuariosRepository;  // new

    @Test
    public void testTSubTareas() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/subtareas").accept(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(HttpStatus.OK.value(), mvcResult.getResponse().getStatus(), "Validate endpoint return 200 OK status");
        
    }

}