package com.chiokore.asistencianomina.repositories;
import com.chiokore.asistencianomina.domain.entities.Candidato;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CandidatoRepository extends JpaRepository<Candidato, Long> {}