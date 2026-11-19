package com.m19collection.api.service;

import com.m19collection.api.dto.VarianteRequest;
import com.m19collection.api.entity.Modelo;
import com.m19collection.api.entity.Variante;
import com.m19collection.api.exception.NotFoundException;
import com.m19collection.api.repository.ModeloRepository;
import com.m19collection.api.repository.VarianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VarianteService {

    private final VarianteRepository varianteRepository;
    private final ModeloRepository modeloRepository;

    public VarianteService(VarianteRepository varianteRepository,
                           ModeloRepository modeloRepository) {
        this.varianteRepository = varianteRepository;
        this.modeloRepository = modeloRepository;
    }

    @Transactional(readOnly = true)
    public List<Variante> findAll() {
        return varianteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Variante findById(Long id) {
        return varianteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Variante not found: " + id));
    }

    @Transactional
    public Variante create(VarianteRequest request) {
        Modelo modelo = findModelo(request.modeloId());
        return varianteRepository.save(new Variante(
                request.color(), request.edicion(),
                request.descripcion(), request.imagen(), modelo));
    }

    @Transactional
    public Variante update(Long id, VarianteRequest request) {
        Variante variante = findById(id);
        variante.setColor(request.color());
        variante.setEdicion(request.edicion());
        variante.setDescripcion(request.descripcion());
        variante.setImagen(request.imagen());
        variante.setModelo(findModelo(request.modeloId()));
        return varianteRepository.save(variante);
    }

    @Transactional
    public void delete(Long id) {
        if (!varianteRepository.existsById(id)) {
            throw new NotFoundException("Variante not found: " + id);
        }
        varianteRepository.deleteById(id);
    }

    private Modelo findModelo(Long id) {
        return modeloRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Modelo not found: " + id));
    }
}
