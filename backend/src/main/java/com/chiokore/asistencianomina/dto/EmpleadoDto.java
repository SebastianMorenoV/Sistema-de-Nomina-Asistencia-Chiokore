package com.chiokore.asistencianomina.dto;

import com.chiokore.asistencianomina.domain.entities.Empleado;
import lombok.Data;

@Data
public class EmpleadoDto {
    private Long id;
    private String nombre;
    private Long rolId;
    private String rolNombre;
    private Long tipoContratoId;
    private String tipoContratoNombre;
    private Double tarifaHora;
    private Boolean requiereApoyo;
    private Boolean activo;
    private String tipoSangre;
    private String condicionesMedicas;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private String direccion;
    private String urlAvatar;
    private Long candidatoId;

    public static EmpleadoDto fromEntity(Empleado e) {
        EmpleadoDto dto = new EmpleadoDto();
        dto.setId(e.getId());
        dto.setNombre(e.getNombre());
        dto.setRolId(e.getRol() != null ? e.getRol().getId() : null);
        dto.setRolNombre(e.getRol() != null ? e.getRol().getNombre() : null);
        dto.setTipoContratoId(e.getTipoContrato() != null ? e.getTipoContrato().getId() : null);
        dto.setTipoContratoNombre(e.getTipoContrato() != null ? e.getTipoContrato().getNombre() : null);
        dto.setTarifaHora(e.getTarifaHora());
        dto.setRequiereApoyo(e.getRequiereApoyo());
        dto.setActivo(e.getActivo());
        dto.setTipoSangre(e.getTipoSangre());
        dto.setCondicionesMedicas(e.getCondicionesMedicas());
        dto.setContactoEmergenciaNombre(e.getContactoEmergenciaNombre());
        dto.setContactoEmergenciaTelefono(e.getContactoEmergenciaTelefono());
        dto.setDireccion(e.getDireccion());
        dto.setUrlAvatar(e.getUrlAvatar());
        dto.setCandidatoId(e.getCandidato() != null ? e.getCandidato().getId() : null);
        return dto;
    }
}
