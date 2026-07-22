package com.chiokore.asistencianomina.repositories;
import com.chiokore.asistencianomina.domain.entities.Asistencia;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.time.LocalDate;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {
    List<Asistencia> findByEmpleadoId(Long empleadoId);
    List<Asistencia> findByEmpleadoIdAndFecha(Long empleadoId, LocalDate fecha);
    List<Asistencia> findByEmpleadoIdAndFechaBetween(Long empleadoId, LocalDate inicio, LocalDate fin);
    List<Asistencia> findByFechaBetweenOrderByFechaDesc(LocalDate inicio, LocalDate fin);
    List<Asistencia> findByFechaOrderByFechaDesc(LocalDate fecha);
}