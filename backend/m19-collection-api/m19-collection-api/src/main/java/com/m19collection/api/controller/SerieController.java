package com.m19collection.api.controller;

import com.m19collection.api.entity.Serie;
import com.m19collection.api.service.SerieService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/series")
public class SerieController {

    private final SerieService serieService;

    public SerieController(SerieService serieService) {
        this.serieService = serieService;
    }

    @GetMapping
    public List<Serie> obtenerTodas() {
        return serieService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Serie> obtenerPorId(@PathVariable Long id) {
        Serie serie = serieService.obtenerPorId(id);

        if (serie == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(serie);
    }

    @PostMapping
    public ResponseEntity<Serie> crear(@RequestBody Serie serie) {
        try {
            Serie nuevaSerie = serieService.crear(serie);
            return ResponseEntity.status(201).body(nuevaSerie);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
