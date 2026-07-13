package com.chiokore.asistencianomina.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Horas totales trabajadas por un empleado en un rango de fechas.
 * Lo consume el modulo de Nomina para calcular el pago quincenal.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HorasEmpleadoDto {
    private Long usuarioId;
    private String nombre;
    private Double horasTotales;
}
