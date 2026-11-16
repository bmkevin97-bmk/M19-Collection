package com.m19collection.api.service;

import com.m19collection.api.entity.Serie;
import com.m19collection.api.repository.SerieRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SerieService {

    private final SerieRepository serieRepository;

    public SerieService(SerieRepository serieRepository) {
        this.serieRepository = serieRepository;
    }

    public List<Serie> obtenerTodas() {
        return serieRepository.findAll();
    }

    public Serie obtenerPorId(Long id) {
        return serieRepository.findById(id).orElse(null);
    }

    public Serie crear(Serie serie) {
        if (serie.getName() == null || serie.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre de la serie es obligatorio.");
        }

        String name = serie.getName().trim();

        if (serieRepository.existsByNameIgnoreCase(name)) {
            throw new IllegalArgumentException("Ya existe una serie con ese nombre.");
        }

        serie.setName(name);
        return serieRepository.save(serie);
    }
}
