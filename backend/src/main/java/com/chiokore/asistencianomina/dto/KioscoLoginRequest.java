package com.chiokore.asistencianomina.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * Petición mínima para el acceso por kiosco.
 * El kiosco solo entrega el identificador del empleado y el backend decide el resto.
 */
@Data
public class KioscoLoginRequest {

    @NotNull
    @Positive
    private Long empleadoId;

    /**
     * Movimiento solicitado desde el kiosco.
     * Si no se envía, el backend conserva el comportamiento de alternancia.
     */
    private String movimiento;
}