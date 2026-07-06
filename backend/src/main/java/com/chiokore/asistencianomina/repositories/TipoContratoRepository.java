package com.chiokore.asistencianomina.repositories;
import com.chiokore.asistencianomina.domain.entities.TipoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface TipoContratoRepository extends JpaRepository<TipoContrato, Long> {
	Optional<TipoContrato> findByNombre(String nombre);
}