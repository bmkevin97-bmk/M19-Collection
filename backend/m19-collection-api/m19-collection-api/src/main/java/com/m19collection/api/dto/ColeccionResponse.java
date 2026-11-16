package com.m19collection.api.dto;

import com.m19collection.api.entity.Coleccion;
import com.m19collection.api.entity.EstadoColeccion;

public record ColeccionResponse(
        Long id,
        Long usuarioId,
        Long varianteId,
        String modelo,
        String color,
        String edicion,
        EstadoColeccion estado,
        Integer cantidad
) {
    public static ColeccionResponse from(Coleccion c) {
        return new ColeccionResponse(
                c.getId(),
                c.getUsuario().getId(),
                c.getVariante().getId(),
                c.getVariante().getModelo().getNombre(),
                c.getVariante().getColor(),
                c.getVariante().getEdicion(),
                c.getEstado(),
                c.getCantidad()
        );
    }
}