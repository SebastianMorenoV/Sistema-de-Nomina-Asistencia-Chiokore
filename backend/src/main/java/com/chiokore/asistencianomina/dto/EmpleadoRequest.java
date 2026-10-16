package com.chiokore.asistencianomina.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

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
    @Size(max = 1000)
    private String condicionesMedicas;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private String direccion;
    private String urlAvatar;

    @Past
    private LocalDate fechaNacimiento;
}
