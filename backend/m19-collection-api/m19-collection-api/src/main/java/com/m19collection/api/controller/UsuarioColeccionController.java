package com.m19collection.api.controller;

import com.m19collection.api.dto.AddVariantRequest;
import com.m19collection.api.dto.ColeccionResponse;
import com.m19collection.api.entity.EstadoColeccion;
import com.m19collection.api.service.ColeccionService;
import com.m19collection.api.dto.UpdateQuantityRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}")
public class UsuarioColeccionController {

    private final ColeccionService coleccionService;

    public UsuarioColeccionController(ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
    }

    @GetMapping("/coleccion")
    public List<ColeccionResponse> getCollection(@PathVariable Long usuarioId) {
        return coleccionService.findByEstado(usuarioId, EstadoColeccion.COLECCION)
                .stream().map(ColeccionResponse::from).toList();
    }

    @GetMapping("/wishlist")
    public List<ColeccionResponse> getWishlist(@PathVariable Long usuarioId) {
        return coleccionService.findByEstado(usuarioId, EstadoColeccion.WISHLIST)
                .stream().map(ColeccionResponse::from).toList();
    }

    @PostMapping("/coleccion")
    public ColeccionResponse addToCollection(@PathVariable Long usuarioId,
                                            @Valid @RequestBody AddVariantRequest request) {
        return ColeccionResponse.from(
                coleccionService.addToCollection(usuarioId, request.varianteId(), request.cantidad()));
    }

    @PostMapping("/wishlist")
    public ColeccionResponse addToWishlist(@PathVariable Long usuarioId,
                                         @Valid @RequestBody AddVariantRequest request) {
        return ColeccionResponse.from(
                coleccionService.addToWishlist(usuarioId, request.varianteId()));
    }

    @PutMapping("/coleccion/{varianteId}")
    public ColeccionResponse updateQuantity(@PathVariable Long usuarioId,
                                            @PathVariable Long varianteId,
                                           @Valid @RequestBody UpdateQuantityRequest request) {
        return ColeccionResponse.from(
                coleccionService.updateQuantity(usuarioId, varianteId, request.cantidad()));
    }

    @DeleteMapping("/coleccion/{varianteId}")
    public ResponseEntity<Void> removeFromCollection(@PathVariable Long usuarioId,
                                                     @PathVariable Long varianteId) {
        coleccionService.remove(usuarioId, varianteId, EstadoColeccion.COLECCION);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/wishlist/{varianteId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long usuarioId,
                                                   @PathVariable Long varianteId) {
        coleccionService.remove(usuarioId, varianteId, EstadoColeccion.WISHLIST);
        return ResponseEntity.noContent().build();
    }

}