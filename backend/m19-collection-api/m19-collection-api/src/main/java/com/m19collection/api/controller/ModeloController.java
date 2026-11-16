package com.m19collection.api.controller;

import com.m19collection.api.dto.AssignSeriesRequest;
import com.m19collection.api.entity.Modelo;
import com.m19collection.api.entity.Serie;
import com.m19collection.api.repository.ModeloRepository;
import com.m19collection.api.service.ModeloService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modelos")
public class ModeloController {

    private final ModeloRepository modeloRepository;
    private final ModeloService modeloService;

    public ModeloController(ModeloRepository modeloRepository, ModeloService modeloService) {
        this.modeloRepository = modeloRepository;
        this.modeloService = modeloService;
    }

    @GetMapping
    public List<Modelo> getAll() {
        return modeloRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Modelo> getById(@PathVariable Long id) {
        return modeloRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Modelo> create(@RequestBody Modelo modelo) {
        Modelo nuevoModelo = modeloRepository.save(modelo);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoModelo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Modelo> update(@PathVariable Long id, @RequestBody Modelo data) {
        return modeloRepository.findById(id)
                .map(m -> {
                    m.setNombre(data.getNombre());
                    m.setAnio(data.getAnio());
                    m.setDescripcion(data.getDescripcion());
                    return ResponseEntity.ok(modeloRepository.save(m));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!modeloRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        modeloRepository.deleteById(id);
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