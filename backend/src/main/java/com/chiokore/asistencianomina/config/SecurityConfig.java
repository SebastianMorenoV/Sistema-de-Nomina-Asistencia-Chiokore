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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.http.HttpMethod;

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
                .requestMatchers(HttpMethod.GET, "/api/empleados").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/empleados").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/empleados/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/empleados/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/horarios").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/horarios", "/api/horarios/assign").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/asistencias").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/asistencias/horas").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/asistencias/registrar/**").permitAll()
                .requestMatchers("/api/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
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
