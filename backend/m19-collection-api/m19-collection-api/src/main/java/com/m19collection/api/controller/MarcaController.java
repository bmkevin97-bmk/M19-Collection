package com.m19collection.api.controller;

import com.m19collection.api.entity.Marca;
import com.m19collection.api.service.MarcaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
public class MarcaController {

    private final MarcaService marcaService;

    public MarcaController(MarcaService marcaService){
        this.marcaService =marcaService;
    }

    @GetMapping
    public List<Marca> obtenerTodas() {
        return marcaService.obtenerTodas();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Marca> obtenerPorId(@PathVariable Long id) {
        Marca marca = marcaService.obtenerPorId(id);

        if(marca == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(marca);
    }
    @PostMapping
    public ResponseEntity<Marca> crear(@RequestBody Marca marca){
        try {
            Marca nuevaMarca = marcaService.crear(marca);
            return ResponseEntity.status(201).body(nuevaMarca);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}