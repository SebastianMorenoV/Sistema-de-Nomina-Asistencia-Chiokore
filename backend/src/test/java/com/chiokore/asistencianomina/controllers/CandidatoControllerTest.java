package com.chiokore.asistencianomina.controllers;

import com.chiokore.asistencianomina.domain.entities.Candidato;
import com.chiokore.asistencianomina.domain.entities.EstadoCandidato;
import com.chiokore.asistencianomina.services.CandidatoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CandidatoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CandidatoService service;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CandidatoController(service)).build();
    }

    @Test
    void getAll_debeRetornarListaVacia() throws Exception {
        when(service.obtenerTodosPaginados(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/candidatos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void getById_existente_debeRetornarCandidato() throws Exception {
        EstadoCandidato espera = new EstadoCandidato();
        espera.setId(1L);
        espera.setNombre("ESPERA");

        Candidato c = new Candidato();
        c.setId(1L);
        c.setNombre("Ana Gómez");
        c.setTelefonoContacto("555-2002");
        c.setFechaSolicitud(LocalDate.of(2026, 1, 15));
        c.setEstado(espera);

        when(service.obtenerPorId(1L)).thenReturn(c);

        mockMvc.perform(get("/api/candidatos/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Ana Gómez"))
                .andExpect(jsonPath("$.telefonoContacto").value("555-2002"))
                .andExpect(jsonPath("$.estado.nombre").value("ESPERA"));
    }

    @Test
    void getById_noExistente_debeRetornar404() throws Exception {
        when(service.obtenerPorId(99L))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Candidato no encontrado con ID: 99"));

        mockMvc.perform(get("/api/candidatos/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_debeRetornar201() throws Exception {
        Candidato saved = new Candidato();
        saved.setId(1L);
        saved.setNombre("Luis Díaz");
        saved.setTelefonoContacto("555-3003");
        saved.setFechaSolicitud(LocalDate.of(2026, 1, 15));

        when(service.guardar(any(Candidato.class))).thenReturn(saved);

        String json = "{\"nombre\":\"Luis Díaz\",\"telefonoContacto\":\"555-3003\",\"fechaSolicitud\":\"2026-01-15\"}";

        mockMvc.perform(post("/api/candidatos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Luis Díaz"));
    }

    @Test
    void update_existente_debeRetornarCandidatoActualizado() throws Exception {
        EstadoCandidato aceptado = new EstadoCandidato();
        aceptado.setId(2L);
        aceptado.setNombre("ACEPTADO");

        Candidato updated = new Candidato();
        updated.setId(1L);
        updated.setNombre("María Editada");
        updated.setTelefonoContacto("555-9999");
        updated.setFechaSolicitud(LocalDate.of(2026, 1, 15));
        updated.setEstado(aceptado);

        when(service.guardar(any(Candidato.class))).thenReturn(updated);

        String json = "{\"nombre\":\"María Editada\",\"telefonoContacto\":\"555-9999\",\"fechaSolicitud\":\"2026-01-15\",\"estado\":{\"id\":2}}";

        mockMvc.perform(put("/api/candidatos/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("María Editada"));
    }

    @Test
    void delete_existente_debeRetornar204() throws Exception {
        doNothing().when(service).eliminar(1L);

        mockMvc.perform(delete("/api/candidatos/1"))
                .andExpect(status().isNoContent());

        verify(service, times(1)).eliminar(1L);
    }

    @Test
    void delete_noExistente_debeRetornar404() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Candidato no encontrado con ID: 99"))
                .when(service).eliminar(99L);

        mockMvc.perform(delete("/api/candidatos/99"))
                .andExpect(status().isNotFound());
    }
}
