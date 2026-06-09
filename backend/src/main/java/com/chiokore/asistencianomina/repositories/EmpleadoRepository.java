package com.chiokore.asistencianomina.repositories;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    List<Empleado> findByActivoTrue();
    Page<Empleado> findByActivoTrue(Pageable pageable);
    Optional<Empleado> findByNombre(String nombre);
}