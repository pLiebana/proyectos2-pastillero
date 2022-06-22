package com.example.application.data.service;

import com.example.application.data.entity.Usuarios;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuarios, UUID> {

}