package com.chiokore.asistencianomina.services;

import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.domain.entities.Rol;
import com.chiokore.asistencianomina.domain.entities.TipoContrato;
import com.chiokore.asistencianomina.dto.EmpleadoRequest;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import com.chiokore.asistencianomina.repositories.RolRepository;
import com.chiokore.asistencianomina.repositories.TipoContratoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmpleadoServiceTest {

    @Mock
    private EmpleadoRepository repository;

    @Mock
    private RolRepository rolRepository;

    @Mock
    private TipoContratoRepository tipoContratoRepository;

    @InjectMocks
    private EmpleadoService service;

    private Empleado crearEmpleado(Long id, String nombre, Boolean activo) {
        Empleado e = new Empleado();
        e.setId(id);
        e.setNombre(nombre);
        e.setActivo(activo);
        return e;
    }

    @Test
    void obtenerActivos_debeRetornarSoloEmpleadosActivos() {
        Empleado e1 = crearEmpleado(1L, "Juan Pérez", true);
        Empleado e2 = crearEmpleado(2L, "Ana Gómez", true);
        when(repository.findByActivoTrue()).thenReturn(List.of(e1, e2));

        List<Empleado> resultado = service.obtenerActivos();

        assertEquals(2, resultado.size());
        assertEquals("Juan Pérez", resultado.get(0).getNombre());
        assertEquals("Ana Gómez", resultado.get(1).getNombre());
        verify(repository, times(1)).findByActivoTrue();
    }

    @Test
    void crear_debeGuardarEmpleadoDesdeDto() {
        EmpleadoRequest dto = new EmpleadoRequest();
        dto.setNombre("Nuevo Empleado");
        dto.setTarifaHora(50.0);
        dto.setRolId(1L);
        dto.setTipoContratoId(2L);

        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("TRABAJADOR");
        TipoContrato tc = new TipoContrato();
        tc.setId(2L);
        tc.setNombre("PAGADO");

        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(tipoContratoRepository.findById(2L)).thenReturn(Optional.of(tc));
        when(repository.save(any(Empleado.class))).thenAnswer(inv -> {
            Empleado e = inv.getArgument(0);
            e.setId(10L);
            return e;
        });

        Empleado resultado = service.crear(dto);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
        assertEquals("Nuevo Empleado", resultado.getNombre());
        assertEquals(50.0, resultado.getTarifaHora());
        assertEquals(rol, resultado.getRol());
        assertEquals(tc, resultado.getTipoContrato());
        verify(repository, times(1)).save(any(Empleado.class));
    }

    @Test
    void actualizar_debeModificarEmpleadoExistente() {
        Empleado existente = crearEmpleado(1L, "Viejo Nombre", true);

        EmpleadoRequest dto = new EmpleadoRequest();
        dto.setNombre("Nombre Nuevo");
        dto.setTarifaHora(75.0);
        dto.setRolId(1L);

        Rol rol = new Rol();
        rol.setId(1L);
        rol.setNombre("ADMINISTRADOR");

        when(repository.findById(1L)).thenReturn(Optional.of(existente));
        when(rolRepository.findById(1L)).thenReturn(Optional.of(rol));
        when(repository.save(any(Empleado.class))).thenReturn(existente);

        Empleado resultado = service.actualizar(1L, dto);

        assertEquals("Nombre Nuevo", resultado.getNombre());
        assertEquals(75.0, resultado.getTarifaHora());
        assertEquals(rol, resultado.getRol());
        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(existente);
    }

    @Test
    void softDelete_debeMarcarActivoComoFalseYGuardar() {
        Empleado existente = crearEmpleado(1L, "Juan Pérez", true);
        when(repository.findById(1L)).thenReturn(Optional.of(existente));

        service.softDelete(1L);

        verify(repository, times(1)).findById(1L);
        verify(repository, times(1)).save(existente);
        assertFalse(existente.getActivo());
    }

    @Test
    void softDelete_cuandoEmpleadoNoExiste_noHaceNada() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        service.softDelete(99L);

        verify(repository, times(1)).findById(99L);
        verify(repository, never()).save(any());
    }
}
