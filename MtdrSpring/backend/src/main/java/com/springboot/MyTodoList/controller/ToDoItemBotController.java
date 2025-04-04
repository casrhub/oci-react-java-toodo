package com.springboot.MyTodoList.controller;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.springboot.MyTodoList.model.Sprint;
import com.springboot.MyTodoList.model.SubTarea;
import com.springboot.MyTodoList.model.Tarea;
import com.springboot.MyTodoList.model.ToDoItem;
import com.springboot.MyTodoList.model.Usuarios;
import com.springboot.MyTodoList.service.SprintService;
import com.springboot.MyTodoList.service.SubTareaService;
import com.springboot.MyTodoList.service.TareaService;
import com.springboot.MyTodoList.service.ToDoItemService;
import com.springboot.MyTodoList.service.UsuarioService;
import com.springboot.MyTodoList.util.BotCommands;
import com.springboot.MyTodoList.util.BotHelper;
import com.springboot.MyTodoList.util.BotLabels;
import com.springboot.MyTodoList.util.BotMessages;

public class ToDoItemBotController extends TelegramLongPollingBot {

	private Map<Long, Map<String, Object>> taskCreationState = new HashMap<>();
	private Map<Long, String> taskStep = new HashMap<>();

	private SubTareaService subTareaService;
	private final SprintService sprintService;

	private TareaService tareaService;

	// State tracker for awaiting commands
	private Map<Long, Long> pendingSprintTareaId = new HashMap<>();

	private Map<Long, String> sessionState = new HashMap<>();
	private Map<Long, Integer> pendingTaskId = new HashMap<>();
	private Map<Long, Long> pendingTaskIdTwo = new HashMap<>();
	private Map<Long, String> pendingDate = new HashMap<>();
	private Map<Long, String> pendingLinkCodes = new HashMap<>();
	private static final Logger logger = LoggerFactory.getLogger(ToDoItemBotController.class);
	private ToDoItemService toDoItemService;
	private String botName;
	private UsuarioService usuarioService;

	public ToDoItemBotController(String botToken,
			String botName,
			TareaService tareaService,
			SprintService sprintService, // 🟢 Put SprintService here
			UsuarioService usuarioService) {
		super(botToken);
		this.tareaService = tareaService;
		this.sprintService = sprintService;
		this.usuarioService = usuarioService;
		this.botName = botName;
	}

