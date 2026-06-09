package com.chiokore.asistencianomina.controllers;
import com.chiokore.asistencianomina.domain.entities.Asistencia;
import com.chiokore.asistencianomina.services.AsistenciaService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/asistencias")
@RequiredArgsConstructor
public class AsistenciaController {
    private final AsistenciaService service;

    @PostMapping("/registrar/{empleadoId}")
    public Asistencia registrar(@PathVariable Long empleadoId) {
        return service.registrarToggle(empleadoId);
    }
}