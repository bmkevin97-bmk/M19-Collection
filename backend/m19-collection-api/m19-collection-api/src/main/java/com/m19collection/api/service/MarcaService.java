package com.m19collection.api.service;

import com.m19collection.api.entity.Marca;
import com.m19collection.api.repository.MarcaRepository;
import org.springframework.stereotype.Service;

/* Permite trabajar con listas de objetos */
import java.util.List;

@Service
public class MarcaService {

    /*"final" solo se puede utilizar una vez, no podrá apuntar a otro repositorio*/
  private final MarcaRepository marcaRepository;

  public MarcaService(MarcaRepository marcaRepository){
      this.marcaRepository = marcaRepository;
  }

  public List<Marca> obtenerTodas(){
      return marcaRepository.findAll();
  }

  public Marca obtenerPorId(Long id) {
      return marcaRepository.findById(id).orElse(null);
  }

  public Marca crear(Marca marca) {
      if (marcaRepository.existsByNombreIgnoreCase(marca.getNombre())){
          throw new IllegalArgumentException(
                  "Ya existe una marca con este nombre."
          );
      }
      return marcaRepository.save(marca);
  }
}
