package com.chiokore.asistencianomina.controllers;

import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.domain.entities.Rol;
import com.chiokore.asistencianomina.domain.entities.TipoContrato;
import com.chiokore.asistencianomina.dto.EmpleadoRequest;
import com.chiokore.asistencianomina.services.EmpleadoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private EmpleadoService service;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new EmpleadoController(service)).build();
    }

    private Rol crearRol() {
        Rol r = new Rol();
        r.setId(1L);
        r.setNombre("TRABAJADOR");
        return r;
    }

    private TipoContrato crearTipoContrato() {
        TipoContrato tc = new TipoContrato();
        tc.setId(1L);
        tc.setNombre("PAGADO");
        return tc;
    }

    @Test
    void getAll_debeRetornarPaginaVacia() throws Exception {
        when(service.obtenerActivosPaginados(any())).thenReturn(Page.empty());

        mockMvc.perform(get("/api/empleados"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));
    }

    @Test
    void create_debeRetornarEmpleadoCreado() throws Exception {
        Empleado saved = new Empleado();
        saved.setId(1L);
        saved.setNombre("Nuevo Empleado");
        saved.setTarifaHora(50.0);
        saved.setActivo(true);
        saved.setRol(crearRol());
        saved.setTipoContrato(crearTipoContrato());

        when(service.crear(any(EmpleadoRequest.class))).thenReturn(saved);

        String json = "{\"nombre\":\"Nuevo Empleado\",\"tarifaHora\":50.0,\"rolId\":1,\"tipoContratoId\":1}";

        mockMvc.perform(post("/api/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Nuevo Empleado"));
    }

    @Test
    void update_debeActualizarYRetornarEmpleado() throws Exception {
        Empleado updated = new Empleado();
        updated.setId(1L);
        updated.setNombre("Empleado Editado");
        updated.setTarifaHora(60.0);
        updated.setActivo(true);
        updated.setRol(crearRol());
        updated.setTipoContrato(crearTipoContrato());

        when(service.actualizar(eq(1L), any(EmpleadoRequest.class))).thenReturn(updated);

        String json = "{\"nombre\":\"Empleado Editado\",\"tarifaHora\":60.0,\"rolId\":1,\"tipoContratoId\":1}";

        mockMvc.perform(put("/api/empleados/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Empleado Editado"));
    }

    @Test
    void delete_debeSoftDeleteYRetornar200() throws Exception {
        doNothing().when(service).softDelete(1L);

        mockMvc.perform(delete("/api/empleados/1"))
                .andExpect(status().isOk());

        verify(service, times(1)).softDelete(1L);
    }
}
