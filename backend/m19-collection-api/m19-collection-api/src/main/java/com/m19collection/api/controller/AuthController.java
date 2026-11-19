package com.m19collection.api.controller;

import com.m19collection.api.dto.AuthResponse;
import com.m19collection.api.dto.LoginRequest;
import com.m19collection.api.dto.UsuarioRequest;
import com.m19collection.api.dto.UsuarioResponse;
import com.m19collection.api.entity.Usuario;
import com.m19collection.api.service.AuthService;
import com.m19collection.api.service.TokenIssuer;
import com.m19collection.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    public AuthController(AuthService authService, UsuarioService usuarioService) {
        this.authService = authService;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody UsuarioRequest request) {
        Usuario usuario = usuarioService.crear(
                new Usuario(request.nombre(), request.email(), request.password()));
        return ResponseEntity.status(201)
                .body(new UsuarioResponse(usuario.getId(), usuario.getNombre(), usuario.getEmail()));
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        TokenIssuer.IssuedToken token = authService.login(request.email(), request.password());
        return new AuthResponse(token.value(), "Bearer", token.expiresInSeconds());
    }
}