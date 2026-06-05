package com.chiokore.asistencianomina.repositories;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    List<Empleado> findByActivoTrue();
}