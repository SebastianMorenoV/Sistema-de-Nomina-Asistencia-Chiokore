package com.chiokore.asistencianomina.services;
import com.chiokore.asistencianomina.domain.entities.Asistencia;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.repositories.AsistenciaRepository;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AsistenciaService {
    private final AsistenciaRepository repository;
    private final EmpleadoRepository empleadoRepository;

    @Transactional
    public Asistencia registrarToggle(Long empleadoId) {
        return registrarMovimiento(empleadoId, null);
    }

    public Asistencia getAsistenciaHoy(Long empleadoId) {
        LocalDate hoy = LocalDate.now();
        List<Asistencia> asistenciasHoy = repository.findByEmpleadoIdAndFecha(empleadoId, hoy);
        return asistenciasHoy.isEmpty() ? null : asistenciasHoy.get(0);
    }

    public List<Asistencia> getAllAsistencias(LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null) {
            return repository.findByFechaBetweenOrderByFechaDesc(startDate, endDate);
        } else if (startDate != null) {
            return repository.findByFechaOrderByFechaDesc(startDate);
        } else if (endDate != null) {
            return repository.findByFechaOrderByFechaDesc(endDate);
        }
        return repository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.DESC, "fecha"));
    }

    public List<Asistencia> getAsistenciasByEmpleado(Long empleadoId) {
        return repository.findByEmpleadoId(empleadoId);
    }

    @Transactional
    public Asistencia registrarMovimiento(Long empleadoId, String movimientoSolicitado) {
        LocalDate hoy = LocalDate.now();
        Empleado empleado = empleadoRepository.findById(empleadoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado."));

        List<Asistencia> asistenciasHoy = repository.findByEmpleadoIdAndFecha(empleadoId, hoy);
        String movimientoNormalizado = movimientoSolicitado == null ? null : movimientoSolicitado.trim().toUpperCase();

        Asistencia asistencia;
        if (asistenciasHoy.isEmpty()) {
            if ("SALIDA".equals(movimientoNormalizado)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Primero debe registrar la entrada de hoy.");
            }

            asistencia = new Asistencia();
            asistencia.setEmpleado(empleado);
            asistencia.setFecha(hoy);
            asistencia.setEntrada(LocalDateTime.now());
            try {
                return repository.saveAndFlush(asistencia);
            } catch (DataIntegrityViolationException ex) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una asistencia registrada para hoy.");
            }
        } else {
            asistencia = asistenciasHoy.get(0);
            if (asistencia.getSalida() == null) {
                if ("ENTRADA".equals(movimientoNormalizado) && asistencia.getEntrada() != null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La entrada de hoy ya fue registrada.");
                }
                asistencia.setSalida(LocalDateTime.now());
                if (asistencia.getEntrada() != null && asistencia.getSalida() != null) {
                    long diffMinutes = java.time.Duration.between(asistencia.getEntrada(), asistencia.getSalida()).toMinutes();
                    asistencia.setHorasCalculadas(diffMinutes / 60.0);
                }
            } else {
                if ("ENTRADA".equals(movimientoNormalizado)) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El empleado ya registró su jornada completa hoy.");
                }
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El empleado ya completó su turno de hoy.");
            }
        }
        return repository.save(asistencia);
    }
}