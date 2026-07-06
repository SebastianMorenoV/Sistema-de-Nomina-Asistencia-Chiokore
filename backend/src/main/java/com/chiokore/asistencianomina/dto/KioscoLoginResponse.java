package com.chiokore.asistencianomina.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Respuesta enriquecida del flujo kiosco.
 * Reutiliza la sesión JWT y agrega la evidencia del registro de asistencia.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KioscoLoginResponse extends AuthResponse {

    private Long asistenciaId;
    private LocalDate fecha;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private Double horasCalculadas;
    private String movimiento;
}