package com.m19collection.api.dto;

import com.m19collection.api.entity.Modelo;

public record ModeloResponse(Long id, String nombre, Integer anio, String descripcion) {

    public static ModeloResponse from(Modelo m) {
        return new ModeloResponse(m.getId(), m.getNombre(), m.getAnio(), m.getDescripcion());
    }
}