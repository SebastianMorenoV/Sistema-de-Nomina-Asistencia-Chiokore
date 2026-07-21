package com.chiokore.asistencianomina.domain.entities;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "candidatos")
public class Candidato {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estado_id")
    private EstadoCandidato estado;
    
    @NotBlank
    private String nombre;
    private String telefonoContacto;
    @NotNull
    private LocalDate fechaSolicitud;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String urlAvatar;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rol_id")
    private Rol rol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "tipo_contrato_id")
    private TipoContrato tipoContrato;

    private String tipoSangre;
    
    @Column(length = 1000)
    private String condicionesMedicas;
    
    private String contactoEmergenciaNombre;
    private String contactoEmergenciaTelefono;
    private Boolean requiereApoyo;
    
    @Column(length = 2000)
    private String notas;
}