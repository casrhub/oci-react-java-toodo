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

    @GetMapping
    public List<Usuarios> getAllUsuarios() {
        return usuarioService.findAll();  
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuarios> getUsuarioById(@PathVariable Integer id) {
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Usuarios> getUsuarioByEmail(@PathVariable String email) {
        return usuarioService.findByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/telegram/{chatId}")
    public ResponseEntity<Usuarios> getUsuarioByTelegramChatId(@PathVariable Long chatId) {
        return usuarioService.findByTelegramChatId(chatId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Map<String, Integer>> createUsuario(@RequestBody Usuarios usuario) {
        Usuarios saved = usuarioService.save(usuario);
        Map<String, Integer> response = new HashMap<>();
        response.put("usuario_id", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PutMapping("/{id}")
    public ResponseEntity<Usuarios> updateUsuario(@PathVariable Integer id, @RequestBody Usuarios updatedUserData) {
        return usuarioService.findById(id).map(existing -> {
            existing.setNombre(updatedUserData.getNombre());
            existing.setEmail(updatedUserData.getEmail());
            existing.setRol(updatedUserData.getRol());
            existing.setEquipoId(updatedUserData.getEquipoId());
            existing.setTelegramChatId(updatedUserData.getTelegramChatId());
            return ResponseEntity.ok(usuarioService.save(existing));
        }).orElse(ResponseEntity.notFound().build());
    }

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
