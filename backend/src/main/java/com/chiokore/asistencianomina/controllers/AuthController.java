package com.chiokore.asistencianomina.controllers;

import com.chiokore.asistencianomina.config.JwtService;
import com.chiokore.asistencianomina.dto.AuthRequest;
import com.chiokore.asistencianomina.dto.AuthResponse;
import com.chiokore.asistencianomina.dto.KioscoLoginRequest;
import com.chiokore.asistencianomina.dto.KioscoLoginResponse;
import com.chiokore.asistencianomina.dto.PublicKeyResponse;
import com.chiokore.asistencianomina.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }

    /**
     * Acceso sin contraseña para el kiosco físico.
     * El backend valida el rol y registra la asistencia antes de emitir el JWT.
     */
    @PostMapping("/kiosk-login")
    public KioscoLoginResponse kioskLogin(@Valid @RequestBody KioscoLoginRequest request) {
        return authService.kioskLogin(request);
    }

    /**
     * Publica la llave pública usada para validar tokens RS256 en otros sistemas del mismo servidor.
     */
    @GetMapping("/public-key")
    public ResponseEntity<PublicKeyResponse> publicKey() {
        PublicKeyResponse response = new PublicKeyResponse(
                jwtService.getIssuer(),
            jwtService.getKeyId(),
                "RS256",
                jwtService.getPublicKeyPem(),
                jwtService.isUsingEphemeralKeyPair()
        );
        return ResponseEntity.ok(response);
    }
}
