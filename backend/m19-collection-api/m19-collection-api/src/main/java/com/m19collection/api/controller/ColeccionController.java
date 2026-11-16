package com.m19collection.api.controller;

import com.m19collection.api.entity.Coleccion;
import com.m19collection.api.service.ColeccionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/colecciones")
public class ColeccionController {

    @Autowired
    private ColeccionService coleccionService;

    @GetMapping
    public List<Coleccion> obtenerTodas() {
        return coleccionService.obtenerTodas();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Coleccion> obtenerPorId(@PathVariable Long id) {

        Coleccion coleccion = coleccionService.obtenerPorId(id);

        if (coleccion == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(coleccion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        boolean eliminado = coleccionService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}