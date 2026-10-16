package com.chiokore.asistencianomina.config;

import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(HttpMethod.POST, "/api/auth/login", "/api/auth/kiosk-login").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/auth/public-key").permitAll()
                // Lecturas que usan el kiosco, el POS y nómina
                .requestMatchers(HttpMethod.GET, "/api/empleados", "/api/empleados/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/horarios").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/nominas/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/asistencias", "/api/asistencias/**").permitAll()
                // Registro de asistencia desde el kiosco
                .requestMatchers(HttpMethod.POST, "/api/asistencias/registrar/**", "/api/asistencias/fingerprint").permitAll()
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // Altas, cambios, bajas, huellas, horarios y candidatos: solo administradores
                .anyRequest().hasRole("ADMINISTRADOR")
            )
            // Sin token (o con uno vencido) responde 401 para que el panel pida iniciar sesión
            .exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)))
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService(EmpleadoRepository empleadoRepository) {
        return username -> empleadoRepository.findByNombre(username)
            .orElseThrow(() -> new UsernameNotFoundException("Empleado no encontrado: " + username));
    }
}
