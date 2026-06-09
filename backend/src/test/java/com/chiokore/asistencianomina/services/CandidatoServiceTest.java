package com.chiokore.asistencianomina.services;

import com.chiokore.asistencianomina.domain.entities.Candidato;
import com.chiokore.asistencianomina.domain.entities.EstadoCandidato;
import com.chiokore.asistencianomina.repositories.CandidatoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CandidatoServiceTest {

    @Mock
    private CandidatoRepository repository;

    @InjectMocks
    private CandidatoService service;

    private EstadoCandidato crearEstado(Long id, String nombre) {
        EstadoCandidato e = new EstadoCandidato();
        e.setId(id);
        e.setNombre(nombre);
        return e;
    }

    private Candidato crearCandidato(Long id, String nombre, String telefono, EstadoCandidato estado) {
        Candidato c = new Candidato();
        c.setId(id);
        c.setNombre(nombre);
        c.setTelefonoContacto(telefono);
        c.setFechaSolicitud(LocalDate.of(2026, 1, 15));
        c.setEstado(estado);
        return c;
    }

    @Test
    void obtenerTodos_debeRetornarListaVaciaCuandoNoHayCandidatos() {
        when(repository.findAll()).thenReturn(List.of());
        List<Candidato> resultado = service.obtenerTodos();
        assertTrue(resultado.isEmpty());
        verify(repository, times(1)).findAll();
    }

    @Test
    void obtenerTodos_debeRetornarListaConCandidatos() {
        EstadoCandidato espera = crearEstado(1L, "ESPERA");
        Candidato c1 = crearCandidato(1L, "Juan Pérez", "555-1001", espera);
        Candidato c2 = crearCandidato(2L, "Ana Gómez", "555-2002", espera);

        when(repository.findAll()).thenReturn(List.of(c1, c2));

        List<Candidato> resultado = service.obtenerTodos();
        assertEquals(2, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        assertEquals("Ana Gómez", resultado.get(1).getNombre());
    }

    @Test
    void obtenerPorId_cuandoExiste_debeRetornarCandidato() {
        EstadoCandidato espera = crearEstado(1L, "ESPERA");
        Candidato c = crearCandidato(1L, "Juan Pérez", "555-1001", espera);

        when(repository.findById(1L)).thenReturn(Optional.of(c));

        Candidato resultado = service.obtenerPorId(1L);
        assertNotNull(resultado);
        assertEquals("Juan Pérez", resultado.getNombre());
        assertEquals("ESPERA", resultado.getEstado().getNombre());
    }

    @Test
    void obtenerPorId_cuandoNoExiste_debeLanzarExcepcion() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.obtenerPorId(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("99"));
    }

    @Test
    void guardar_debeRetornarCandidatoConIdAsignado() {
        EstadoCandidato espera = crearEstado(1L, "ESPERA");
        Candidato input = crearCandidato(null, "Nuevo Candidato", "555-3003", espera);
        Candidato saved = crearCandidato(10L, "Nuevo Candidato", "555-3003", espera);

        when(repository.save(any(Candidato.class))).thenReturn(saved);

        Candidato resultado = service.guardar(input);
        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Nuevo Candidato", resultado.getNombre());
        verify(repository, times(1)).save(input);
    }

    @Test
    void eliminar_cuandoExiste_debeEjecutarDelete() {
        when(repository.existsById(1L)).thenReturn(true);
        doNothing().when(repository).deleteById(1L);

        service.eliminar(1L);

        verify(repository, times(1)).existsById(1L);
        verify(repository, times(1)).deleteById(1L);
    }

    @Test
    void eliminar_cuandoNoExiste_debeLanzarExcepcion() {
        when(repository.existsById(99L)).thenReturn(false);

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.eliminar(99L));
        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
        assertTrue(ex.getReason().contains("99"));
        verify(repository, never()).deleteById(any());
    }
}
