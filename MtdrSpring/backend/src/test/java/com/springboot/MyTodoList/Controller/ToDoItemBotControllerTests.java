package com.springboot.MyTodoList.Controller;

import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.UsuarioService;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ToDoItemBotControllerTests.class)
public class ToDoItemBotControllerTests {

    @MockBean
    private UsuarioService usuarioService;


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoItemService toDoItemService;

    @Test
    public void testGetAllTareas() throws Exception {
        OffsetDateTime now = OffsetDateTime.now();

        List<ToDoItem> mockList = List.of(
            new ToDoItem(1, "Write tests", now.minusDays(1), false, now.plusDays(3)),
            new ToDoItem(2, "Review PR", now, true, now.plusDays(1))
        );

        Mockito.when(toDoItemService.findAll()).thenReturn(mockList);

        mockMvc.perform(get("/todolist").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].description").value("Write tests"))
            .andExpect(jsonPath("$[0].done").value(false))
            .andExpect(jsonPath("$[1].description").value("Review PR"))
            .andExpect(jsonPath("$[1].done").value(true));
    }
}
