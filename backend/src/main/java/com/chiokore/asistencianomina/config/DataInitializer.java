package com.chiokore.asistencianomina.config;

import com.chiokore.asistencianomina.domain.entities.*;
import com.chiokore.asistencianomina.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EstadoCandidatoRepository estadoRepo;
    private final RolRepository rolRepo;
    private final TipoContratoRepository tipoContratoRepo;
    private final EmpleadoRepository empleadoRepo;

    @Override
    public void run(String... args) throws Exception {
        if (rolRepo.count() == 0) {
            Rol rAdmin = new Rol(); rAdmin.setNombre("ADMINISTRADOR"); rolRepo.save(rAdmin);
            Rol rTrab = new Rol(); rTrab.setNombre("TRABAJADOR"); rolRepo.save(rTrab);
            
            TipoContrato tPagado = new TipoContrato(); tPagado.setNombre("PAGADO"); tipoContratoRepo.save(tPagado);
            TipoContrato tVoluntario = new TipoContrato(); tVoluntario.setNombre("VOLUNTARIO"); tipoContratoRepo.save(tVoluntario);
            
            EstadoCandidato eEspera = new EstadoCandidato(); eEspera.setNombre("ESPERA"); estadoRepo.save(eEspera);
            EstadoCandidato eAceptado = new EstadoCandidato(); eAceptado.setNombre("ACEPTADO"); estadoRepo.save(eAceptado);

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            String passowrdEnconder = encoder.encode("1234");
            Empleado e1 = new Empleado();
            e1.setNombre("Sofía Martínez");
            e1.setContrasena(passowrdEnconder);
            e1.setRol(rTrab);
            e1.setTipoContrato(tPagado);
            e1.setTarifaHora(50.0);
            e1.setActivo(true);
            e1.setRequiereApoyo(false);
            e1.setTipoSangre("O+");
            e1.setUrlAvatar("https://ui-avatars.com/api/?name=Sofia+Martinez&size=200&background=FDE68A&color=92400E");
            empleadoRepo.save(e1);

            Empleado e2 = new Empleado();
            e2.setNombre("Mateo López");
            e2.setContrasena(passowrdEnconder);
            e2.setRol(rTrab);
            e2.setTipoContrato(tVoluntario);
            e2.setTarifaHora(0.0);
            e2.setActivo(true);
            e2.setRequiereApoyo(true);
            e2.setCondicionesMedicas("Neurodivergente");
            e2.setTipoSangre("A+");
            e2.setUrlAvatar("https://ui-avatars.com/api/?name=Mateo+Lopez&size=200&background=A7F3D0&color=065F46");
            empleadoRepo.save(e2);
            
            Empleado e3 = new Empleado();
            e3.setNombre("Valentina Ruiz");
            e3.setContrasena(passowrdEnconder);
            e3.setRol(rTrab);
            e3.setTipoContrato(tPagado);
            e3.setTarifaHora(60.0);
            e3.setActivo(true);
            e3.setRequiereApoyo(false);
            e3.setTipoSangre("B-");
            e3.setUrlAvatar("https://ui-avatars.com/api/?name=Valentina+Ruiz&size=200&background=FECACA&color=991B1B");
            empleadoRepo.save(e3);
            
            System.out.println("Datos de prueba (Cafeteria/Bazar) inyectados con éxito.");
        }
    }
}