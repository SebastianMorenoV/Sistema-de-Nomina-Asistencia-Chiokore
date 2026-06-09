package com.chiokore.asistencianomina.repositories;

import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.domain.entities.Rol;
import com.chiokore.asistencianomina.domain.entities.TipoContrato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class EmpleadoRepositoryTest {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TipoContratoRepository tipoContratoRepository;

    private Rol rol;
    private TipoContrato tipoContrato;

    @BeforeEach
    void setUp() {
        rol = new Rol();
        rol.setNombre("TRABAJADOR");
        rol = rolRepository.save(rol);

        tipoContrato = new TipoContrato();
        tipoContrato.setNombre("PAGADO");
        tipoContrato = tipoContratoRepository.save(tipoContrato);
    }

    private Empleado crearEmpleado(String nombre, Boolean activo) {
        Empleado e = new Empleado();
        e.setNombre(nombre);
        e.setActivo(activo);
        e.setRol(rol);
        e.setTipoContrato(tipoContrato);
        e.setTarifaHora(50.0);
        return e;
    }

    @Test
    void findByActivoTrue_debeRetornarSoloEmpleadosActivos() {
        Empleado activo1 = crearEmpleado("Juan Pérez", true);
        Empleado activo2 = crearEmpleado("Ana Gómez", true);
        Empleado inactivo = crearEmpleado("Carlos Díaz", false);

        empleadoRepository.save(activo1);
        empleadoRepository.save(activo2);
        empleadoRepository.save(inactivo);

        List<Empleado> activos = empleadoRepository.findByActivoTrue();

        assertEquals(2, activos.size());
        assertTrue(activos.stream().anyMatch(e -> e.getNombre().equals("Juan Pérez")));
        assertTrue(activos.stream().anyMatch(e -> e.getNombre().equals("Ana Gómez")));
        assertTrue(activos.stream().noneMatch(e -> e.getNombre().equals("Carlos Díaz")));
    }

    @Test
    void save_debePersistirYRetornarEmpleadoConId() {
        Empleado e = crearEmpleado("Nuevo Empleado", true);
        Empleado saved = empleadoRepository.save(e);

        assertNotNull(saved.getId());
        assertEquals("Nuevo Empleado", saved.getNombre());

        Optional<Empleado> found = empleadoRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Nuevo Empleado", found.get().getNombre());
    }

    @Test
    void findById_debeRetornarEmpleadoPorId() {
        Empleado e = crearEmpleado("María López", true);
        Empleado saved = empleadoRepository.save(e);

        Optional<Empleado> found = empleadoRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("María López", found.get().getNombre());
        assertEquals(rol.getId(), found.get().getRol().getId());
        assertEquals(tipoContrato.getId(), found.get().getTipoContrato().getId());
    }
}
