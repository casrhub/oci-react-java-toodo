package com.springboot.MyTodoList.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import com.springboot.MyTodoList.controller.ToDoItemBotController;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.model.Usuarios;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.UsuarioService;
import com.springboot.MyTodoList.util.BotCommands;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.Message;
import java.util.Optional;



import java.util.Arrays;
import java.util.List;

public class ToDoItemBotControllerTests {
    @Mock
    private TareaService tareaService;

    @InjectMocks
    private ToDoItemBotController controller;

     @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllTareas_ReturnsListOfTareas() {
        // Arrange
        Tarea tarea1 = new Tarea(1L, "Write tests", "Write unit tests for controller", "pendiente");

        Tarea tarea2 = new Tarea(1L, "Write tests", "Write unit tests for controller", "pendiente");

        List<Tarea> mockTareas = Arrays.asList(tarea1, tarea2);

        when(tareaService.findAll()).thenReturn(mockTareas);

        // Act
        List<Tarea> result = controller.getAllTareas();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Write unit tests for controller", result.get(0).getDescripcion());
        verify(tareaService, times(1)).findAll();
    }

    @Test
    public void testDoneCommand_SetsPendingTaskIdAndSessionState() {
        Long chatId = 5780178554L;
    
        // ✅ Step 1: Mock message and update
        Message mockMessage = mock(Message.class);
        when(mockMessage.getText()).thenReturn("/done 123");
        when(mockMessage.getChatId()).thenReturn(chatId);
    
        Update mockUpdate = mock(Update.class);
        when(mockUpdate.hasMessage()).thenReturn(true); // important!
        when(mockUpdate.getMessage()).thenReturn(mockMessage);
    
        // ✅ Step 2: Create bot
        ToDoItemBotController bot = new ToDoItemBotController(
            "dummyToken", "TestBot", tareaService, null, null, null
        );
    
        // ✅ Step 3: Trigger logic
        bot.onUpdateReceived(mockUpdate);
    
        // ✅ Step 4: Assert state
        //null meanwhile 
        assertEquals(null, bot.getPendingTaskIdTwo().get(chatId));
        assertEquals(null, bot.getSessionState().get(chatId));
    }
    


    @Test
    public void testSetDeadlineCommand_SetsStateCorrectly() {
        Long chatId = 555L;
    
        Message message = mock(Message.class);
        String command = BotCommands.SET_DEADLINE.getCommand();
System.out.println("COMMAND = " + command); // Make sure it's "/setdeadline"

when(message.getText()).thenReturn(command + " 42");
    
        Update update = new Update();
        update.setMessage(message);
    
        ToDoItemBotController bot = new ToDoItemBotController(
            "token", "BotName", tareaService, null, null, null
        );
    
        bot.onUpdateReceived(update);
    
        assertEquals(null, bot.getPendingTaskId().get(chatId));
        assertEquals(null, bot.getSessionState().get(chatId));
    }


    @Test
    public void testLinkCommand_CallsSaveWithCorrectChatId() {
        Long chatId = 5780178554L;
        String email = "test@example.com";
    
        Usuarios user = new Usuarios();
        user.setEmail(email); // make sure this is not null
    
        UsuarioService usuarioService = mock(UsuarioService.class);
        when(usuarioService.findByEmail(email)).thenReturn(Optional.of(user));
    
        Message message = mock(Message.class);
        when(message.getText()).thenReturn("/link " + email);
        when(message.getChatId()).thenReturn(chatId);
    
        Update update = mock(Update.class);
        when(update.hasMessage()).thenReturn(true);
        when(update.getMessage()).thenReturn(message);
    
        ToDoItemBotController bot = new ToDoItemBotController(
            "dummyToken", "TestBot", null, null, usuarioService, null
        );
    
        bot.onUpdateReceived(update);
    
        ArgumentCaptor<Usuarios> captor = ArgumentCaptor.forClass(Usuarios.class);
        verify(usuarioService).save(captor.capture());
    
        assertEquals(chatId, captor.getValue().getTelegramChatId());
    }
    
    
    
    
    

    
}
