package com.chiokore.asistencianomina.domain.entities;
import jakarta.persistence.*;
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
    
    private String nombre;
    private String telefonoContacto;
    private LocalDate fechaSolicitud;
}