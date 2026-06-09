package com.chiokore.asistencianomina.repositories;

import com.chiokore.asistencianomina.domain.entities.Candidato;
import com.chiokore.asistencianomina.domain.entities.EstadoCandidato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class CandidatoRepositoryTest {

    @Autowired
    private CandidatoRepository candidatoRepository;

    @Autowired
    private EstadoCandidatoRepository estadoCandidatoRepository;

    private EstadoCandidato estado;

    @BeforeEach
    void setUp() {
        estado = new EstadoCandidato();
        estado.setNombre("ESPERA");
        estado = estadoCandidatoRepository.save(estado);
    }

    private Candidato crearCandidato(String nombre, String telefono) {
        Candidato c = new Candidato();
        c.setNombre(nombre);
        c.setTelefonoContacto(telefono);
        c.setFechaSolicitud(LocalDate.of(2026, 1, 15));
        c.setEstado(estado);
        return c;
    }

    @Test
    void saveYFindAll_debePersistirYRetornarCandidatos() {
        Candidato c1 = crearCandidato("Juan Pérez", "555-1001");
        Candidato c2 = crearCandidato("Ana Gómez", "555-2002");

        candidatoRepository.save(c1);
        candidatoRepository.save(c2);

        List<Candidato> candidatos = candidatoRepository.findAll();

        assertEquals(2, candidatos.size());
        assertTrue(candidatos.stream().anyMatch(c -> c.getNombre().equals("Juan Pérez")));
        assertTrue(candidatos.stream().anyMatch(c -> c.getNombre().equals("Ana Gómez")));
    }

    @Test
    void findById_debeRetornarCandidato() {
        Candidato c = crearCandidato("María López", "555-3003");
        Candidato saved = candidatoRepository.save(c);

        Optional<Candidato> found = candidatoRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("María López", found.get().getNombre());
        assertEquals("ESPERA", found.get().getEstado().getNombre());
    }

    @Test
    void delete_debeEliminarCandidato() {
        Candidato c = crearCandidato("Carlos Ruiz", "555-4004");
        Candidato saved = candidatoRepository.save(c);

        candidatoRepository.deleteById(saved.getId());

        Optional<Candidato> found = candidatoRepository.findById(saved.getId());
        assertFalse(found.isPresent());
    }
}
