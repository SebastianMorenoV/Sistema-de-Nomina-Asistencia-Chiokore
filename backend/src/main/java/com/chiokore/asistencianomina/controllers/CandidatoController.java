package com.chiokore.asistencianomina.controllers;
import com.chiokore.asistencianomina.domain.entities.Candidato;
import com.chiokore.asistencianomina.dto.PagedResponse;
import com.chiokore.asistencianomina.services.CandidatoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/candidatos")
@RequiredArgsConstructor
public class CandidatoController {
    private final CandidatoService service;

    @GetMapping
    public PagedResponse<Candidato> getAll(@RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return PagedResponse.from(service.obtenerTodosPaginados(pageable));
    }

    @GetMapping("/{id}")
    public Candidato getById(@PathVariable Long id) {
        return service.obtenerPorId(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Candidato create(@Valid @RequestBody Candidato c) {
        return service.guardar(c);
    }

    @PutMapping("/{id}")
    public Candidato update(@PathVariable Long id, @Valid @RequestBody Candidato c) {
        c.setId(id);
        return service.guardar(c);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.eliminar(id);
    }
}
