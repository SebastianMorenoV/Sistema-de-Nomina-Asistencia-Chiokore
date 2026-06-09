package com.chiokore.asistencianomina.controllers;

import com.chiokore.asistencianomina.domain.entities.Horario;
import com.chiokore.asistencianomina.dto.HorarioDto;
import com.chiokore.asistencianomina.repositories.HorarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.Data;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/horarios")
@RequiredArgsConstructor
public class HorarioController {

    private final HorarioRepository horarioRepository;
    private final com.chiokore.asistencianomina.repositories.EmpleadoRepository empleadoRepository;

    @GetMapping
    public List<HorarioDto> getAll() {
        return horarioRepository.findAll().stream()
                .map(HorarioDto::fromEntity)
                .collect(Collectors.toList());
    }

    // Endpoint simplificado para guardar un nuevo horario (Para la pre-selección o guardado automático)
    @PostMapping
    public Horario save(@RequestBody Horario horario) {
        return horarioRepository.save(horario);
    }

    @Data
    public static class AssignRequest {
        private String diaSemana;
        private String horaInicio;
        private Long oldEmpleadoId;
        private Long newEmpleadoId;
    }

    @PostMapping("/assign")
    public void assignHorario(@RequestBody AssignRequest req) {
        // Buscar el horario existente que coincida con dia, hora y el oldEmpleadoId (si había uno)
        if (req.getOldEmpleadoId() != null) {
            horarioRepository.findAll().stream()
                .filter(h -> h.getDiaSemana().equals(req.getDiaSemana()) 
                        && h.getHoraInicio() != null && h.getHoraInicio().toString().startsWith(req.getHoraInicio())
                        && h.getEmpleado().getId().equals(req.getOldEmpleadoId()))
                .findFirst()
                .ifPresent(h -> {
                    if (req.getNewEmpleadoId() == null || req.getNewEmpleadoId().toString().isEmpty()) {
                        // Lo borró
                        horarioRepository.delete(h);
                    } else {
                        // Lo actualizó
                        empleadoRepository.findById(req.getNewEmpleadoId()).ifPresent(emp -> {
                            h.setEmpleado(emp);
                            horarioRepository.save(h);
                        });
                    }
                });
        } else if (req.getNewEmpleadoId() != null && !req.getNewEmpleadoId().toString().isEmpty()) {
            // No había uno viejo, y puso uno nuevo (crear nuevo)
            empleadoRepository.findById(req.getNewEmpleadoId()).ifPresent(emp -> {
                Horario h = new Horario();
                h.setEmpleado(emp);
                h.setDiaSemana(req.getDiaSemana());
                try {
                    h.setHoraInicio(java.time.LocalTime.parse(req.getHoraInicio() + ":00"));
                    h.setHoraFin(h.getHoraInicio().plusHours(4)); // Asumir 4 horas, o depender de lógica
                } catch (Exception e) {}
                horarioRepository.save(h);
            });
        }
    }
}
