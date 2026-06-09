package com.chiokore.asistencianomina.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {

    @NotBlank
    private String nombre;

    @NotBlank
    private String contrasena;
}
