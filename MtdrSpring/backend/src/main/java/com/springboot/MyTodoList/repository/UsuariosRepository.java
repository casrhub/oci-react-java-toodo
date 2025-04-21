package com.springboot.MyTodoList.repository;

import com.springboot.MyTodoList.model.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuariosRepository extends JpaRepository<Usuarios, Integer> {
    Optional<Usuarios> findByEmail(String email);
    Optional<Usuarios> findByTelegramChatId(Long telegramChatId);
    List<Usuarios> findByEquipoId(Integer equipoId);
    List<Usuarios> findByRol(String rol);
}