package com.m19collection.api.repository;

import com.m19collection.api.entity.Marca;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcaRepository extends JpaRepository<Marca, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
}
