package com.m19collection.api.dto;

import com.m19collection.api.entity.Variante;

public record VarianteResponse(
        Long id,
        String color,
        String edicion,
        String descripcion,
        String imagen,
        Long modeloId,
        String modelo
) {
    public static VarianteResponse from(Variante v) {
        return new VarianteResponse(
                v.getId(),
                v.getColor(),
                v.getEdicion(),
                v.getDescripcion(),
                v.getImagen(),
                v.getModelo().getId(),
                v.getModelo().getNombre()
        );
    }
}