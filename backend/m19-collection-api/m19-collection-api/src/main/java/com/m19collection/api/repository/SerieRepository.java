package com.m19collection.api.repository;

import com.m19collection.api.entity.Serie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SerieRepository extends JpaRepository<Serie, Long> {
    boolean existsByNameIgnoreCase(String name);
}
