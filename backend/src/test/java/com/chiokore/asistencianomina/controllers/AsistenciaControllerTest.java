package com.chiokore.asistencianomina.controllers;

import com.chiokore.asistencianomina.domain.entities.Asistencia;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.services.AsistenciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.chiokore.asistencianomina.services.BiometricService;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AsistenciaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AsistenciaService service;

    @Mock
    private BiometricService biometricService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new AsistenciaController(service, biometricService)).build();
    }

    @Test
    void registrar_debeRetornar200ConAsistencia() throws Exception {
        Empleado empleado = new Empleado();
        empleado.setId(1L);
        empleado.setNombre("Juan Pérez");

        Asistencia asistencia = new Asistencia();
        asistencia.setId(10L);
        asistencia.setEmpleado(empleado);
        asistencia.setEntrada(LocalDateTime.of(2026, 6, 6, 8, 0));

        when(service.registrarToggle(1L)).thenReturn(asistencia);

        mockMvc.perform(post("/api/asistencias/registrar/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.empleado.nombre").value("Juan Pérez"));
    }

    @Test
    void registrar_cuandoServiceLanzaResponseStatusException_debeRetornar400() throws Exception {
        when(service.registrarToggle(1L))
                .thenThrow(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "El empleado ya completó su turno de hoy."));

        mockMvc.perform(post("/api/asistencias/registrar/1"))
                .andExpect(status().isBadRequest());
    }
}
