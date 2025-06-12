package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Usuarios;
import com.springboot.MyTodoList.service.UsuarioService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

  @Autowired private UsuarioService usuarioService;

  @GetMapping
  public List<Usuarios> getAllUsuarios() {
    return usuarioService.findAll();
  }

  @GetMapping("/{id}")
  public ResponseEntity<Usuarios> getUsuarioById(@PathVariable Integer id) {
    return usuarioService
        .findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/equipo/{equipoId}")
  public List<Usuarios> getUsuariosByEquipoId(@PathVariable Integer equipoId) {
    return usuarioService.findByEquipoId(equipoId);
  }

  @GetMapping("/email/{email}")
  public ResponseEntity<Usuarios> getUsuarioByEmail(@PathVariable String email) {
    return usuarioService
        .findByEmail(email)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/telegram/{chatId}")
  public ResponseEntity<Usuarios> getUsuarioByTelegramChatId(@PathVariable Long chatId) {
    return usuarioService
        .findByTelegramChatId(chatId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/rol/{rol}")
  public List<Usuarios> getUsuariosByRol(@PathVariable String rol) {
    return usuarioService.findByRol(rol);
  }

  @PostMapping
  public ResponseEntity<Map<String, Integer>> createUsuario(@RequestBody Usuarios usuario) {
    Usuarios saved = usuarioService.save(usuario);
    Map<String, Integer> response = new HashMap<>();
    response.put("usuario_id", saved.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @PutMapping("/{id}")
  public ResponseEntity<?> updateUsuario(
      @PathVariable Integer id, @RequestBody Usuarios newUserData) {
    if (newUserData.getRol() != null
        && newUserData.getRol().toLowerCase().contains("manager")
        && newUserData.getRol().contains(",")) {
      Map<String, String> error = new HashMap<>();
      error.put("error", "El usuario con rol manager no puede tener otro rol");
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
    return usuarioService
        .findById(id)
        .map(
            existing -> {
              existing.setNombre(newUserData.getNombre());
              existing.setEmail(newUserData.getEmail());
              existing.setRol(newUserData.getRol());
              existing.setEquipoId(newUserData.getEquipoId());
              existing.setTelegramChatId(newUserData.getTelegramChatId());
              return ResponseEntity.ok(usuarioService.save(existing));
            })
        .orElse(ResponseEntity.notFound().build());
  }

  @PutMapping("/{id}/telegram")
  public ResponseEntity<String> linkTelegram(
      @PathVariable Integer id, @RequestBody Long telegramChatId) {
    boolean linked = usuarioService.linkTelegramUser(telegramChatId, id);
    return linked ? ResponseEntity.ok("Telegram linked") : ResponseEntity.notFound().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id) {
    Optional<Usuarios> optionalUsuarios = usuarioService.findById(id);
    if (optionalUsuarios.isPresent()) {
      usuarioService.deleteById(id);
      return ResponseEntity.ok().build();
    } else {
      return ResponseEntity.notFound().build();
    }
  }
}
