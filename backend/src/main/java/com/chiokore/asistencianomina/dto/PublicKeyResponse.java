package com.chiokore.asistencianomina.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Respuesta pública con la llave necesaria para validar tokens RS256 fuera del proceso.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PublicKeyResponse {

    private String issuer;
    private String keyId;
    private String algorithm;
    private String publicKeyPem;
    private boolean ephemeral;
}