	@Override
	public void onUpdateReceived(Update update) {

		if (update.hasMessage() && update.getMessage().hasText()) {

			String messageTextFromTelegram = update.getMessage().getText();
			long chatId = update.getMessage().getChatId();

			if (messageTextFromTelegram.equals(BotCommands.START_COMMAND.getCommand())
					|| messageTextFromTelegram.equals(BotLabels.SHOW_MAIN_SCREEN.getLabel())) {

				SendMessage messageToTelegram = new SendMessage();
				messageToTelegram.setChatId(chatId);
				messageToTelegram.setText(BotMessages.HELLO_MYTODO_BOT.getMessage());

				ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
				List<KeyboardRow> keyboard = new ArrayList<>();

				// first row
				KeyboardRow row = new KeyboardRow();
				row.add(BotLabels.LIST_ALL_ITEMS.getLabel());
				row.add(BotLabels.ADD_NEW_ITEM.getLabel());
				// Add the first row to the keyboard
				keyboard.add(row);

				// second row
				row = new KeyboardRow();
				row.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
				row.add(BotLabels.HIDE_MAIN_SCREEN.getLabel());
				keyboard.add(row);

				// Set the keyboard
				keyboardMarkup.setKeyboard(keyboard);

				// Add the keyboard markup
				messageToTelegram.setReplyMarkup(keyboardMarkup);

				try {
					execute(messageToTelegram);
				} catch (TelegramApiException e) {
					logger.error(e.getLocalizedMessage(), e);
				}

			} else if (messageTextFromTelegram.indexOf(BotLabels.DONE.getLabel()) != -1) {

				String done = messageTextFromTelegram.substring(0,
						messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
				Integer id = Integer.valueOf(done);

				try {

					ToDoItem item = getToDoItemById(id).getBody();
					item.setDone(true);
					updateToDoItem(item, id);
					BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DONE.getMessage(), this);

				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}

			} else if (messageTextFromTelegram.indexOf(BotLabels.UNDO.getLabel()) != -1) {

				String undo = messageTextFromTelegram.substring(0,
						messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
				Integer id = Integer.valueOf(undo);

				try {

					ToDoItem item = getToDoItemById(id).getBody();
					item.setDone(false);
					updateToDoItem(item, id);
					BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_UNDONE.getMessage(), this);

				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}

			} else if (messageTextFromTelegram.indexOf(BotLabels.DELETE.getLabel()) != -1) {

				String delete = messageTextFromTelegram.substring(0,
						messageTextFromTelegram.indexOf(BotLabels.DASH.getLabel()));
				Integer id = Integer.valueOf(delete);

				try {

					deleteToDoItem(id).getBody();
					BotHelper.sendMessageToTelegram(chatId, BotMessages.ITEM_DELETED.getMessage(), this);

				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}

			} else if (messageTextFromTelegram.equals(BotCommands.HIDE_COMMAND.getCommand())
					|| messageTextFromTelegram.equals(BotLabels.HIDE_MAIN_SCREEN.getLabel())) {

				BotHelper.sendMessageToTelegram(chatId, BotMessages.BYE.getMessage(), this);

			} else if (messageTextFromTelegram.equals(BotCommands.TODO_LIST.getCommand())
					|| messageTextFromTelegram.equals(BotLabels.LIST_ALL_ITEMS.getLabel())
					|| messageTextFromTelegram.equals(BotLabels.MY_TODO_LIST.getLabel())) {

				List<Tarea> allItems = getAllTareas();
				List<Tarea> activeItems = allItems.stream().filter(t -> !"completado".equalsIgnoreCase(t.getEstado()))
						.collect(Collectors.toList());
				List<Tarea> doneItems = allItems.stream().filter(t -> "completado".equalsIgnoreCase(t.getEstado()))
						.collect(Collectors.toList());

				ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
				List<KeyboardRow> keyboard = new ArrayList<>();

				// command back to main screen
				KeyboardRow mainScreenRowTop = new KeyboardRow();
				mainScreenRowTop.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
				keyboard.add(mainScreenRowTop);

				KeyboardRow firstRow = new KeyboardRow();
				firstRow.add(BotLabels.ADD_NEW_ITEM.getLabel());
				keyboard.add(firstRow);

				KeyboardRow myTodoListTitleRow = new KeyboardRow();
				myTodoListTitleRow.add(BotLabels.MY_TODO_LIST.getLabel());
				keyboard.add(myTodoListTitleRow);

				List<Tarea> activeTareas = allItems.stream()
						.filter(item -> !"completado".equalsIgnoreCase(item.getEstado()))
						.collect(Collectors.toList());

				List<Tarea> doneTareas = allItems.stream()
						.filter(item -> "completado".equalsIgnoreCase(item.getEstado()))
						.collect(Collectors.toList());

				for (Tarea item : activeTareas) {
					KeyboardRow currentRow = new KeyboardRow();
					currentRow.add(item.getTitulo());
					currentRow.add(item.getTareaId() + BotLabels.DASH.getLabel() + BotLabels.DONE.getLabel());
					keyboard.add(currentRow);
				}

				for (Tarea item : doneItems) {
					KeyboardRow currentRow = new KeyboardRow();
					currentRow.add(item.getDescripcion());
					currentRow.add(item.getTareaId() + BotLabels.DASH.getLabel() + BotLabels.UNDO.getLabel());
					currentRow.add(item.getTareaId() + BotLabels.DASH.getLabel() + BotLabels.DELETE.getLabel());
					keyboard.add(currentRow);
				}

				// command back to main screen
				KeyboardRow mainScreenRowBottom = new KeyboardRow();
				mainScreenRowBottom.add(BotLabels.SHOW_MAIN_SCREEN.getLabel());
				keyboard.add(mainScreenRowBottom);

				keyboardMarkup.setKeyboard(keyboard);

				SendMessage messageToTelegram = new SendMessage();
				messageToTelegram.setChatId(chatId);
				messageToTelegram.setText(BotLabels.MY_TODO_LIST.getLabel());
				messageToTelegram.setReplyMarkup(keyboardMarkup);

				try {
					execute(messageToTelegram);
				} catch (TelegramApiException e) {
					logger.error(e.getLocalizedMessage(), e);
				}

			} else if (messageTextFromTelegram.equals(BotCommands.ADD_ITEM.getCommand())
					|| messageTextFromTelegram.equals(BotLabels.ADD_NEW_ITEM.getLabel())) {
				try {
					SendMessage messageToTelegram = new SendMessage();
					messageToTelegram.setChatId(chatId);
					messageToTelegram.setText(BotMessages.TYPE_NEW_TODO_ITEM.getMessage());
					// hide keyboard
					ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove(true);
					messageToTelegram.setReplyMarkup(keyboardMarkup);

					// send message
					execute(messageToTelegram);

				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
				// deadline command
			} else if (messageTextFromTelegram.startsWith(BotCommands.SET_DEADLINE.getCommand())) {
				try {
					// Example command format: "/setdeadline 123 2024-03-20T12:00:00Z"
					String[] parts = messageTextFromTelegram.split(" ");
					if (parts.length != 2) {
						BotHelper.sendMessageToTelegram(chatId, "Usage: /setdeadline <task_id>", this);
						return;
					}

					int taskId = Integer.parseInt(parts[1]);
					pendingTaskId.put(chatId, taskId);
					sessionState.put(chatId, "AWAITING_DATE");
					BotHelper.sendMessageToTelegram(chatId, "Please enter the deadline date (DD-MM-YYYY):", this);

				} catch (Exception e) {
					logger.error("Error setting deadline: " + e.getMessage(), e);
					BotHelper.sendMessageToTelegram(chatId, BotMessages.ERROR_SETTING_DEADLINE.getMessage(), this);// This
																													// message
																													// comes
																													// from
																													// util/BotMessages

				}
				// Wait for Time
			} else if (sessionState.get(chatId) != null && sessionState.get(chatId).equals("AWAITING_DATE")) {
				pendingDate.put(chatId, messageTextFromTelegram); // Store date input
				sessionState.put(chatId, "AWAITING_TIME");

				BotHelper.sendMessageToTelegram(chatId, "Please enter the time (HH:MM, 24-hour format):", this);
			} else if (sessionState.get(chatId) != null && sessionState.get(chatId).equals("AWAITING_TIME")) {
				try {
					String dateInput = pendingDate.get(chatId);
					String timeInput = messageTextFromTelegram;

					// Convert to ISO format
					String[] dateParts = dateInput.split("-");
					String[] timeParts = timeInput.split(":");

					String isoDeadline = String.format(
							"%s-%s-%sT%s:%s:00Z",
							dateParts[2], dateParts[1], dateParts[0], // Convert DD-MM-YYYY → YYYY-MM-DD
							timeParts[0], timeParts[1] // HH:MM
					);

					OffsetDateTime deadline = OffsetDateTime.parse(isoDeadline);
					int taskId = pendingTaskId.get(chatId);

					// Save deadline in database
					ResponseEntity<ToDoItem> response = toDoItemService.updateDeadline(taskId, deadline);
					if (response.getStatusCode() == HttpStatus.OK) {
						BotHelper.sendMessageToTelegram(chatId, "✅ Deadline set successfully!", this);
					} else {
						BotHelper.sendMessageToTelegram(chatId, "⚠️ Task not found.", this);
					}

					// Clear session state
					sessionState.remove(chatId);
					pendingTaskId.remove(chatId);
					pendingDate.remove(chatId);
					return;
				} catch (Exception e) {
					BotHelper.sendMessageToTelegram(chatId, "❌ Error setting deadline. Please try again.", this);
					return;
				}
			}
			// Handle /link command
			else if (messageTextFromTelegram.startsWith("/link")) {
				try {
					String[] parts = messageTextFromTelegram.split(" ");
					if (parts.length != 2) {
						BotHelper.sendMessageToTelegram(chatId, "Usage: /link <email>", this);
						return;
					}

					String email = parts[1];
					logger.info("🔗 Attempting to link Telegram chatId {} with email {}", chatId, email);

					Optional<Usuarios> userOpt = usuarioService.findByEmail(email);

					if (userOpt.isPresent()) {
						Usuarios user = userOpt.get();
						logger.info("✅ Found user: {} - Linking now...", user.getEmail());

						user.setTelegramChatId(chatId);
						usuarioService.save(user);

						BotHelper.sendMessageToTelegram(chatId, "✅ Your account has been linked!", this);
					} else {
						logger.warn("❌ No user found with email: {}", email);
						BotHelper.sendMessageToTelegram(chatId, "❌ Email not found. Please verify and try again.",
								this);
					}
				} catch (Exception e) {
					logger.error("❌ Linking error", e);
					BotHelper.sendMessageToTelegram(chatId, "❌ Unexpected error during linking.", this);
				}
			} else if (messageTextFromTelegram.equals("/newtask")) {
				taskCreationState.put(chatId, new HashMap<>());
				taskStep.put(chatId, "AWAITING_TITLE");
				BotHelper.sendMessageToTelegram(chatId, "📝 What's the title of the task?", this);
			}

			else if (taskStep.containsKey(chatId)) {
				String step = taskStep.get(chatId);
				Map<String, Object> data = taskCreationState.get(chatId);

				switch (step) {
					case "AWAITING_TITLE":
						data.put("titulo", messageTextFromTelegram);
						taskStep.put(chatId, "AWAITING_DESCRIPTION");
						BotHelper.sendMessageToTelegram(chatId, "✍️ Now enter the description:", this);
						break;

					case "AWAITING_DESCRIPTION":
						data.put("descripcion", messageTextFromTelegram);
						taskStep.put(chatId, "AWAITING_HOURS");
						BotHelper.sendMessageToTelegram(chatId, "⏱ Estimated hours (number):", this);
						break;

					case "AWAITING_HOURS":
						int estimated = Integer.parseInt(messageTextFromTelegram);
						data.put("horasEstimadas", Math.min(estimated, 4)); // Cap at 4h
						if (estimated > 4) {
							data.put("remainingHours", estimated - 4);
							data.put("needsSubtasks", true); // signal for later
						}
						taskStep.put(chatId, "AWAITING_USER");
						BotHelper.sendMessageToTelegram(chatId, "👤 What's your user ID?", this);
						break;

					case "AWAITING_USER":
						data.put("usuarioId", Long.parseLong(messageTextFromTelegram));
						taskStep.put(chatId, "AWAITING_EQUIPO");
						BotHelper.sendMessageToTelegram(chatId, "👥 What's the Equipo ID?", this);
						break;

					case "AWAITING_EQUIPO":
						data.put("equipoId", Long.parseLong(messageTextFromTelegram));
						taskStep.put(chatId, "AWAITING_PROYECTO");
						BotHelper.sendMessageToTelegram(chatId, "📁 What's the Proyecto ID?", this);
						break;

					case "AWAITING_PROYECTO":
						data.put("proyectoId", Long.parseLong(messageTextFromTelegram));
						data.put("estado", "pendiente");
						data.put("fechaCreacion", OffsetDateTime.now().toString());

						if (Boolean.TRUE.equals(data.get("needsSubtasks"))) {
							data.put("subtasks", new ArrayList<Map<String, Object>>());
							taskStep.put(chatId, "AWAITING_SUBTASK_TITLE");
							BotHelper.sendMessageToTelegram(chatId,
									"⛏ Now let's break it into subtasks. Enter the first subtask title:", this);
						} else {
							// Proceed to create the task
							createTareaAndSubtasks(chatId, data);
						}
						break;

					case "AWAITING_SUBTASK_TITLE":
						// Ask for subtask hours
						data.put("currentSubtaskTitle", messageTextFromTelegram);
						taskStep.put(chatId, "AWAITING_SUBTASK_HOURS");
						BotHelper.sendMessageToTelegram(chatId, "⌛ How many hours for this subtask?", this);
						break;

					case "AWAITING_SUBTASK_HOURS":
						try {
							BigDecimal hours = new BigDecimal(messageTextFromTelegram);
							Map<String, Object> subtask = new HashMap<>();
							subtask.put("titulo", data.get("currentSubtaskTitle"));
							subtask.put("horasEstimadas", hours);

							List<Map<String, Object>> subs = (List<Map<String, Object>>) data.get("subtasks");
							subs.add(subtask);

							BigDecimal remaining = new BigDecimal(data.get("remainingHours").toString())
									.subtract(hours);
							data.put("remainingHours", remaining);

							if (remaining.compareTo(BigDecimal.ZERO) > 0) {
								taskStep.put(chatId, "AWAITING_SUBTASK_TITLE");
								BotHelper.sendMessageToTelegram(chatId,
										"➕ Enter next subtask title (" + remaining + "h remaining):", this);
							} else {
								createTareaAndSubtasks(chatId, data); // All subtasks collected
							}
						} catch (NumberFormatException e) {
							BotHelper.sendMessageToTelegram(chatId,
									"❌ Invalid number. Please enter a valid number for hours.", this);
						}
						break;

				}
			} else if (messageTextFromTelegram.startsWith("/done")) {
				try {
					String[] parts = messageTextFromTelegram.split(" ");
					if (parts.length != 2) {
						BotHelper.sendMessageToTelegram(chatId, "Usage: /done <task_id>", this);
						return;
					}

					Long taskId = Long.parseLong(parts[1]);
					pendingTaskIdTwo.put(chatId, taskId);
					sessionState.put(chatId, "AWAITING_HORAS_REALES");

					BotHelper.sendMessageToTelegram(chatId, "⏱ How many real hours did you spend on this task?", this);
				} catch (Exception e) {
					logger.error("❌ Failed to parse task id", e);
					BotHelper.sendMessageToTelegram(chatId, "❌ Invalid task ID. Try again.", this);
				}
			} else if ("AWAITING_HORAS_REALES".equals(sessionState.get(chatId))) {
				try {
					BigDecimal realHours = new BigDecimal(messageTextFromTelegram);
					Long taskId = pendingTaskIdTwo.get(chatId);

					Map<String, Object> payload = new HashMap<>();
					payload.put("estado", "completado");
					payload.put("horasReales", realHours);

					// Call your existing TareaController endpoint
					Tarea updated = tareaService.markAsComplete(taskId, "completado", realHours);

					if (updated != null) {
						BotHelper.sendMessageToTelegram(chatId,
								"✅ Task marked as completed with " + realHours + " hours!", this);
					} else {
						BotHelper.sendMessageToTelegram(chatId, "❌ Task not found.", this);
					}

				} catch (Exception e) {
					logger.error("❌ Failed to set real hours", e);
					BotHelper.sendMessageToTelegram(chatId, "❌ Invalid input. Please enter a number (e.g., 2.5)", this);
					return; // Let them try again
				}

				// ✅ Clean up session state
				sessionState.remove(chatId);
				pendingTaskId.remove(chatId);
			} else if (messageTextFromTelegram.startsWith("/assignsprint")) {
				String[] parts = messageTextFromTelegram.split(" ");
				if (parts.length != 2) {
					BotHelper.sendMessageToTelegram(chatId, "⚠️ Usage: /assignsprint <TAREA_ID>", this);
					return;
				}

				try {
					Long tareaId = Long.parseLong(parts[1]);
					pendingSprintTareaId.put(chatId, tareaId);
					sessionState.put(chatId, "AWAITING_SPRINT_ID");
					BotHelper.sendMessageToTelegram(chatId, "📦 Please enter the SPRINT_ID to assign this task to:",
							this);
				} catch (NumberFormatException e) {
					BotHelper.sendMessageToTelegram(chatId, "❌ Invalid TAREA_ID format.", this);
				}
			} else if ("AWAITING_SPRINT_ID".equals(sessionState.get(chatId))) {
				try {
					Long sprintId = Long.parseLong(messageTextFromTelegram);
					Long tareaId = pendingSprintTareaId.get(chatId);

					Optional<Tarea> tareaOpt = tareaService.findById(tareaId);
					Optional<Sprint> sprintOpt = sprintService.findById(sprintId);

					if (tareaOpt.isPresent() && sprintOpt.isPresent()) {
						Tarea tarea = tareaOpt.get();
						tarea.setSprint(sprintOpt.get());
						tareaService.save(tarea);

						BotHelper.sendMessageToTelegram(chatId, "✅ Task " + tareaId + " assigned to sprint " + sprintId,
								this);
					} else {
						BotHelper.sendMessageToTelegram(chatId, "⚠️ Invalid TAREA_ID or SPRINT_ID. Please try again.",
								this);
					}

					// Clear session
					sessionState.remove(chatId);
					pendingSprintTareaId.remove(chatId);
				} catch (Exception e) {
					logger.error("❌ Error assigning sprint", e);
					BotHelper.sendMessageToTelegram(chatId, "❌ Something went wrong assigning the sprint.", this);
				}
			}

			else {
				try {
					ToDoItem newItem = new ToDoItem();
					newItem.setDescription(messageTextFromTelegram);
					newItem.setCreation_ts(OffsetDateTime.now());
					newItem.setDone(false);
					ResponseEntity entity = addToDoItem(newItem);

					SendMessage messageToTelegram = new SendMessage();
					messageToTelegram.setChatId(chatId);
					messageToTelegram.setText(BotMessages.NEW_ITEM_ADDED.getMessage());

					execute(messageToTelegram);
				} catch (Exception e) {
					logger.error(e.getLocalizedMessage(), e);
				}
			}
		}

	}

	private void createTareaAndSubtasks(long chatId, Map<String, Object> data) {
		try {
			Tarea tarea = new Tarea();
			tarea.setTitulo((String) data.get("titulo"));
			tarea.setDescripcion((String) data.get("descripcion"));
			tarea.setUsuarioId((Long) data.get("usuarioId"));
			tarea.setEquipoId((Long) data.get("equipoId"));
			tarea.setProyectoId((Long) data.get("proyectoId"));
			tarea.setHorasEstimadas(new BigDecimal(data.get("horasEstimadas").toString()));
			tarea.setEstado((String) data.get("estado"));
			tarea.setFechaCreacion(OffsetDateTime.parse(data.get("fechaCreacion").toString()));

			Tarea saved = tareaService.save(tarea);

			List<Map<String, Object>> subtasks = (List<Map<String, Object>>) data.getOrDefault("subtasks",
					new ArrayList<>());
			for (Map<String, Object> sub : subtasks) {
				SubTarea subTarea = new SubTarea();
				subTarea.setTarea(saved);
				subTarea.setTitulo((String) sub.get("titulo"));
				subTarea.setDescripcion("From bot");
				subTarea.setEstado("pendiente");
				subTarea.setHorasEstimadas(new BigDecimal(sub.get("horasEstimadas").toString()));
				subTarea.setHorasReales(BigDecimal.ZERO);
				subTarea.setFechaCreacion(OffsetDateTime.now());

				subTareaService.save(subTarea);
			}

			BotHelper.sendMessageToTelegram(chatId, "✅ Task and subtasks created!", this);
		} catch (Exception e) {
			logger.error("❌ Error creating task or subtasks", e);
			BotHelper.sendMessageToTelegram(chatId, "❌ Something went wrong. Try again later.", this);
		}
		taskStep.remove(chatId);
		taskCreationState.remove(chatId);
	}

	@Override
	public String getBotUsername() {
		return botName;
	}

	// GET /todolist
	public List<Tarea> getAllTareas() {
		return tareaService.findAll();
	}

	// GET BY ID /todolist/{id}
	public ResponseEntity<ToDoItem> getToDoItemById(@PathVariable int id) {
		try {
			ResponseEntity<ToDoItem> responseEntity = toDoItemService.getItemById(id);
			return new ResponseEntity<ToDoItem>(responseEntity.getBody(), HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// PUT /todolist
	public ResponseEntity<ToDoItem> addToDoItem(@RequestBody ToDoItem todoItem) throws Exception {
		ToDoItem td = toDoItemService.addToDoItem(todoItem);
		HttpHeaders responseHeaders = new HttpHeaders();
		responseHeaders.set("location", "" + td.getID());
		responseHeaders.set("Access-Control-Expose-Headers", "location");
		// URI location = URI.create(""+td.getID())

		return ResponseEntity.ok().headers(responseHeaders).build();
	}

	// UPDATE /todolist/{id}
	public ResponseEntity<ToDoItem> updateToDoItem(@RequestBody ToDoItem toDoItem, @PathVariable int id) {
		try {
			ToDoItem toDoItem1 = toDoItemService.updateToDoItem(id, toDoItem);
			System.out.println(toDoItem1.toString());
			return new ResponseEntity<>(toDoItem1, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		}
	}

	// DELETE todolist/{id}
	public ResponseEntity<Boolean> deleteToDoItem(@PathVariable("id") int id) {
		Boolean flag = false;
		try {
			flag = toDoItemService.deleteToDoItem(id);
			return new ResponseEntity<>(flag, HttpStatus.OK);
		} catch (Exception e) {
			logger.error(e.getLocalizedMessage(), e);
			return new ResponseEntity<>(flag, HttpStatus.NOT_FOUND);
		}

	}

}