package com.m19collection.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ModeloRequest(
        @NotBlank @Size(max = 150) String nombre,
        @NotNull @Min(1950) @Max(2100) Integer anio,
        @Size(max = 255) String descripcion
) {

}