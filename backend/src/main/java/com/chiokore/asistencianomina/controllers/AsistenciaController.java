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
    private final com.chiokore.asistencianomina.services.BiometricService biometricService;

    @PostMapping("/registrar/{empleadoId}")
    public Asistencia registrar(@PathVariable Long empleadoId) {
        return service.registrarToggle(empleadoId);
    }

    @GetMapping
    public java.util.List<Asistencia> getAllAsistencias(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate
    ) {
        return service.getAllAsistencias(startDate, endDate);
    }

    @GetMapping("/empleado/{empleadoId}")
    public java.util.List<Asistencia> getAsistenciasByEmpleado(@PathVariable Long empleadoId) {
        return service.getAsistenciasByEmpleado(empleadoId);
    }

    @GetMapping("/hoy/{empleadoId}")
    public org.springframework.http.ResponseEntity<Asistencia> getAsistenciaHoy(@PathVariable Long empleadoId) {
        Asistencia asistencia = service.getAsistenciaHoy(empleadoId);
        if (asistencia == null) {
            return org.springframework.http.ResponseEntity.notFound().build();
        }
        return org.springframework.http.ResponseEntity.ok(asistencia);
    }

    @PostMapping("/fingerprint")
    public Asistencia registrarByFingerprint(@RequestBody java.util.Map<String, String> payload) {
        String base64Image = payload.get("imagenB64");
        if (base64Image == null || base64Image.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Imagen no proporcionada");
        }
        com.chiokore.asistencianomina.domain.entities.Empleado empleado = biometricService.identifyEmployee(base64Image)
                .orElseThrow(() -> new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.NOT_FOUND, "Huella no reconocida o empleado inactivo"));
        
        return service.registrarToggle(empleado.getId());
    }
}