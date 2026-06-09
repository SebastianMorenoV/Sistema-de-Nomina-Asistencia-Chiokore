package com.chiokore.asistencianomina.services;
import com.chiokore.asistencianomina.domain.entities.Candidato;
import com.chiokore.asistencianomina.repositories.CandidatoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CandidatoService {
    private final CandidatoRepository repository;

    public Page<Candidato> obtenerTodosPaginados(Pageable pageable) {
        return repository.findAll(pageable);
    }

    public List<Candidato> obtenerTodos() {
        return repository.findAll();
    }

    public Candidato obtenerPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Candidato no encontrado con ID: " + id));
    }

    public Candidato guardar(Candidato c) {
        return repository.save(c);
    }

    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Candidato no encontrado con ID: " + id);
        }
        repository.deleteById(id);
    }
}
