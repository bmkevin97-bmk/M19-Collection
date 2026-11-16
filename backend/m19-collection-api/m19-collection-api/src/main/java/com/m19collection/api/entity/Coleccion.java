package com.m19collection.api.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "colecciones",
        uniqueConstraints = @UniqueConstraint(columnNames = {"usuario_id", "variante_id"}))
public class Coleccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "variante_id", nullable = false)
    private Variante variante;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoColeccion estado;

    @Column(nullable = false)
    private Integer cantidad;

    public Coleccion() {
    }

    public Coleccion(Usuario usuario, Variante variante, EstadoColeccion estado, Integer cantidad) {
        this.usuario = usuario;
        this.variante = variante;
        this.estado = estado;
        this.cantidad = cantidad;
    }

    public Long getId() { return id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Variante getVariante() { return variante; }
    public void setVariante(Variante variante) { this.variante = variante; }

    public EstadoColeccion getEstado() { return estado; }
    public void setEstado(EstadoColeccion estado) {
        this.estado = estado;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}

