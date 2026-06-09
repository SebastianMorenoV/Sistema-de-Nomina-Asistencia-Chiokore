package com.chiokore.asistencianomina.controllers;

import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.domain.entities.Horario;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import com.chiokore.asistencianomina.repositories.HorarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class HorarioControllerTest {

    private MockMvc mockMvc;

    @Mock
    private HorarioRepository horarioRepository;

    @Mock
    private EmpleadoRepository empleadoRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(
                new HorarioController(horarioRepository, empleadoRepository)).build();
    }

    private Horario crearHorario(Long id, Empleado emp, String dia, LocalTime inicio, LocalTime fin) {
        Horario h = new Horario();
        h.setId(id);
        h.setEmpleado(emp);
        h.setDiaSemana(dia);
        h.setHoraInicio(inicio);
        h.setHoraFin(fin);
        return h;
    }

    @Test
    void getAll_debeRetornarLista() throws Exception {
        Empleado emp = new Empleado();
        emp.setId(1L);
        emp.setNombre("Juan Pérez");

        Horario h = crearHorario(1L, emp, "LUNES",
                LocalTime.of(8, 0), LocalTime.of(17, 0));

        when(horarioRepository.findAll()).thenReturn(List.of(h));

        mockMvc.perform(get("/api/horarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].empleadoNombre").value("Juan Pérez"))
                .andExpect(jsonPath("$[0].diaSemana").value("LUNES"));
    }

    @Test
    void save_debeGuardarYRetornarHorario() throws Exception {
        Empleado emp = new Empleado();
        emp.setId(1L);
        emp.setNombre("Juan Pérez");

        Horario saved = crearHorario(1L, emp, "LUNES",
                LocalTime.of(8, 0), LocalTime.of(17, 0));

        when(horarioRepository.save(any(Horario.class))).thenReturn(saved);

        String json = "{\"empleado\":{\"id\":1},\"diaSemana\":\"LUNES\",\"horaInicio\":\"08:00:00\",\"horaFin\":\"17:00:00\"}";

        mockMvc.perform(post("/api/horarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.diaSemana").value("LUNES"));
    }

    @Test
    void assignHorario_conOldEmpleadoIdYNewEmpleadoId_debeActualizar() throws Exception {
        Empleado empOld = new Empleado();
        empOld.setId(1L);
        empOld.setNombre("Viejo");

        Empleado empNew = new Empleado();
        empNew.setId(2L);
        empNew.setNombre("Nuevo");

        Horario existente = crearHorario(10L, empOld, "LUNES",
                LocalTime.of(8, 0), LocalTime.of(12, 0));

        when(horarioRepository.findAll()).thenReturn(List.of(existente));
        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empNew));

        String json = "{\"diaSemana\":\"LUNES\",\"horaInicio\":\"08\",\"oldEmpleadoId\":1,\"newEmpleadoId\":2}";

        mockMvc.perform(post("/api/horarios/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(horarioRepository, times(1)).save(existente);
        verify(horarioRepository, never()).delete(any());
    }

    @Test
    void assignHorario_conOldEmpleadoIdYNewEmpleadoIdNull_debeEliminar() throws Exception {
        Empleado empOld = new Empleado();
        empOld.setId(1L);
        empOld.setNombre("Viejo");

        Horario existente = crearHorario(10L, empOld, "LUNES",
                LocalTime.of(8, 0), LocalTime.of(12, 0));

        when(horarioRepository.findAll()).thenReturn(List.of(existente));

        String json = "{\"diaSemana\":\"LUNES\",\"horaInicio\":\"08\",\"oldEmpleadoId\":1}";

        mockMvc.perform(post("/api/horarios/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(horarioRepository, times(1)).delete(existente);
        verify(horarioRepository, never()).save(any());
    }

    @Test
    void assignHorario_sinOldEmpleadoIdYConNewEmpleadoId_debeCrearNuevo() throws Exception {
        Empleado empNew = new Empleado();
        empNew.setId(2L);
        empNew.setNombre("Nuevo");

        when(empleadoRepository.findById(2L)).thenReturn(Optional.of(empNew));
        when(horarioRepository.save(any(Horario.class))).thenAnswer(inv -> inv.getArgument(0));

        String json = "{\"diaSemana\":\"MARTES\",\"horaInicio\":\"09\",\"newEmpleadoId\":2}";

        mockMvc.perform(post("/api/horarios/assign")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk());

        verify(horarioRepository, times(1)).save(any(Horario.class));
    }
}
