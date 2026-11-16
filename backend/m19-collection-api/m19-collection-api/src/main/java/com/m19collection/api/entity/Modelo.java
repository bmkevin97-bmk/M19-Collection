package com.m19collection.api.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "modelos")
public class Modelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Integer anio;

    private String descripcion;

    public Modelo() {
    }

    public Modelo(String nombre, Integer anio, String descripcion) {
        this.nombre = nombre;
        this.anio = anio;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }


    @ManyToMany
    @JoinTable(
            name = "modelo_series",
            joinColumns = @JoinColumn(name = "modelo_id"),
            inverseJoinColumns = @JoinColumn(name = "serie_id")
    )
    private Set<Serie> series = new HashSet<>();

    public Set<Serie> getSeries() {
        return series;
    }

    public void setSeries(Set<Serie> series) {
        this.series = series;
    }

}
