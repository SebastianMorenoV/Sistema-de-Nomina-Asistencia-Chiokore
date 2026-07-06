package com.chiokore.asistencianomina.config;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Centraliza la carga o generación del material RSA usado por el IdP.
 *
 * En desarrollo puede generar un par efímero si los archivos configurados no existen.
 * En entornos persistentes se recomienda entregar las llaves por archivo o secreto.
 */
@Component
public class RsaKeyProvider {

    private final String privateKeyPath;
    private final String publicKeyPath;
    private final String keyId;

    @Getter
    private KeyPair keyPair;

    @Getter
    private boolean ephemeral;

    public RsaKeyProvider(
            @Value("${jwt.rsa.private-key-path:}") String privateKeyPath,
            @Value("${jwt.rsa.public-key-path:}") String publicKeyPath,
            @Value("${jwt.rsa.key-id:chiokore-nomina-v1}") String keyId) {
        this.privateKeyPath = privateKeyPath;
        this.publicKeyPath = publicKeyPath;
        this.keyId = keyId;
    }

    @PostConstruct
    public void initialize() {
        this.keyPair = loadConfiguredKeyPair().orElseGet(this::generateEphemeralKeyPair);
    }

    public PrivateKey getPrivateKey() {
        return keyPair.getPrivate();
    }

    public PublicKey getPublicKey() {
        return keyPair.getPublic();
    }

    public String getKeyId() {
        return keyId;
    }

    public String getPublicKeyPem() {
        return toPem(getPublicKey().getEncoded(), "PUBLIC KEY");
    }

    private java.util.Optional<KeyPair> loadConfiguredKeyPair() {
        try {
            if (hasText(privateKeyPath) && hasText(publicKeyPath)) {
                Path privatePath = Path.of(privateKeyPath);
                Path publicPath = Path.of(publicKeyPath);

                if (Files.exists(privatePath) && Files.exists(publicPath)) {
                    PrivateKey privateKey = readPrivateKey(privatePath);
                    PublicKey publicKey = readPublicKey(publicPath);
                    return java.util.Optional.of(new KeyPair(publicKey, privateKey));
                }
            }
        } catch (Exception ignored) {
            // Si la llave configurada no puede cargarse, se usará un par efímero para no bloquear el arranque.
        }

        return java.util.Optional.empty();
    }

    private KeyPair generateEphemeralKeyPair() {
        try {
            KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
            generator.initialize(2048);
            this.ephemeral = true;
            return generator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el par RSA del IdP", ex);
        }
    }

    private PrivateKey readPrivateKey(Path path) throws Exception {
        String pem = Files.readString(path, StandardCharsets.UTF_8);
        String content = stripPemHeaders(pem);
        byte[] decoded = Base64.getDecoder().decode(content);
        return KeyFactory.getInstance("RSA")
                .generatePrivate(new PKCS8EncodedKeySpec(decoded));
    }

    private PublicKey readPublicKey(Path path) throws Exception {
        String pem = Files.readString(path, StandardCharsets.UTF_8);
        String content = stripPemHeaders(pem);
        byte[] decoded = Base64.getDecoder().decode(content);
        return KeyFactory.getInstance("RSA")
                .generatePublic(new X509EncodedKeySpec(decoded));
    }

    private String stripPemHeaders(String pem) {
        return pem
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s+", "");
    }

    private String toPem(byte[] encoded, String type) {
        String base64 = Base64.getMimeEncoder(64, new byte[]{'\n'}).encodeToString(encoded);
        return "-----BEGIN " + type + "-----\n" + base64 + "\n-----END " + type + "-----";
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}