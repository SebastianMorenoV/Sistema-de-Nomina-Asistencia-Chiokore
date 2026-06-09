package com.chiokore.asistencianomina.domain.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "asistencias")
public class Asistencia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;
    
    @NotNull
    private LocalDate fecha;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private Double horasCalculadas;
    private Boolean modificadoManualmente;
}