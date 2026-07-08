package com.chiokore.asistencianomina.repositories;
import com.chiokore.asistencianomina.domain.entities.EstadoCandidato;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EstadoCandidatoRepository extends JpaRepository<EstadoCandidato, Long> {
	Optional<EstadoCandidato> findByNombre(String nombre);
}