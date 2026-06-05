const fs = require('fs');
const path = require('path');

const baseDir = path.join(__dirname, 'src', 'main', 'java', 'com', 'chiokore', 'asistencianomina');

const files = {
"domain/entities/EstadoCandidato.java": `package com.chiokore.asistencianomina.domain.entities;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "estados_candidato")
public class EstadoCandidato {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre; // ESPERA, ACEPTADO, RECHAZADO
}`,

"domain/entities/Rol.java": `package com.chiokore.asistencianomina.domain.entities;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "roles")
public class Rol {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre; // ADMINISTRADOR, TRABAJADOR
}`,

"domain/entities/TipoContrato.java": `package com.chiokore.asistencianomina.domain.entities;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "tipos_contrato")
public class TipoContrato {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre; // PAGADO, VOLUNTARIADO
}`,

"domain/entities/Candidato.java": `package com.chiokore.asistencianomina.domain.entities;
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
}`,

"domain/entities/Empleado.java": `package com.chiokore.asistencianomina.domain.entities;
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
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rol_id")
    private Rol rol;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tipo_contrato_id")
    private TipoContrato tipoContrato;
    
    private String nombre;
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
}`
};

Object.keys(files).forEach(filename => {
    fs.writeFileSync(path.join(baseDir, filename), files[filename]);
});

// Delete old enums if they exist
const enumsDir = path.join(baseDir, 'domain', 'enums');
if (fs.existsSync(enumsDir)) {
    fs.rmSync(enumsDir, { recursive: true, force: true });
}
console.log("Catálogos normalizados correctamente.");
