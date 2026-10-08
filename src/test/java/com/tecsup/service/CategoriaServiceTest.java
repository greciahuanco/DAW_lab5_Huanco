package com.tecsup.service;

import com.tecsup.model.Categoria;
import com.tecsup.repository.CategoriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CategoriaServiceTest {

    @Mock
    private CategoriaRepository repo;

    @InjectMocks
    private CategoriaService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testListarCategorias() {
        when(repo.findAll()).thenReturn(List.of(new Categoria(1L, "Tecnologia")));

        List<Categoria> resultado = service.listar();

        assertEquals(1, resultado.size());
        assertEquals("Tecnologia", resultado.get(0).getNombre());
        verify(repo).findAll();
    }

    @Test
    void testGuardarYActualizarCategoria() {
        Categoria categoria = new Categoria(1L, "Hogar");
        when(repo.save(categoria)).thenReturn(categoria);

        assertSame(categoria, service.guardar(categoria));
        categoria.setNombre("Hogar y jardin");
        assertSame(categoria, service.actualizar(categoria));
        verify(repo, org.mockito.Mockito.times(2)).save(categoria);
    }

    @Test
    void testObtenerCategoriaExistenteEInexistente() {
        Categoria categoria = new Categoria(1L, "Libros");
        when(repo.findById(1L)).thenReturn(Optional.of(categoria));
        when(repo.findById(99L)).thenReturn(Optional.empty());

        assertSame(categoria, service.obtener(1L));
        assertNull(service.obtener(99L));
        verify(repo).findById(1L);
        verify(repo).findById(99L);
    }

    @Test
    void testEliminarCategoria() {
        doNothing().when(repo).deleteById(1L);

        service.eliminar(1L);

        verify(repo).deleteById(1L);
    }
}
