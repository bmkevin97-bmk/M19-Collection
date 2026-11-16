package com.m19collection.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "name")
public class Marca {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre){
        this.nombre = nombre;
    }
}