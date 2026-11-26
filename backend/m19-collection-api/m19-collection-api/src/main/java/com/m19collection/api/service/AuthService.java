package com.m19collection.api.service;

import com.m19collection.api.entity.Usuario;
import com.m19collection.api.exception.InvalidCredentialsException;
import com.m19collection.api.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenIssuer tokenIssuer;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       TokenIssuer tokenIssuer) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenIssuer = tokenIssuer;
    }

    @Transactional(readOnly = true)
    public TokenIssuer.IssuedToken login(String email, String password) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(email.trim()).orElse(null);

        if (usuario == null || !passwordEncoder.matches(password, usuario.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        return tokenIssuer.issue(usuario.getId(), usuario.getEmail(), usuario.getRole().name());
    }
}