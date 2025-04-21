package com.springboot.MyTodoList.controller;

import com.springboot.MyTodoList.model.Usuarios;
import com.springboot.MyTodoList.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Map;
import org.springframework.http.HttpStatus;

@RestController
@RequestMapping("/usuarios")
public class UsuariosController {

    @Autowired
    private UsuarioService usuarioService;
    // GET ALL USERS
    @GetMapping
    public List<Usuarios> getAllUsuarios() {
        return usuarioService.findAll();  
    }
    // GET BY USER ID
    @GetMapping("/{id}")
    public ResponseEntity<Usuarios> getUsuarioById(@PathVariable Integer id) {
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // GET ALL USERS BY TEAM ID
    @GetMapping("/equipo/{equipoId}")
    public List<Usuarios> getUsuariosByEquipoId(@PathVariable Integer equipoId) {
        return usuarioService.findByEquipoId(equipoId);
    }
    // GET USER BY EMAIL 
    @GetMapping("/email/{email}")
    public ResponseEntity<Usuarios> getUsuarioByEmail(@PathVariable String email) {
        return usuarioService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // GET USER BY TELEGRAM CHAT ID
    @GetMapping("/telegram/{chatId}")
    public ResponseEntity<Usuarios> getUsuarioByTelegramChatId(@PathVariable Long chatId) {
        return usuarioService.findByTelegramChatId(chatId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    // GET USER BY ROLE
    @GetMapping("/rol/{rol}")
    public List<Usuarios> getUsuariosByRol(@PathVariable String rol) {
        return usuarioService.findByRol(rol);
    }
    // POST NEW USER
    @PostMapping
    public ResponseEntity<Map<String, Integer>> createUsuario(@RequestBody Usuarios usuario) {
        Usuarios saved = usuarioService.save(usuario);
        Map<String, Integer> response = new HashMap<>();
        response.put("usuario_id", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    // PUT UPDATE USER BY ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUsuario(@PathVariable Integer id, @RequestBody Usuarios newUserData) {
        // para que no se pueda añadir rol de dev a un manager
        if (newUserData.getRol() != null && newUserData.getRol().toLowerCase().contains("manager") 
            && newUserData.getRol().contains(",")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "El usuario con rol manager no puede tener otro rol"); 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
        return usuarioService.findById(id).map(existing -> {
            existing.setNombre(newUserData.getNombre());
            existing.setEmail(newUserData.getEmail());
            existing.setRol(newUserData.getRol());
            existing.setEquipoId(newUserData.getEquipoId());
            existing.setTelegramChatId(newUserData.getTelegramChatId());
            return ResponseEntity.ok(usuarioService.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }
    // PUT UPDATE LINK TELEGRAM
    @PutMapping("/{id}/telegram")
    public ResponseEntity<String> linkTelegram(@PathVariable Integer id, @RequestBody Long telegramChatId) {
        boolean linked = usuarioService.linkTelegramUser(telegramChatId, id);
        return linked ? ResponseEntity.ok("Telegram linked") : ResponseEntity.notFound().build();
    }

    // por verse ** (quizá terminemos sin dejar que se borren usuarios)
    // alternativa --> un bool de status para desactivar cuentas
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
