package com.chiokore.asistencianomina.controllers;

import com.chiokore.asistencianomina.domain.entities.Asistencia;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import com.chiokore.asistencianomina.repositories.AsistenciaRepository;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/nominas")
@RequiredArgsConstructor
public class NominaController {

    private final EmpleadoRepository empleadoRepository;
    private final AsistenciaRepository asistenciaRepository;

    @GetMapping("/semanal")
    public List<Map<String, Object>> getNominaSemanal(
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin) {
        
        List<Map<String, Object>> reporte = new ArrayList<>();
        List<Empleado> empleados = empleadoRepository.findByActivoTrue();
        
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        for (Empleado emp : empleados) {
            // Filtrar asistencias por fecha
            List<Asistencia> asistencias = asistenciaRepository.findAll().stream()
                .filter(a -> a.getEmpleado().getId().equals(emp.getId()))
                .filter(a -> {
                    if (a.getFecha() == null) return false;
                    if (fechaInicio != null && a.getFecha().toString().compareTo(fechaInicio) < 0) return false;
                    if (fechaFin != null && a.getFecha().toString().compareTo(fechaFin) > 0) return false;
                    return true;
                })
                .toList();

            Map<String, Object> fila = new HashMap<>();
            fila.put("emp", emp.getNombre());
            
            // Días mock para llenar la estructura del grid en React
            String[] dias = {"lu", "ma", "mi", "ju", "vi", "sa"};
            for (String dia : dias) {
                Map<String, String> horario = new HashMap<>();
                horario.put("e", "");
                horario.put("s", "");
                fila.put(dia, horario);
            }

            double horasTotales = 0.0;
            
            // Asignar asistencias al día correspondiente (lunes=1, ..., sabado=6)
            for (Asistencia a : asistencias) {
                if (a.getFecha() == null) continue;
                int dayOfWeek = a.getFecha().getDayOfWeek().getValue();
                if (dayOfWeek >= 1 && dayOfWeek <= 6) {
                    String diaKey = dias[dayOfWeek - 1];
                    Map<String, String> d = (Map<String, String>) fila.get(diaKey);
                    
                    if (a.getEntrada() != null) {
                        d.put("e", a.getEntrada().format(timeFormatter));
                    }
                    if (a.getSalida() != null) {
                        d.put("s", a.getSalida().format(timeFormatter));
                    }
                    if (a.getHorasCalculadas() != null) {
                        horasTotales += a.getHorasCalculadas();
                    } else if (a.getEntrada() != null && a.getSalida() != null) {
                        long diff = java.time.Duration.between(a.getEntrada(), a.getSalida()).toMinutes();
                        horasTotales += (diff / 60.0);
                    }
                }
            }
            
            fila.put("horas", String.format("%.1f hrs", horasTotales));
            reporte.add(fila);
        }
        
        return reporte;
    }
}
