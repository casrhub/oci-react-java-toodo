package com.springboot.MyTodoList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import com.springboot.MyTodoList.controller.ToDoItemBotController;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.UsuarioService;
import com.springboot.MyTodoList.util.BotMessages;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.SubTareaService;;

@SpringBootApplication
public class MyTodoListApplication implements CommandLineRunner {

	@Autowired
	private TareaService tareaService;
	@Autowired
	private SubTareaService subTareaService;
	@Autowired
	private SprintService sprintService;

	private static final Logger logger = LoggerFactory.getLogger(MyTodoListApplication.class);

	private final UsuarioService usuarioService;

	@Value("${telegram.bot.token}")
	private String telegramBotToken;

	@Value("${telegram.bot.name}")
	private String botName;

	@Autowired
	public MyTodoListApplication(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	public static void main(String[] args) {
		SpringApplication.run(MyTodoListApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			ToDoItemBotController bot = new ToDoItemBotController(telegramBotToken, botName, tareaService,
					sprintService, usuarioService);
			telegramBotsApi.registerBot(bot);
			logger.info(BotMessages.BOT_REGISTERED_STARTED.getMessage());
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
}