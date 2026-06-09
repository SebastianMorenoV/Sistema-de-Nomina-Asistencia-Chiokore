package com.chiokore.asistencianomina.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

@Data
public class EmpleadoRequest {

    @NotBlank
    private String nombre;

    private String contrasena;

    @PositiveOrZero
    private Double tarifaHora;

    private Long rolId;
    private Long tipoContratoId;
    private Boolean requiereApoyo;
    private String tipoSangre;
    private String condicionesMedicas;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private String direccion;
    private String urlAvatar;
}
