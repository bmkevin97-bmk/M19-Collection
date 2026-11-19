package com.m19collection.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VarianteRequest(
        @NotBlank @Size(max = 100) String color,
        @NotBlank @Size(max = 100) String edicion,
        @Size(max = 255) String descripcion,
        @Size(max = 255) String imagen,
        @NotNull Long modeloId
) {

}