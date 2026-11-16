package com.m19collection.api.service;

import com.m19collection.api.entity.Modelo;
import com.m19collection.api.entity.Serie;
import com.m19collection.api.repository.ModeloRepository;
import com.m19collection.api.repository.SerieRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ModeloService {
    private final ModeloRepository modeloRepository;
    private final SerieRepository serieRepository;

    public ModeloService(ModeloRepository modeloRepository, SerieRepository serieRepository){
        this.modeloRepository = modeloRepository;
        this.serieRepository = serieRepository;
    }

    @Transactional(readOnly = true)
    public List<Serie> obtenerSeries(Long modeloId){
        Modelo modelo = buscarModelo(modeloId);
        return new ArrayList<>(modelo.getSeries());
    }

    @Transactional
    public List<Serie> asignarSeries(Long modeloId, Set<Long> serieIds){
        if (serieIds == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "serieIds is required");
        }
        Modelo modelo = buscarModelo(modeloId);
        List<Serie> series = serieRepository.findAllById(serieIds);

        if (series.size() !=serieIds.size()){
            Set<Long> found = series.stream().map(Serie::getId).collect(Collectors.toSet());
            Set<Long> missing = new HashSet<>(serieIds);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Series not found: " + missing);
        }
        modelo.setSeries(new HashSet<>(series));
        modeloRepository.save(modelo);
        return new ArrayList<>(modelo.getSeries());
    }
    private Modelo buscarModelo(Long id){
        return modeloRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Modelo not found: " + id));
    }
}
