package com.chiokore.asistencianomina.controllers;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.dto.EmpleadoRequest;
import com.chiokore.asistencianomina.dto.PagedResponse;
import com.chiokore.asistencianomina.services.EmpleadoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/empleados")
@RequiredArgsConstructor
public class EmpleadoController {
    private final EmpleadoService service;

    @GetMapping
    public PagedResponse<Empleado> getAll(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PagedResponse.from(service.obtenerActivosPaginados(pageable));
    }

    @GetMapping("/pos-status")
    public java.util.List<com.chiokore.asistencianomina.dto.PosEmpleadoDTO> getPosStatus() {
        return service.getPosStatus();
    }

    @PostMapping
    public Empleado create(@Valid @RequestBody EmpleadoRequest request) {
        return service.crear(request);
    }

    @PutMapping("/{id}")
    public Empleado update(@PathVariable Long id, @Valid @RequestBody EmpleadoRequest e) {
        return service.actualizar(id, e);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        service.softDelete(id);
    }

    @PostMapping("/{id}/huella")
    public Empleado enrollFingerprint(@PathVariable Long id, @RequestBody java.util.Map<String, String> payload) {
        String base64Image = payload.get("imagenB64");
        if (base64Image == null || base64Image.isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Imagen no proporcionada");
        }
        return service.enrollFingerprint(id, base64Image);
    }
}
