package com.m19collection.api.controller;

import com.m19collection.api.dto.VarianteRequest;
import com.m19collection.api.dto.VarianteResponse;
import com.m19collection.api.service.VarianteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variantes")
public class VarianteController {

    private final VarianteService varianteService;

    public VarianteController(VarianteService varianteService) {
        this.varianteService = varianteService;
    }

    @GetMapping
    public List<VarianteResponse> obtenerTodas() {
        return varianteService.findAll().stream().map(VarianteResponse::from).toList();
    }

    @GetMapping("/{id}")
    public VarianteResponse obtenerPorId(@PathVariable Long id) {
        return VarianteResponse.from(varianteService.findById(id));
    }

    @PostMapping
    public ResponseEntity<VarianteResponse> crear(@Valid @RequestBody VarianteRequest request) {
        return ResponseEntity.status(201)
                .body(VarianteResponse.from(varianteService.create(request)));
    }

    @PutMapping("/{id}")
    public VarianteResponse actualizar(@PathVariable Long id,
                                       @Valid @RequestBody VarianteRequest request) {
        return VarianteResponse.from(varianteService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        varianteService.delete(id);
        return ResponseEntity.noContent().build();
    }
}