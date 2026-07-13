package com.chiokore.asistencianomina.controllers;
import com.chiokore.asistencianomina.domain.entities.Asistencia;
import com.chiokore.asistencianomina.dto.HorasEmpleadoDto;
import com.chiokore.asistencianomina.services.AsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {
    private final AsistenciaService service;

    @PostMapping("/registrar/{empleadoId}")
    public Asistencia registrar(@PathVariable Long empleadoId) {
        return service.registrarToggle(empleadoId);
    }

    // GET /api/asistencias/horas?desde=2026-07-01&hasta=2026-07-15
    // Devuelve las horas totales por empleado en el rango. Lo consume el modulo de Nomina.
    @GetMapping("/horas")
    public List<HorasEmpleadoDto> horasPorRango(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {
        return service.obtenerHorasPorRango(desde, hasta);
    }
}