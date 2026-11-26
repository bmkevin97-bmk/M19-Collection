package com.m19collection.api.controller;

import com.m19collection.api.dto.AssignSeriesRequest;
import com.m19collection.api.dto.ModeloRequest;
import com.m19collection.api.dto.ModeloResponse;
import com.m19collection.api.entity.Serie;
import com.m19collection.api.service.ModeloService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modelos")
public class ModeloController {

    private final ModeloService modeloService;

    public ModeloController(ModeloService modeloService) {
        this.modeloService = modeloService;
    }

    @GetMapping
    public List<ModeloResponse> getAll() {
        return modeloService.findAll().stream().map(ModeloResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ModeloResponse getById(@PathVariable Long id) {
        return ModeloResponse.from(modeloService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ModeloResponse> create(@Valid @RequestBody ModeloRequest request) {
        return ResponseEntity.status(201)
                .body(ModeloResponse.from(modeloService.create(request)));
    }

    @PutMapping("/{id}")
    public ModeloResponse update(@PathVariable Long id,
                                 @Valid @RequestBody ModeloRequest request) {
        return ModeloResponse.from(modeloService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        modeloService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/series")
    public List<Serie> getSeries(@PathVariable Long id) {
        return modeloService.obtenerSeries(id);
    }

    @PutMapping("/{id}/series")
    public List<Serie> assignSeries(@PathVariable Long id,
                                    @RequestBody AssignSeriesRequest request) {
        return modeloService.asignarSeries(id, request.serieIds());
    }
}