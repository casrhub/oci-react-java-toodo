package com.springboot.MyTodoList.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.persistence.*;

@Entity
@Table(name = "USUARIOS", schema = "ADMIN")
public class Usuarios {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "USUARIO_ID")
  private Integer id;

  @Column(name = "NOMBRE")
  private String nombre;

  @Column(name = "EMAIL")
  private String email;

  @Column(name = "ROL")
  private String rol;

  @Column(name = "EQUIPO_ID")
  private Integer equipoId;

  @Column(name = "TELEGRAM_CHAT_ID")
  private Long telegramChatId;

  // Getters and Setters
  @JsonProperty("usuario_id") // para que los tests lo mappeen a como lo regresa el json
  public Integer getId() {
    return id;
  }

  @JsonProperty("usuario_id")
  public void setId(Integer id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getRol() {
    return rol;
  }

  public void setRol(String rol) {
    this.rol = rol;
  }

  @JsonProperty("equipo_id")
  public Integer getEquipoId() {
    return equipoId;
  }

  @JsonProperty("equipo_id")
  public void setEquipoId(Integer equipoId) {
    this.equipoId = equipoId;
  }

  public Long getTelegramChatId() {
    return telegramChatId;
  }

  public void setTelegramChatId(Long telegramChatId) {
    this.telegramChatId = telegramChatId;
  }
}
