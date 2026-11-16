package com.m19collection.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "variantes")
public class Variante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    private String edicion;

    private String descripcion;

    private String imagen;

    @ManyToOne
    @JoinColumn(name = "modelo_id", nullable = false)
    private Modelo modelo;

    public Variante() {
    }

    public Variante(String color, String edicion, String descripcion, String imagen, Modelo modelo) {
        this.color = color;
        this.edicion = edicion;
        this.descripcion = descripcion;
        this.imagen = imagen;
        this.modelo = modelo;
    }

    public Long getId() {
        return id;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEdicion() {
        return edicion;
    }

    public void setEdicion(String edicion) {
        this.edicion = edicion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Modelo getModelo() {
        return modelo;
    }

    public void setModelo(Modelo modelo) {
        this.modelo = modelo;
    }
}