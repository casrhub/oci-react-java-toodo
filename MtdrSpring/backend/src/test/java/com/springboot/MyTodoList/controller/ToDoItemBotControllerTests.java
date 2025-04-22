package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.model.Usuarios;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.UsuarioService;
import com.springboot.MyTodoList.util.BotCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for ToDoItemBotController.
 */
public class ToDoItemBotControllerTests {

    @Mock
    private TareaService tareaService;

    @InjectMocks
    private ToDoItemBotController controller;   // used in simple service test

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    /* ----------------------------------------------------------------
       Simple service test (no Telegram mocks)
       -------------------------------------------------------------- */
    @Test
    public void testGetAllTareas_ReturnsListOfTareas() {
        Tarea t1 = new Tarea(1L, "Write tests", "Write unit tests", "pendiente");
        Tarea t2 = new Tarea(2L, "Fix bug", "Null‑pointer fix", "pendiente");

        when(tareaService.findAll()).thenReturn(Arrays.asList(t1, t2));

        List<Tarea> result = controller.getAllTareas();

        assertEquals(2, result.size());
        verify(tareaService, times(1)).findAll();
    }

    /* ----------------------------------------------------------------
       Test for /done command
       -------------------------------------------------------------- */
    @Test
    public void testDoneCommand_SetsPendingTaskIdAndSessionState() {
        long chatId = 123L;

        Message msg = mock(Message.class);
        when(msg.getText()).thenReturn("/done 42");
        when(msg.getChatId()).thenReturn(chatId);
        when(msg.hasText()).thenReturn(true);

        Update upd = mock(Update.class);
        when(upd.hasMessage()).thenReturn(true);
        when(upd.getMessage()).thenReturn(msg);

        ToDoItemBotController bot =
                new ToDoItemBotController("token", "Bot",
                        tareaService, null, null, null);

        bot.onUpdateReceived(upd);

        assertEquals(42L, bot.getPendingTaskIdTwo().get(chatId));
        assertEquals("AWAITING_HORAS_REALES", bot.getSessionState().get(chatId));
    }

    /* ----------------------------------------------------------------
       Test for /setdeadline command
       -------------------------------------------------------------- */
    @Test
    public void testSetDeadlineCommand_SetsStateCorrectly() {
        long chatId = 555L;

        Message msg = mock(Message.class);
        when(msg.getText()).thenReturn(BotCommands.SET_DEADLINE.getCommand() + " 99");
        when(msg.getChatId()).thenReturn(chatId);
        when(msg.hasText()).thenReturn(true);

        Update upd = mock(Update.class);
        when(upd.hasMessage()).thenReturn(true);
        when(upd.getMessage()).thenReturn(msg);

        ToDoItemBotController bot =
                new ToDoItemBotController("token", "Bot",
                        tareaService, null, null, null);

        bot.onUpdateReceived(upd);

        assertEquals(99, bot.getPendingTaskId().get(chatId));
        assertEquals("AWAITING_DATE", bot.getSessionState().get(chatId));
    }

    /* ----------------------------------------------------------------
       Test for /link command
       -------------------------------------------------------------- */
    @Test
    public void testLinkCommand_CallsSaveWithCorrectChatId() {
        long chatId = 999L;
        String email = "test@example.com";

        // mock user & service
        Usuarios user = new Usuarios();
        user.setEmail(email);
        UsuarioService usuarioService = mock(UsuarioService.class);
        when(usuarioService.findByEmail(email)).thenReturn(Optional.of(user));

        // mock telegram update
        Message msg = mock(Message.class);
        when(msg.getText()).thenReturn("/link " + email);
        when(msg.getChatId()).thenReturn(chatId);
        when(msg.hasText()).thenReturn(true);

        Update upd = mock(Update.class);
        when(upd.hasMessage()).thenReturn(true);
        when(upd.getMessage()).thenReturn(msg);

        ToDoItemBotController bot =
                new ToDoItemBotController("token", "Bot",
                        null, null, usuarioService, null);

        bot.onUpdateReceived(upd);

        ArgumentCaptor<Usuarios> captor = ArgumentCaptor.forClass(Usuarios.class);
        verify(usuarioService).save(captor.capture());

        assertEquals(chatId, captor.getValue().getTelegramChatId());
    }
}
