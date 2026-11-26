package com.m19collection.api.service;

import com.m19collection.api.entity.Coleccion;
import com.m19collection.api.entity.EstadoColeccion;
import com.m19collection.api.entity.Modelo;
import com.m19collection.api.entity.Usuario;
import com.m19collection.api.entity.Variante;
import com.m19collection.api.exception.BusinessRuleException;
import com.m19collection.api.exception.NotFoundException;
import com.m19collection.api.repository.ColeccionRepository;
import com.m19collection.api.repository.UsuarioRepository;
import com.m19collection.api.repository.VarianteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ColeccionServiceTest {

    private static final Long USER_ID = 1L;
    private static final Long VARIANT_ID = 10L;

    @Mock
    private ColeccionRepository coleccionRepository;
    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private VarianteRepository varianteRepository;

    @InjectMocks
    private ColeccionService service;

    private Usuario usuario;
    private Variante variante;

    @BeforeEach
    void setUp() {
        usuario = new Usuario("Kevin", "kevin@m19collection.com", "hash");
        variante = new Variante("Rojo", "Basico", "desc", "img.jpg",
                new Modelo("Twin Mill III", 2024, "desc"));
    }

    private void givenExisting(Coleccion item) {
        when(coleccionRepository.findByUsuario_IdAndVariante_Id(USER_ID, VARIANT_ID))
                .thenReturn(Optional.of(item));
    }

    private void givenNoExisting() {
        when(coleccionRepository.findByUsuario_IdAndVariante_Id(USER_ID, VARIANT_ID))
                .thenReturn(Optional.empty());
    }

    private void givenSaveReturnsArgument() {
        when(coleccionRepository.save(any(Coleccion.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void addToCollection_createsNewItemWithDefaultQuantity() {
        givenNoExisting();
        when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.of(usuario));
        when(varianteRepository.findById(VARIANT_ID)).thenReturn(Optional.of(variante));
        givenSaveReturnsArgument();

        Coleccion result = service.addToCollection(USER_ID, VARIANT_ID, null);

        assertThat(result.getEstado()).isEqualTo(EstadoColeccion.COLECCION);
        assertThat(result.getCantidad()).isEqualTo(1);
    }

    @Test
    void addToCollection_incrementsQuantityWhenAlreadyInCollection() {
        givenExisting(new Coleccion(usuario, variante, EstadoColeccion.COLECCION, 2));
        givenSaveReturnsArgument();

        Coleccion result = service.addToCollection(USER_ID, VARIANT_ID, 3);

        assertThat(result.getCantidad()).isEqualTo(5);
        verify(usuarioRepository, never()).findById(any());
    }

    @Test
    void addToCollection_movesWishlistItemToCollection() {
        givenExisting(new Coleccion(usuario, variante, EstadoColeccion.WISHLIST, 1));
        givenSaveReturnsArgument();

        Coleccion result = service.addToCollection(USER_ID, VARIANT_ID, 2);

        assertThat(result.getEstado()).isEqualTo(EstadoColeccion.COLECCION);
        assertThat(result.getCantidad()).isEqualTo(2);
    }

    @Test
    void addToCollection_rejectsQuantityBelowOne() {
        assertThatThrownBy(() -> service.addToCollection(USER_ID, VARIANT_ID, 0))
                .isInstanceOf(IllegalArgumentException.class);
        verifyNoInteractions(coleccionRepository);
    }

    @Test
    void addToCollection_requiresVarianteId() {
        assertThatThrownBy(() -> service.addToCollection(USER_ID, null, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addToCollection_failsWhenUsuarioDoesNotExist() {
        givenNoExisting();
        when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addToCollection(USER_ID, VARIANT_ID, 1))
                .isInstanceOf(NotFoundException.class);
        verify(coleccionRepository, never()).save(any());
    }

    @Test
    void addToWishlist_createsWishlistItem() {
        givenNoExisting();
        when(usuarioRepository.findById(USER_ID)).thenReturn(Optional.of(usuario));
        when(varianteRepository.findById(VARIANT_ID)).thenReturn(Optional.of(variante));
        givenSaveReturnsArgument();

        Coleccion result = service.addToWishlist(USER_ID, VARIANT_ID);

        assertThat(result.getEstado()).isEqualTo(EstadoColeccion.WISHLIST);
        assertThat(result.getCantidad()).isEqualTo(1);
    }

    @Test
    void addToWishlist_isRejectedWhenAlreadyInCollection() {
        givenExisting(new Coleccion(usuario, variante, EstadoColeccion.COLECCION, 3));

        assertThatThrownBy(() -> service.addToWishlist(USER_ID, VARIANT_ID))
                .isInstanceOf(BusinessRuleException.class);
        verify(coleccionRepository, never()).save(any());
    }

    @Test
    void addToWishlist_doesNothingWhenAlreadyInWishlist() {
        Coleccion existing = new Coleccion(usuario, variante, EstadoColeccion.WISHLIST, 1);
        givenExisting(existing);

        Coleccion result = service.addToWishlist(USER_ID, VARIANT_ID);

        assertThat(result).isSameAs(existing);
        verify(coleccionRepository, never()).save(any());
    }

    @Test
    void updateQuantity_setsExactQuantity() {
        givenExisting(new Coleccion(usuario, variante, EstadoColeccion.COLECCION, 5));
        givenSaveReturnsArgument();

        Coleccion result = service.updateQuantity(USER_ID, VARIANT_ID, 2);

        assertThat(result.getCantidad()).isEqualTo(2);
    }

    @Test
    void updateQuantity_failsForWishlistItem() {
        givenExisting(new Coleccion(usuario, variante, EstadoColeccion.WISHLIST, 1));

        assertThatThrownBy(() -> service.updateQuantity(USER_ID, VARIANT_ID, 2))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void updateQuantity_rejectsInvalidQuantity() {
        assertThatThrownBy(() -> service.updateQuantity(USER_ID, VARIANT_ID, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void remove_deletesTheItem() {
        Coleccion existing = new Coleccion(usuario, variante, EstadoColeccion.COLECCION, 1);
        givenExisting(existing);

        service.remove(USER_ID, VARIANT_ID, EstadoColeccion.COLECCION);

        verify(coleccionRepository).delete(existing);
    }

    @Test
    void remove_cannotTouchAnotherUsersItem() {
        Long otherUserId = 2L;
        when(coleccionRepository.findByUsuario_IdAndVariante_Id(otherUserId, VARIANT_ID))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.remove(otherUserId, VARIANT_ID, EstadoColeccion.COLECCION))
                .isInstanceOf(NotFoundException.class);
        verify(coleccionRepository, never()).delete(any());
    }
}