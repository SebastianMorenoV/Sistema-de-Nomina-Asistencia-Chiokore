package com.chiokore.asistencianomina.services;

import com.chiokore.asistencianomina.domain.entities.Asistencia;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.repositories.AsistenciaRepository;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaServiceTest {

    @Mock
    private AsistenciaRepository repository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @InjectMocks
    private AsistenciaService service;

    private Empleado crearEmpleado() {
        Empleado e = new Empleado();
        e.setId(1L);
        e.setNombre("Juan Pérez");
        return e;
    }

    @Test
    void registrarToggle_cuandoNoExisteAsistenciaHoy_debeCrearNuevaConEntrada() {
        Empleado empleado = crearEmpleado();
        when(empleadoRepository.findById(1L)).thenReturn(Optional.of(empleado));
        when(repository.findByEmpleadoIdAndFecha(eq(1L), any(LocalDate.class))).thenReturn(List.of());

        Asistencia saved = new Asistencia();
        saved.setId(100L);
        saved.setEmpleado(empleado);
        saved.setEntrada(LocalDateTime.now());
        when(repository.save(any(Asistencia.class))).thenReturn(saved);

        Asistencia resultado = service.registrarToggle(1L);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getId());
        assertEquals(empleado, resultado.getEmpleado());
        assertNotNull(resultado.getEntrada());
        assertNull(resultado.getSalida());
        verify(repository, times(1)).save(any(Asistencia.class));
    }

    @Test
    void registrarToggle_cuandoExisteAsistenciaSinSalida_debeEstablecerSalida() {
        Empleado empleado = crearEmpleado();
        Asistencia existente = new Asistencia();
        existente.setId(50L);
        existente.setEmpleado(empleado);
        existente.setEntrada(LocalDateTime.of(2026, 6, 6, 8, 0));
        existente.setSalida(null);
        existente.setFecha(LocalDate.of(2026, 6, 6));

        when(repository.findByEmpleadoIdAndFecha(eq(1L), any(LocalDate.class)))
                .thenReturn(List.of(existente));

        Asistencia saved = new Asistencia();
        saved.setId(50L);
        saved.setEmpleado(empleado);
        saved.setEntrada(LocalDateTime.of(2026, 6, 6, 8, 0));
        saved.setSalida(LocalDateTime.now());
        when(repository.save(any(Asistencia.class))).thenReturn(saved);

        Asistencia resultado = service.registrarToggle(1L);

        assertNotNull(resultado);
        assertEquals(50L, resultado.getId());
        assertNotNull(resultado.getSalida());
    }

    @Test
    void registrarToggle_cuandoAsistenciaYaTieneEntradaYSalida_debeLanzarResponseStatusException() {
        Empleado empleado = crearEmpleado();
        Asistencia existente = new Asistencia();
        existente.setId(50L);
        existente.setEmpleado(empleado);
        existente.setEntrada(LocalDateTime.of(2026, 6, 6, 8, 0));
        existente.setSalida(LocalDateTime.of(2026, 6, 6, 17, 0));
        existente.setFecha(LocalDate.of(2026, 6, 6));

        when(repository.findByEmpleadoIdAndFecha(eq(1L), any(LocalDate.class)))
                .thenReturn(List.of(existente));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.registrarToggle(1L));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
        assertEquals("El empleado ya completó su turno de hoy.", ex.getReason());
        verify(repository, never()).save(any());
    }

    @Test
    void registrarToggle_cuandoEmpleadoNoExiste_debeLanzarNoSuchElementException() {
        when(repository.findByEmpleadoIdAndFecha(eq(99L), any(LocalDate.class))).thenReturn(List.of());
        when(empleadoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(java.util.NoSuchElementException.class,
                () -> service.registrarToggle(99L));
        verify(repository, never()).save(any());
    }
}
