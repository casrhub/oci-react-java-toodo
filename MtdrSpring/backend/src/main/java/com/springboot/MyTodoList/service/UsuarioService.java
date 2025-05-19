package com.springboot.MyTodoList.service;

import com.springboot.MyTodoList.model.Usuarios;
import com.springboot.MyTodoList.repository.UsuariosRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Service class for managing Usuarios (Users) entities.
 * Provides CRUD operations and user management functionality.
 */
@Service
public class UsuarioService {

  private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

  private final UsuariosRepository usuarioRepository;

  /**
   * Constructor for UsuarioService.
   * @param usuarioRepository Repository for Usuarios entities
   */
  public UsuarioService(UsuariosRepository usuarioRepository) {
    this.usuarioRepository = usuarioRepository;
  }

  /**
   * Retrieves a user by their email.
   * @param email The email to search for
   * @return Optional containing the user if found
   */
  public Optional<Usuarios> findUserByEmail(String email) {
    Assert.hasText(email, "Email must not be empty");
    return usuarioRepository.findByEmail(email);
  }

  /**
   * Retrieves a user by their Telegram chat ID.
   * @param telegramChatId The Telegram chat ID to search for
   * @return Optional containing the user if found
   */
  public Optional<Usuarios> findUserByTelegramChatId(Long telegramChatId) {
    Assert.notNull(telegramChatId, "Telegram chat ID must not be null");
    return usuarioRepository.findByTelegramChatId(telegramChatId);
  }

  /**
   * Links a user to their Telegram account.
   * @param telegramId The Telegram ID to link
   * @param usuarioId The user ID to link with
   * @return true if the linking was successful, false otherwise
   */
  public boolean linkUserToTelegram(Long telegramId, Integer usuarioId) {
    Assert.notNull(telegramId, "Telegram ID must not be null");
    Assert.notNull(usuarioId, "User ID must not be null");

    logger.info("Looking for user with ID: {}", usuarioId);
    Optional<Usuarios> userOpt = usuarioRepository.findById(usuarioId);

    if (userOpt.isPresent()) {
      Usuarios user = userOpt.get();
      user.setTelegramChatId(telegramId);
      usuarioRepository.save(user);
      logger.info("User {} linked to Telegram ID {}", usuarioId, telegramId);
      return true;
    }

    logger.warn("User ID {} not found", usuarioId);
    return false;
  }

  /**
   * Retrieves a user by their ID.
   * @param id The ID of the user to find
   * @return Optional containing the user if found
   */
  public Optional<Usuarios> findUserById(Integer id) {
    Assert.notNull(id, "User ID must not be null");
    return usuarioRepository.findById(id);
  }

  /**
   * Creates a new user.
   * @param user The user to create
   * @return The created user
   */
  public Usuarios createUser(Usuarios user) {
    Assert.notNull(user, "User must not be null");
    validateUser(user);
    return usuarioRepository.save(user);
  }

  /**
   * Updates an existing user.
   * @param id The ID of the user to update
   * @param updatedUser The updated user data
   * @return Optional containing the updated user if found
   */
  public Optional<Usuarios> updateUser(Integer id, Usuarios updatedUser) {
    Assert.notNull(id, "User ID must not be null");
    Assert.notNull(updatedUser, "Updated user must not be null");
    validateUser(updatedUser);

    return usuarioRepository.findById(id)
        .map(user -> {
          user.setNombre(updatedUser.getNombre());
          user.setEmail(updatedUser.getEmail());
          user.setRol(updatedUser.getRol());
          user.setEquipoId(updatedUser.getEquipoId());
          user.setTelegramChatId(updatedUser.getTelegramChatId());
          return usuarioRepository.save(user);
        });
  }

  /**
   * Retrieves all users.
   * @return List of all users
   */
  public List<Usuarios> findAllUsers() {
    return usuarioRepository.findAll();
  }

  /**
   * Deletes a user by their ID.
   * @param id The ID of the user to delete
   * @return true if the user was deleted, false if they didn't exist
   */
  public boolean deleteUser(Integer id) {
    Assert.notNull(id, "User ID must not be null");
    if (usuarioRepository.existsById(id)) {
      usuarioRepository.deleteById(id);
      return true;
    }
    return false;
  }

  /**
   * Retrieves all users in a specific team.
   * @param equipoId The team ID to search for
   * @return List of users in the specified team
   */
  public List<Usuarios> findUsersByTeamId(Integer equipoId) {
    Assert.notNull(equipoId, "Team ID must not be null");
    return usuarioRepository.findByEquipoId(equipoId);
  }

  /**
   * Retrieves all users with a specific role.
   * @param rol The role to search for
   * @return List of users with the specified role
   */
  public List<Usuarios> findUsersByRole(String rol) {
    Assert.hasText(rol, "Role must not be empty");
    return usuarioRepository.findByRol(rol);
  }

  /**
   * Validates a user entity.
   * @param user The user to validate
   * @throws IllegalArgumentException if the user is invalid
   */
  private void validateUser(Usuarios user) {
    Assert.hasText(user.getNombre(), "User name must not be empty");
    Assert.hasText(user.getEmail(), "User email must not be empty");
    Assert.hasText(user.getRol(), "User role must not be empty");
    Assert.notNull(user.getEquipoId(), "Team ID must not be null");
  }
}
