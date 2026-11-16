package com.m19collection.api.controller;

import com.m19collection.api.entity.Variante;
import com.m19collection.api.repository.VarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/variantes")
public class VarianteController {

    @Autowired
    private VarianteRepository varianteRepository;

    @GetMapping
    public List<Variante> obtenerTodas() {
        return varianteRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Variante> obtenerPorId(@PathVariable Long id) {

        return varianteRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Variante> crear(@RequestBody Variante variante) {

        Variante nuevaVariante = varianteRepository.save(variante);

        return ResponseEntity.status(201).body(nuevaVariante);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Variante> actualizar(
            @PathVariable Long id,
            @RequestBody Variante variante) {

        return varianteRepository.findById(id)
                .map(varianteExistente -> {

                    varianteExistente.setColor(variante.getColor());
                    varianteExistente.setEdicion(variante.getEdicion());
                    varianteExistente.setDescripcion(variante.getDescripcion());
                    varianteExistente.setImagen(variante.getImagen());
                    varianteExistente.setModelo(variante.getModelo());

                    Variante actualizada = varianteRepository.save(varianteExistente);

                    return ResponseEntity.ok(actualizada);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {

        if (!varianteRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        varianteRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}

