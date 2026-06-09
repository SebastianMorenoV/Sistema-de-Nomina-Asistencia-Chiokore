package com.chiokore.asistencianomina.dto;

import lombok.Data;

@Data
public class AuthResponse {

    private String accessToken;
    private Long empleadoId;
    private String nombre;
    private String rol;
}
