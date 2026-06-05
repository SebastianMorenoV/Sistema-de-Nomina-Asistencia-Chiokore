package com.chiokore.asistencianomina.services;

import com.chiokore.asistencianomina.domain.entities.Asistencia;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.math.BigDecimal;

@Service
public class NominaService {

    public BigDecimal calcularTotal(Empleado empleado, List<Asistencia> asistencias) {
        long minutosTotales = 0;
        
        for (Asistencia asistencia : asistencias) {
            if (asistencia.getEntrada() != null && asistencia.getSalida() != null) {
                Duration duracion = Duration.between(asistencia.getEntrada(), asistencia.getSalida());
                minutosTotales += duracion.toMinutes();
            }
        }
        
        double horas = minutosTotales / 60.0;
        double tarifa = empleado.getTarifaHora() != null ? empleado.getTarifaHora() : 0.0;
        return BigDecimal.valueOf(tarifa).multiply(BigDecimal.valueOf(horas));
    }
}
