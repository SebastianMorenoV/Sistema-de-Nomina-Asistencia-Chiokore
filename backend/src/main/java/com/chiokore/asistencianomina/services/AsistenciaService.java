package com.chiokore.asistencianomina.services;
import com.chiokore.asistencianomina.domain.entities.Asistencia;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.repositories.AsistenciaRepository;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsistenciaService {
    private final AsistenciaRepository repository;
    private final EmpleadoRepository empleadoRepository;

    public Asistencia registrarToggle(Long empleadoId) {
        LocalDate hoy = LocalDate.now();
        List<Asistencia> asistenciasHoy = repository.findByEmpleadoIdAndFecha(empleadoId, hoy);
        
        Asistencia asistencia;
        if (asistenciasHoy.isEmpty()) {
            asistencia = new Asistencia();
            Empleado e = empleadoRepository.findById(empleadoId).orElseThrow();
            asistencia.setEmpleado(e);
            asistencia.setFecha(hoy);
            asistencia.setEntrada(LocalDateTime.now());
        } else {
            asistencia = asistenciasHoy.get(0);
            if (asistencia.getSalida() == null) {
                asistencia.setSalida(LocalDateTime.now());
                if (asistencia.getEntrada() != null && asistencia.getSalida() != null) {
                    long diffMinutes = java.time.Duration.between(asistencia.getEntrada(), asistencia.getSalida()).toMinutes();
                    asistencia.setHorasCalculadas(diffMinutes / 60.0);
                }
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El empleado ya completó su turno de hoy.");
            }
        }
        return repository.save(asistencia);
    }
}