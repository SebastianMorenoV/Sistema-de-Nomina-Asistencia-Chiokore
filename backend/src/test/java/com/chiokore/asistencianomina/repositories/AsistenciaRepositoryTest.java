package com.chiokore.asistencianomina.repositories;

import com.chiokore.asistencianomina.domain.entities.Asistencia;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.domain.entities.Rol;
import com.chiokore.asistencianomina.domain.entities.TipoContrato;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class AsistenciaRepositoryTest {

    @Autowired
    private AsistenciaRepository asistenciaRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private TipoContratoRepository tipoContratoRepository;

    private Empleado empleado;
    private LocalDate fecha;

    @BeforeEach
    void setUp() {
        Rol rol = new Rol();
        rol.setNombre("TRABAJADOR");
        rol = rolRepository.save(rol);

        TipoContrato tc = new TipoContrato();
        tc.setNombre("PAGADO");
        tc = tipoContratoRepository.save(tc);

        empleado = new Empleado();
        empleado.setNombre("Juan Pérez");
        empleado.setActivo(true);
        empleado.setRol(rol);
        empleado.setTipoContrato(tc);
        empleado = empleadoRepository.save(empleado);

        fecha = LocalDate.of(2026, 6, 6);
    }

    @Test
    void saveYFindByEmpleadoId_debeRetornarAsistencias() {
        Asistencia a = new Asistencia();
        a.setEmpleado(empleado);
        a.setFecha(fecha);
        a.setEntrada(LocalDateTime.of(2026, 6, 6, 8, 0));

        asistenciaRepository.save(a);

        List<Asistencia> asistencias = asistenciaRepository.findByEmpleadoId(empleado.getId());

        assertEquals(1, asistencias.size());
        assertEquals(fecha, asistencias.get(0).getFecha());
    }

    @Test
    void findByEmpleadoIdAndFecha_debeRetornarAsistenciaEspecifica() {
        Asistencia a = new Asistencia();
        a.setEmpleado(empleado);
        a.setFecha(fecha);
        a.setEntrada(LocalDateTime.of(2026, 6, 6, 8, 0));
        a.setSalida(LocalDateTime.of(2026, 6, 6, 17, 0));

        asistenciaRepository.save(a);

        List<Asistencia> resultado = asistenciaRepository.findByEmpleadoIdAndFecha(
                empleado.getId(), fecha);

        assertEquals(1, resultado.size());
        assertEquals(fecha, resultado.get(0).getFecha());
        assertNotNull(resultado.get(0).getEntrada());
        assertNotNull(resultado.get(0).getSalida());
    }

    @Test
    void save_debeGenerarId() {
        Asistencia a = new Asistencia();
        a.setEmpleado(empleado);
        a.setFecha(fecha);
        a.setEntrada(LocalDateTime.now());

        Asistencia saved = asistenciaRepository.save(a);

        assertNotNull(saved.getId());
        assertTrue(saved.getId() > 0);
    }
}
