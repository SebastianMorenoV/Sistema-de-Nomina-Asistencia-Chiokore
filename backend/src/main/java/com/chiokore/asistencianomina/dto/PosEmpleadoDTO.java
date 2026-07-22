package com.chiokore.asistencianomina.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PosEmpleadoDTO {
    private Long id;
    private String nombre;
    private String rol;
    private Boolean activo;
    private Boolean marcoAsistencia;
}
