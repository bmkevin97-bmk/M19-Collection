package com.m19collection.api.controller;

import com.m19collection.api.entity.Usuario;
import com.m19collection.api.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.m19collection.api.dto.UsuarioResponse;
import com.m19collection.api.dto.UsuarioRequest;
import com.m19collection.api.dto.UsuarioUpdateRequest;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    private UsuarioResponse convertirAResponse(Usuario usuario){
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNombre(),
                usuario.getEmail()
        );
    }

    @GetMapping
    public List<UsuarioResponse> obtenerTodos(){
        return usuarioService.obtenerTodos().stream()
                .map(this::convertirAResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> obtenerPorId(@PathVariable Long id) {

        Usuario usuario = usuarioService.obtenerPorId(id);

        if (usuario == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertirAResponse(usuario));
    }

    @PostMapping
    public ResponseEntity<UsuarioResponse> crear(@Valid  @RequestBody UsuarioRequest request) {
        Usuario usuario = new Usuario(request.nombre(), request.email(), request.password());
        Usuario nuevoUsuario = usuarioService.crear(usuario);
        return ResponseEntity.status(201)
                .body(convertirAResponse(nuevoUsuario));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioUpdateRequest request) {

        Usuario usuarioActualizado =
                usuarioService.actualizar(id, request.nombre(), request.email());

        if (usuarioActualizado == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(convertirAResponse(usuarioActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        boolean eliminado = usuarioService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}