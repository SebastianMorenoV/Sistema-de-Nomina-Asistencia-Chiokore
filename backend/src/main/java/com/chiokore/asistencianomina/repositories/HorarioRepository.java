package com.chiokore.asistencianomina.repositories;

import com.chiokore.asistencianomina.domain.entities.Horario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HorarioRepository extends JpaRepository<Horario, Long> {
}