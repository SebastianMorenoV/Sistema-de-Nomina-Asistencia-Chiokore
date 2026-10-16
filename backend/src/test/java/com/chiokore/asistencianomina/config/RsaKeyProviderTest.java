package com.chiokore.asistencianomina.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class RsaKeyProviderTest {

    @TempDir
    private Path dir;

    @Test
    void conRutasSinArchivos_debeGenerarlasYReusarlasAlReiniciar() {
        Path privada = dir.resolve("keys/private_key.pem");
        Path publica = dir.resolve("keys/public_key.pem");

        RsaKeyProvider primero = new RsaKeyProvider(privada.toString(), publica.toString(), "test");
        primero.initialize();

        assertFalse(primero.isEphemeral());
        assertTrue(Files.exists(privada));
        assertTrue(Files.exists(publica));

        RsaKeyProvider segundo = new RsaKeyProvider(privada.toString(), publica.toString(), "test");
        segundo.initialize();

        assertFalse(segundo.isEphemeral());
        assertEquals(primero.getPublicKeyPem(), segundo.getPublicKeyPem());
    }

    @Test
    void sinRutas_debeUsarUnParEfimero() {
        RsaKeyProvider provider = new RsaKeyProvider("", "", "test");
        provider.initialize();

        assertTrue(provider.isEphemeral());
    }

    @Test
    void conLlaveIlegible_noDebeSobrescribirla() throws Exception {
        Path privada = dir.resolve("private_key.pem");
        Path publica = dir.resolve("public_key.pem");
        Files.writeString(privada, "no es una llave");

        RsaKeyProvider provider = new RsaKeyProvider(privada.toString(), publica.toString(), "test");
        provider.initialize();

        assertTrue(provider.isEphemeral());
        assertEquals("no es una llave", Files.readString(privada));
        assertFalse(Files.exists(publica));
    }
}
