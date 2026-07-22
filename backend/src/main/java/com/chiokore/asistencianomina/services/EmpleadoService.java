package com.chiokore.asistencianomina.services;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.dto.EmpleadoRequest;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import com.chiokore.asistencianomina.repositories.RolRepository;
import com.chiokore.asistencianomina.repositories.TipoContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoService {
    private final EmpleadoRepository repository;
    private final RolRepository rolRepository;
    private final TipoContratoRepository tipoContratoRepository;
    private final com.chiokore.asistencianomina.repositories.AsistenciaRepository asistenciaRepository;
    
    public List<Empleado> obtenerActivos() {
        return repository.findByActivoTrue();
    }

    public List<com.chiokore.asistencianomina.dto.PosEmpleadoDTO> getPosStatus() {
        java.time.LocalDate hoy = java.time.LocalDate.now();
        List<Empleado> todos = repository.findAll();
        return todos.stream().map(e -> {
            boolean marco = !asistenciaRepository.findByEmpleadoIdAndFecha(e.getId(), hoy).isEmpty();
            
            return new com.chiokore.asistencianomina.dto.PosEmpleadoDTO(
                    e.getId(),
                    e.getNombre(),
                    e.getRol() != null ? e.getRol().getNombre() : "Sin Rol",
                    e.getActivo(),
                    marco
            );
        }).toList();
    }

    public Empleado guardar(Empleado e) {
        return repository.save(e);
    }

    public Empleado crear(EmpleadoRequest dto) {
        Empleado e = new Empleado();
        aplicarDto(e, dto);
        return repository.save(e);
    }

    public Empleado actualizar(Long id, EmpleadoRequest dto) {
        Empleado e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
        aplicarDto(e, dto);
        return repository.save(e);
    }

    public Page<Empleado> obtenerActivosPaginados(Pageable pageable) {
        return repository.findByActivoTrue(pageable);
    }

    public void softDelete(Long id) {
        repository.findById(id).ifPresent(e -> {
            e.setActivo(false);
            repository.save(e);
        });
    }

    private void aplicarDto(Empleado e, EmpleadoRequest dto) {
        e.setNombre(dto.getNombre());
        e.setContrasena(dto.getContrasena());
        e.setTarifaHora(dto.getTarifaHora());
        e.setRequiereApoyo(dto.getRequiereApoyo());
        e.setTipoSangre(dto.getTipoSangre());
        e.setCondicionesMedicas(dto.getCondicionesMedicas());
        e.setContactoEmergenciaNombre(dto.getContactoEmergenciaNombre());
        e.setContactoEmergenciaTelefono(dto.getContactoEmergenciaTelefono());
        e.setDireccion(dto.getDireccion());
        e.setUrlAvatar(dto.getUrlAvatar());

        if (dto.getRolId() != null) {
            e.setRol(rolRepository.findById(dto.getRolId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Rol no encontrado")));
        }
        if (dto.getTipoContratoId() != null) {
            e.setTipoContrato(tipoContratoRepository.findById(dto.getTipoContratoId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tipo de contrato no encontrado")));
        }
    }

    public Empleado enrollFingerprint(Long id, String base64Image) {
        Empleado e = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado"));
        
        String serializedTemplate = BiometricService.createTemplateFromBase64Image(base64Image);
        e.setHuellaDactilar(serializedTemplate);
        return repository.save(e);
    }
}
