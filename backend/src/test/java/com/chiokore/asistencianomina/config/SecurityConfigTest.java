package com.chiokore.asistencianomina.config;

import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.domain.entities.Rol;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import com.chiokore.asistencianomina.repositories.RolRepository;
import jakarta.servlet.Filter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class SecurityConfigTest {

    private static final String NUEVO_EMPLEADO = "{\"nombre\":\"Nuevo Empleado\"}";

    @Autowired
    private WebApplicationContext context;

    @Autowired
    @Qualifier("springSecurityFilterChain")
    private Filter springSecurityFilterChain;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private JwtService jwtService;

    private MockMvc mockMvc;
    private String tokenAdmin;
    private String tokenKiosco;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(context).addFilters(springSecurityFilterChain).build();

        Rol rol = new Rol();
        rol.setNombre("ADMINISTRADOR");
        rol = rolRepository.save(rol);

        Empleado admin = new Empleado();
        admin.setNombre("admin-prueba");
        admin.setRol(rol);
        admin = empleadoRepository.save(admin);

        tokenAdmin = jwtService.generateAccessToken(admin.getNombre(), admin.getId(), "ADMINISTRADOR", false);
        tokenKiosco = jwtService.generateAccessToken(admin.getNombre(), admin.getId(), "ADMINISTRADOR", true);
    }

    @Test
    void lecturasDeEmpleados_sonPublicas() throws Exception {
        mockMvc.perform(get("/api/empleados")).andExpect(status().isOk());
        mockMvc.perform(get("/api/empleados/pos-status")).andExpect(status().isOk());
    }

    @Test
    void crearEmpleado_sinToken_debeResponder401() throws Exception {
        mockMvc.perform(post("/api/empleados").contentType(MediaType.APPLICATION_JSON).content(NUEVO_EMPLEADO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearEmpleado_conTokenDeKiosco_debeResponder401() throws Exception {
        mockMvc.perform(post("/api/empleados")
                        .header("Authorization", "Bearer " + tokenKiosco)
                        .contentType(MediaType.APPLICATION_JSON).content(NUEVO_EMPLEADO))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearEmpleado_conTokenDeAdministrador_debePermitirlo() throws Exception {
        mockMvc.perform(post("/api/empleados")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON).content(NUEVO_EMPLEADO))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Nuevo Empleado"));
    }

    @Test
    void candidatos_sinToken_debeResponder401() throws Exception {
        mockMvc.perform(get("/api/candidatos")).andExpect(status().isUnauthorized());
    }

    @Test
    void login_conContrasenaIncorrecta_debeResponder401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\":\"admin-prueba\",\"contrasena\":\"incorrecta\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Usuario o contraseña incorrectos."));
    }
}
