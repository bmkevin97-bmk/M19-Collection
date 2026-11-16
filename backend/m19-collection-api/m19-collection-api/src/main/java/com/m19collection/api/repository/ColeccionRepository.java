package com.m19collection.api.repository;

import com.m19collection.api.entity.Coleccion;
import com.m19collection.api.entity.EstadoColeccion;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.List;
import java.util.Optional;

public interface ColeccionRepository extends JpaRepository<Coleccion, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Coleccion> findByUsuario_IdAndVariante_Id(Long usuarioId, Long varianteId);

    List<Coleccion> findByUsuario_IdAndEstado(Long usuarioId, EstadoColeccion estado);
}