package com.m19collection.api.service;

import com.m19collection.api.entity.Usuario;
import com.m19collection.api.exception.BusinessRuleException;
import com.m19collection.api.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public List<Usuario> obtenerTodos() {
        return usuarioRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Transactional
    public Usuario crear(Usuario usuario) {
        String email = normalize(usuario.getEmail());

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new BusinessRuleException("Email already registered");
        }

        usuario.setEmail(email);
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        return usuarioRepository.save(usuario);
    }

    @Transactional
    public Usuario actualizar(Long id, String nombre, String email) {
        Usuario existente = usuarioRepository.findById(id).orElse(null);

        if (existente == null) {
            return null;
        }

        String normalized = normalize(email);

        if (usuarioRepository.existsByEmailIgnoreCaseAndIdNot(normalized, id)) {
            throw new BusinessRuleException("Email already registered");
        }

        existente.setNombre(nombre);
        existente.setEmail(normalized);
        return usuarioRepository.save(existente);
    }

    @Transactional
    public boolean eliminar(Long id) {
        if (!usuarioRepository.existsById(id)) {
            return false;
        }

        usuarioRepository.deleteById(id);
        return true;
    }

    private String normalize(String email) {
        return email.trim().toLowerCase();
    }
}