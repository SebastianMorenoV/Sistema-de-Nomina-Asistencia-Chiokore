package com.chiokore.asistencianomina.domain.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Data
@Entity
@Table(name = "empleados")
// Oculta los getters de UserDetails (incluye el hash en "password") y los campos internos de los proxies de Hibernate.
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "username", "authorities",
        "accountNonExpired", "accountNonLocked", "credentialsNonExpired", "enabled"})
public class Empleado implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "candidato_id", nullable = true)
    private Candidato candidato;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_contrato_id")
    private TipoContrato tipoContrato;
    
    @NotBlank
    private String nombre;
    @Lob
    private String urlAvatar;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String contrasena;
    @PositiveOrZero
    private Double tarifaHora;
    private Boolean requiereApoyo;
    
    // Huella Dactilar (plantilla SourceAFIS en Base64; nunca se expone en la API)
    @JsonIgnore
    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String huellaDactilar;

    // Soft delete
    private Boolean activo = true;
    
    // Info medica y emergencia
    private String tipoSangre;
    // Mismo largo que en Candidato para que la contratación no falle
    @Column(length = 1000)
    private String condicionesMedicas;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private String direccion;

    // Se usa para mostrar a los cumpleañeros del mes
    private LocalDate fechaNacimiento;

    @JsonProperty("tieneHuella")
    public boolean tieneHuella() {
        return huellaDactilar != null && !huellaDactilar.isBlank();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (rol != null && rol.getNombre() != null) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + rol.getNombre()));
        }
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return contrasena;
    }

    @Override
    public String getUsername() {
        return nombre;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo != null ? activo : true;
    }

    /**
     * Indica si el empleado pertenece al rol de administrador.
     * Se usa para separar el login tradicional del flujo kiosco.
     */
    public boolean esAdministrador() {
        return rol != null && Objects.equals("ADMINISTRADOR", rol.getNombre());
    }

    /**
     * Indica si el empleado pertenece al rol de trabajador.
     */
    public boolean esTrabajador() {
        return rol != null && Objects.equals("TRABAJADOR", rol.getNombre());
    }
}