package com.chiokore.asistencianomina.services;

import com.chiokore.asistencianomina.config.JwtService;
import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.dto.AuthRequest;
import com.chiokore.asistencianomina.dto.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse login(AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getNombre(), request.getContrasena())
        );

        Empleado empleado = (Empleado) authentication.getPrincipal();

        String token = jwtService.generateAccessToken(empleado.getUsername(), empleado.getId());

        AuthResponse response = new AuthResponse();
        response.setAccessToken(token);
        response.setEmpleadoId(empleado.getId());
        response.setNombre(empleado.getNombre());
        response.setRol(empleado.getRol().getNombre());

        return response;
    }
}
