package com.chiokore.asistencianomina.config;

import com.chiokore.asistencianomina.domain.entities.*;
import com.chiokore.asistencianomina.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EstadoCandidatoRepository estadoRepo;
    private final RolRepository rolRepo;
    private final TipoContratoRepository tipoContratoRepo;
    private final EmpleadoRepository empleadoRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        Rol rAdmin = getOrCreateRol("ADMINISTRADOR");
        Rol rTrab = getOrCreateRol("TRABAJADOR");
        TipoContrato tPagado = getOrCreateTipoContrato("PAGADO");
        TipoContrato tVoluntario = getOrCreateTipoContrato("VOLUNTARIO");
        getOrCreateEstadoCandidato("ESPERA");
        getOrCreateEstadoCandidato("ACEPTADO");

        String passwordHash = passwordEncoder.encode("1234");

        ensureEmpleado(
                "admin",
                passwordHash,
                rAdmin,
                tPagado,
                0.0,
                false,
                null,
                "https://ui-avatars.com/api/?name=Admin&size=200&background=1F2937&color=F9FAFB");

        ensureEmpleado(
                "Sofía Martínez",
                passwordHash,
                rTrab,
                tPagado,
                50.0,
                false,
                "O+",
                "https://ui-avatars.com/api/?name=Sofia+Martinez&size=200&background=FDE68A&color=92400E");

        ensureEmpleado(
                "Mateo López",
                passwordHash,
                rTrab,
                tVoluntario,
                0.0,
                true,
                "A+",
                "https://ui-avatars.com/api/?name=Mateo+Lopez&size=200&background=A7F3D0&color=065F46");

        ensureEmpleado(
                "Valentina Ruiz",
                passwordHash,
                rTrab,
                tPagado,
                60.0,
                false,
                "B-",
                "https://ui-avatars.com/api/?name=Valentina+Ruiz&size=200&background=FECACA&color=991B1B");

        System.out.println("Datos de prueba iniciales verificados o inyectados con éxito.");
    }

    private Rol getOrCreateRol(String nombre) {
        return rolRepo.findByNombre(nombre).orElseGet(() -> {
            Rol rol = new Rol();
            rol.setNombre(nombre);
            return rolRepo.save(rol);
        });
    }

    private TipoContrato getOrCreateTipoContrato(String nombre) {
        return tipoContratoRepo.findByNombre(nombre).orElseGet(() -> {
            TipoContrato tipoContrato = new TipoContrato();
            tipoContrato.setNombre(nombre);
            return tipoContratoRepo.save(tipoContrato);
        });
    }

    private EstadoCandidato getOrCreateEstadoCandidato(String nombre) {
        return estadoRepo.findByNombre(nombre).orElseGet(() -> {
            EstadoCandidato estado = new EstadoCandidato();
            estado.setNombre(nombre);
            return estadoRepo.save(estado);
        });
    }

    private void ensureEmpleado(String nombre,
                                String passwordHash,
                                Rol rol,
                                TipoContrato tipoContrato,
                                Double tarifaHora,
                                boolean requiereApoyo,
                                String tipoSangre,
                                String urlAvatar) {
        Optional<Empleado> existente = empleadoRepo.findByNombre(nombre);
        if (existente.isPresent()) {
            return;
        }

        Empleado empleado = new Empleado();
        empleado.setNombre(nombre);
        empleado.setContrasena(passwordHash);
        empleado.setRol(rol);
        empleado.setTipoContrato(tipoContrato);
        empleado.setTarifaHora(tarifaHora);
        empleado.setActivo(true);
        empleado.setRequiereApoyo(requiereApoyo);
        empleado.setTipoSangre(tipoSangre);
        empleado.setUrlAvatar(urlAvatar);
        empleadoRepo.save(empleado);
    }
}