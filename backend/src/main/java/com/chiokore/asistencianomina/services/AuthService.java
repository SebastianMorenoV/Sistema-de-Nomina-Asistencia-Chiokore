package com.chiokore.asistencianomina.services;

import com.chiokore.asistencianomina.config.JwtService;
import com.chiokore.asistencianomina.domain.entities.Asistencia;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.dto.AuthRequest;
import com.chiokore.asistencianomina.dto.AuthResponse;
import com.chiokore.asistencianomina.dto.KioscoLoginRequest;
import com.chiokore.asistencianomina.dto.KioscoLoginResponse;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import jakarta.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final Duration ESPERA_MINIMA_HUELLA = Duration.ofMinutes(1);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final EmpleadoRepository empleadoRepository;
    private final AsistenciaService asistenciaService;
    private final BiometricService biometricService;

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getNombre(), request.getContrasena())
        );

        Empleado empleado = (Empleado) authentication.getPrincipal();

        if (!empleado.esAdministrador()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El login regular está restringido a administradores.");
        }

        String token = jwtService.generateAccessToken(
                empleado.getUsername(),
                empleado.getId(),
                empleado.getRol() != null ? empleado.getRol().getNombre() : null,
                false);

        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setEmpleadoId(empleado.getId());
        response.setNombre(empleado.getNombre());
        response.setRol(empleado.getRol().getNombre());

        return response;
    }

    /**
     * Cierra o abre la asistencia del trabajador desde un dispositivo de kiosco.
     * El flujo no usa contraseña y está pensado para una interacción de una sola acción.
     */
    @Transactional
    public KioscoLoginResponse kioskLogin(KioscoLoginRequest request) {
        Empleado empleado = empleadoRepository.findById(request.getEmpleadoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Empleado no encontrado."));

        if (!empleado.isEnabled()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "El empleado está inactivo.");
        }

        // Permitimos el acceso por kiosco a todos los empleados activos (incluyendo administradores)
        // para que puedan checar entrada/salida desde la pantalla o con huella.
        return registrarEnKiosco(empleado, request.getMovimiento());
    }

    /**
     * Identifica al empleado por su huella y alterna su entrada/salida del día.
     */
    @Transactional
    public KioscoLoginResponse kioskFingerprintLogin(String imagenB64) {
        Empleado empleado = biometricService.identifyEmployee(imagenB64)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Huella no reconocida o empleado inactivo."));

        // Evita que un segundo escaneo accidental cierre la jornada justo después de abrirla.
        Asistencia hoy = asistenciaService.getAsistenciaHoy(empleado.getId());
        if (hoy != null && hoy.getSalida() == null && hoy.getEntrada() != null
                && hoy.getEntrada().isAfter(LocalDateTime.now().minus(ESPERA_MINIMA_HUELLA))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Tu entrada ya fue registrada hace un momento.");
        }
        return registrarEnKiosco(empleado, null);
    }

    private KioscoLoginResponse registrarEnKiosco(Empleado empleado, String movimiento) {
        Asistencia asistencia = asistenciaService.registrarMovimiento(empleado.getId(), movimiento);
        boolean esEntrada = asistencia.getSalida() == null;

        String token = jwtService.generateAccessToken(
                empleado.getUsername(),
                empleado.getId(),
                empleado.getRol() != null ? empleado.getRol().getNombre() : null,
                true);

        KioscoLoginResponse response = new KioscoLoginResponse();
        response.setAccessToken(token);
        response.setEmpleadoId(empleado.getId());
        response.setNombre(empleado.getNombre());
        response.setRol(empleado.getRol() != null ? empleado.getRol().getNombre() : null);
        response.setAsistenciaId(asistencia.getId());
        response.setFecha(asistencia.getFecha());
        response.setEntrada(asistencia.getEntrada());
        response.setSalida(asistencia.getSalida());
        response.setHorasCalculadas(asistencia.getHorasCalculadas());
        response.setMovimiento(esEntrada ? "ENTRADA" : "SALIDA");
        return response;
    }
}
