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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoService {
    private final EmpleadoRepository repository;
    private final RolRepository rolRepository;
    private final TipoContratoRepository tipoContratoRepository;
    private final com.chiokore.asistencianomina.repositories.AsistenciaRepository asistenciaRepository;
    private final BiometricService biometricService;
    private final PasswordEncoder passwordEncoder;
    
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

    /**
     * Empleados activos que cumplen años en el mes indicado (1-12), ordenados por día.
     */
    public List<Empleado> obtenerCumpleaneros(int mes) {
        if (mes < 1 || mes > 12) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El mes debe estar entre 1 y 12.");
        }
        return repository.findByActivoTrue().stream()
                .filter(e -> e.getFechaNacimiento() != null && e.getFechaNacimiento().getMonthValue() == mes)
                .sorted(Comparator.comparingInt((Empleado e) -> e.getFechaNacimiento().getDayOfMonth())
                        .thenComparing(Empleado::getNombre))
                .toList();
    }

    public void softDelete(Long id) {
        repository.findById(id).ifPresent(e -> {
            e.setActivo(false);
            repository.save(e);
        });
    }

    private void aplicarDto(Empleado e, EmpleadoRequest dto) {
        e.setNombre(dto.getNombre());
        // Sin contraseña nueva se conserva la actual; el login de administradores la compara con BCrypt
        if (dto.getContrasena() != null && !dto.getContrasena().isBlank()) {
            e.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        }
        e.setTarifaHora(dto.getTarifaHora());
        e.setRequiereApoyo(dto.getRequiereApoyo());
        e.setTipoSangre(dto.getTipoSangre());
        e.setCondicionesMedicas(dto.getCondicionesMedicas());
        e.setContactoEmergenciaNombre(dto.getContactoEmergenciaNombre());
        e.setContactoEmergenciaTelefono(dto.getContactoEmergenciaTelefono());
        e.setDireccion(dto.getDireccion());
        e.setFechaNacimiento(dto.getFechaNacimiento());
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
        
        e.setHuellaDactilar(biometricService.createTemplate(base64Image));
        return repository.save(e);
    }
}
