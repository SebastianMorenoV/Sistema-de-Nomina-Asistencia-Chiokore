package com.chiokore.asistencianomina.services;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoService {
    private final EmpleadoRepository repository;
    
    public List<Empleado> obtenerActivos() { return repository.findByActivoTrue(); }
    public Empleado guardar(Empleado e) { return repository.save(e); }
    public void softDelete(Long id) {
        repository.findById(id).ifPresent(e -> {
            e.setActivo(false);
            repository.save(e);
        });
    }
}