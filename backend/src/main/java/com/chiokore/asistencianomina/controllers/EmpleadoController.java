package com.chiokore.asistencianomina.controllers;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.services.EmpleadoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {
    private final EmpleadoService service;

    @GetMapping
    public List<Empleado> getAll() { return service.obtenerActivos(); }

    @PostMapping
    public Empleado create(@RequestBody Empleado e) { return service.guardar(e); }

    @PutMapping("/{id}")
    public Empleado update(@PathVariable Long id, @RequestBody Empleado e) { 
        e.setId(id);
        return service.guardar(e); 
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) { service.softDelete(id); }
}