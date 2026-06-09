package com.chiokore.asistencianomina.controllers;

import com.chiokore.asistencianomina.dto.AuthRequest;
import com.chiokore.asistencianomina.dto.AuthResponse;
import com.chiokore.asistencianomina.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
