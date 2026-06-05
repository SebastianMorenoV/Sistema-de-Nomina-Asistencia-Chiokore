package com.chiokore.asistencianomina.domain.entities;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipos_contrato")
public class TipoContrato {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre; // PAGADO, VOLUNTARIADO
}