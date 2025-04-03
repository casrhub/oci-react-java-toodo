package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {
    Optional<Usuarios> findByEmail(String email);
    Optional<Usuarios> findByTelegramChatId(Long telegramChatId);
}