package com.chiokore.asistencianomina.dto;

import com.chiokore.asistencianomina.domain.entities.Horario;
import lombok.Data;

@Data
public class HorarioDto {
    private Long id;
    private Long empleadoId;
    private String empleadoNombre;
    private String diaSemana;
    private String horaInicio;
    private String horaFin;

    public static HorarioDto fromEntity(Horario h) {
        HorarioDto dto = new HorarioDto();
        dto.setId(h.getId());
        dto.setEmpleadoId(h.getEmpleado().getId());
        dto.setEmpleadoNombre(h.getEmpleado().getNombre());
        dto.setDiaSemana(h.getDiaSemana());
        dto.setHoraInicio(h.getHoraInicio() != null ? h.getHoraInicio().toString() : "");
        dto.setHoraFin(h.getHoraFin() != null ? h.getHoraFin().toString() : "");
        return dto;
    }
}
