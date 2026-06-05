package com.chiokore.asistencianomina.domain.entities;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "recibos_nomina")
public class ReciboNomina {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;
    
    private LocalDate fechaInicioPeriodo;
    private LocalDate fechaFinPeriodo;
    private Double horasPagadas;
    private Double tarifaAplicada;
    private Double totalPagado;
    private LocalDate fechaEmision;
    private Boolean firmaConfirmada;
}