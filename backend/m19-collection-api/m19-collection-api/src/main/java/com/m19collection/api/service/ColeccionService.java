package com.m19collection.api.service;

import com.m19collection.api.entity.Coleccion;
import com.m19collection.api.entity.EstadoColeccion;
import com.m19collection.api.entity.Usuario;
import com.m19collection.api.entity.Variante;
import com.m19collection.api.exception.BusinessRuleException;
import com.m19collection.api.exception.NotFoundException;
import com.m19collection.api.repository.ColeccionRepository;
import com.m19collection.api.repository.UsuarioRepository;
import com.m19collection.api.repository.VarianteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ColeccionService {

    private final ColeccionRepository coleccionRepository;
    private final UsuarioRepository usuarioRepository;
    private final VarianteRepository varianteRepository;

    public ColeccionService(ColeccionRepository coleccionRepository,
                            UsuarioRepository usuarioRepository,
                            VarianteRepository varianteRepository) {
        this.coleccionRepository = coleccionRepository;
        this.usuarioRepository = usuarioRepository;
        this.varianteRepository = varianteRepository;
    }

    public List<Coleccion> obtenerTodas() {
        return coleccionRepository.findAll();
    }

    public Coleccion obtenerPorId(Long id) {
        return coleccionRepository.findById(id).orElse(null);
    }

    public boolean eliminar(Long id) {
        if (!coleccionRepository.existsById(id)) {
            return false;
        }
        coleccionRepository.deleteById(id);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Coleccion> findByEstado(Long usuarioId, EstadoColeccion estado) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new NotFoundException("Usuario not found: " + usuarioId);
        }
        return coleccionRepository.findByUsuario_IdAndEstado(usuarioId, estado);
    }

    @Transactional
    public Coleccion addToCollection(Long usuarioId, Long varianteId, Integer cantidad) {
        requireVarianteId(varianteId);
        int qty = resolveQuantity(cantidad);

        Optional<Coleccion> existing =
                coleccionRepository.findByUsuario_IdAndVariante_Id(usuarioId, varianteId);

        if (existing.isPresent()) {
            Coleccion item = existing.get();
            if (item.getEstado() == EstadoColeccion.WISHLIST) {
                item.setEstado(EstadoColeccion.COLECCION);
                item.setCantidad(qty);
            } else {
                item.setCantidad(item.getCantidad() + qty);
            }
            return coleccionRepository.save(item);
        }

        Usuario usuario = findUsuario(usuarioId);
        Variante variante = findVariante(varianteId);
        return coleccionRepository.save(
                new Coleccion(usuario, variante, EstadoColeccion.COLECCION, qty));
    }

    @Transactional
    public Coleccion addToWishlist(Long usuarioId, Long varianteId) {
        requireVarianteId(varianteId);

        Optional<Coleccion> existing =
                coleccionRepository.findByUsuario_IdAndVariante_Id(usuarioId, varianteId);

        if (existing.isPresent()) {
            Coleccion item = existing.get();
            if (item.getEstado() == EstadoColeccion.COLECCION) {
                throw new BusinessRuleException("Variant is already in the collection");
            }
            return item;
        }

        Usuario usuario = findUsuario(usuarioId);
        Variante variante = findVariante(varianteId);
        return coleccionRepository.save(
                new Coleccion(usuario, variante, EstadoColeccion.WISHLIST, 1));
    }

    private void requireVarianteId(Long varianteId) {
        if (varianteId == null) {
            throw new IllegalArgumentException("varianteId is required");
        }
    }

    private int resolveQuantity(Integer cantidad) {
        int qty = (cantidad == null) ? 1 : cantidad;
        if (qty < 1) {
            throw new IllegalArgumentException("cantidad must be at least 1");
        }
        return qty;
    }

    private Usuario findUsuario(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Usuario not found: " + id));
    }

    private Variante findVariante(Long id) {
        return varianteRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Variante not found: " + id));
    }

    @Transactional
    public Coleccion updateQuantity(Long usuarioId, Long varianteId, Integer cantidad) {
        if (cantidad == null || cantidad < 1) {
            throw new IllegalArgumentException("cantidad must be at least 1");
        }
        Coleccion item = findItem(usuarioId, varianteId, EstadoColeccion.COLECCION);
        item.setCantidad(cantidad);
        return coleccionRepository.save(item);
    }

    @Transactional
    public void remove(Long usuarioId, Long varianteId, EstadoColeccion estado) {
        Coleccion item = findItem(usuarioId, varianteId, estado);
        coleccionRepository.delete(item);
    }

    private Coleccion findItem(Long usuarioId, Long varianteId, EstadoColeccion estado) {
        return coleccionRepository.findByUsuario_IdAndVariante_Id(usuarioId, varianteId)
                .filter(item -> item.getEstado() == estado)
                .orElseThrow(() -> new NotFoundException(
                        "Variante " + varianteId + " not found in " + estado + " of usuario " + usuarioId));
    }

}
