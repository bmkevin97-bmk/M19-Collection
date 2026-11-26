package com.m19collection.api.controller;

import com.m19collection.api.dto.AddVariantRequest;
import com.m19collection.api.dto.ColeccionResponse;
import com.m19collection.api.dto.UpdateQuantityRequest;
import com.m19collection.api.entity.EstadoColeccion;
import com.m19collection.api.service.ColeccionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final ColeccionService coleccionService;

    public MeController(ColeccionService coleccionService) {
        this.coleccionService = coleccionService;
    }

    @GetMapping("/coleccion")
    public List<ColeccionResponse> getCollection(@AuthenticationPrincipal Jwt jwt) {
        return coleccionService.findByEstado(userId(jwt), EstadoColeccion.COLECCION)
                .stream().map(ColeccionResponse::from).toList();
    }

    @GetMapping("/wishlist")
    public List<ColeccionResponse> getWishlist(@AuthenticationPrincipal Jwt jwt) {
        return coleccionService.findByEstado(userId(jwt), EstadoColeccion.WISHLIST)
                .stream().map(ColeccionResponse::from).toList();
    }

    @PostMapping("/coleccion")
    public ColeccionResponse addToCollection(@AuthenticationPrincipal Jwt jwt,
                                             @Valid @RequestBody AddVariantRequest request) {
        return ColeccionResponse.from(
                coleccionService.addToCollection(userId(jwt), request.varianteId(), request.cantidad()));
    }

    @PostMapping("/wishlist")
    public ColeccionResponse addToWishlist(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody AddVariantRequest request) {
        return ColeccionResponse.from(
                coleccionService.addToWishlist(userId(jwt), request.varianteId()));
    }

    @PutMapping("/coleccion/{varianteId}")
    public ColeccionResponse updateQuantity(@AuthenticationPrincipal Jwt jwt,
                                            @PathVariable Long varianteId,
                                            @Valid @RequestBody UpdateQuantityRequest request) {
        return ColeccionResponse.from(
                coleccionService.updateQuantity(userId(jwt), varianteId, request.cantidad()));
    }

    @DeleteMapping("/coleccion/{varianteId}")
    public ResponseEntity<Void> removeFromCollection(@AuthenticationPrincipal Jwt jwt,
                                                     @PathVariable Long varianteId) {
        coleccionService.remove(userId(jwt), varianteId, EstadoColeccion.COLECCION);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/wishlist/{varianteId}")
    public ResponseEntity<Void> removeFromWishlist(@AuthenticationPrincipal Jwt jwt,
                                                   @PathVariable Long varianteId) {
        coleccionService.remove(userId(jwt), varianteId, EstadoColeccion.WISHLIST);
        return ResponseEntity.noContent().build();
    }

    private Long userId(Jwt jwt) {
        return Long.parseLong(jwt.getSubject());
    }
}