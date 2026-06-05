package com.chiokore.asistencianomina.repositories;
import com.chiokore.asistencianomina.domain.entities.ReciboNomina;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ReciboNominaRepository extends JpaRepository<ReciboNomina, Long> {
    List<ReciboNomina> findByEmpleadoId(Long empleadoId);
}