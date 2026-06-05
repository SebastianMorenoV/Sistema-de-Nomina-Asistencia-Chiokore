package com.chiokore.asistencianomina.domain.entities;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "estados_candidato")
public class EstadoCandidato {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre; // ESPERA, ACEPTADO, RECHAZADO
}