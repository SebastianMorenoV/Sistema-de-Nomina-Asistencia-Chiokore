package com.chiokore.asistencianomina.domain.entities;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "empleados")
public class Empleado {
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
    
    private String nombre;
    @Column(columnDefinition = "LONGTEXT")
    private String urlAvatar;
    private String contrasena;
    private Double tarifaHora;
    private Boolean requiereApoyo;
    
    // Soft delete
    private Boolean activo = true;
    
    // Info medica y emergencia
    private String tipoSangre;
    private String condicionesMedicas;
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private String direccion;
}