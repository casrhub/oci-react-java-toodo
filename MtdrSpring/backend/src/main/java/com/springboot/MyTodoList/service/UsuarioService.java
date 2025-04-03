package com.springboot.MyTodoList.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.springboot.MyTodoList.model.Usuarios;
import com.springboot.MyTodoList.repository.UsuariosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioService.class);

    @Autowired
    private UsuariosRepository usuarioRepository;

    public Optional<Usuarios> findByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public Optional<Usuarios> findByTelegramChatId(Long telegramChatId) {
        return usuarioRepository.findByTelegramChatId(telegramChatId);
    }

    public boolean linkTelegramUser(Long telegramId, Integer usuarioId) {
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

    public Optional<Usuarios> findById(Integer id) {
        return usuarioRepository.findById(id);
    }
    public Usuarios save(Usuarios usuario) {
        return usuarioRepository.save(usuario);
    }
    
}
