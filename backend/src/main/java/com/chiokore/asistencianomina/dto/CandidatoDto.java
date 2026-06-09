package com.chiokore.asistencianomina.dto;

import com.chiokore.asistencianomina.domain.entities.Candidato;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CandidatoDto {
    private Long id;
    private String nombre;
    private String telefonoContacto;
    private LocalDate fechaSolicitud;
    private Long estadoId;
    private String estadoNombre;

    public static CandidatoDto fromEntity(Candidato c) {
        CandidatoDto dto = new CandidatoDto();
        dto.setId(c.getId());
        dto.setNombre(c.getNombre());
        dto.setTelefonoContacto(c.getTelefonoContacto());
        dto.setFechaSolicitud(c.getFechaSolicitud());
        dto.setEstadoId(c.getEstado() != null ? c.getEstado().getId() : null);
        dto.setEstadoNombre(c.getEstado() != null ? c.getEstado().getNombre() : null);
        return dto;
    }
}
