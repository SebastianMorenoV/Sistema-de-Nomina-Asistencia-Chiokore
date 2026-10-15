package com.chiokore.asistencianomina.services;

import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BiometricServiceTest {

    @Mock
    private EmpleadoRepository empleadoRepository;

    // Bytes que en Base64 estándar producen '+' y '/', y en Base64URL '-' y '_'
    private static final byte[] BYTES = {(byte) 0xfb, (byte) 0xff, (byte) 0xbf, 0x01};

    @Test
    void decodeImage_aceptaBase64UrlSinRelleno() {
        String base64Url = Base64.getUrlEncoder().withoutPadding().encodeToString(BYTES);
        assertTrue(base64Url.contains("-") || base64Url.contains("_"));
        assertArrayEquals(BYTES, BiometricService.decodeImage(base64Url));
    }

    @Test
    void decodeImage_aceptaBase64ConPrefijoData() {
        String dataUrl = "data:image/png;base64," + Base64.getEncoder().encodeToString(BYTES);
        assertArrayEquals(BYTES, BiometricService.decodeImage(dataUrl));
    }

    @Test
    void createTemplate_imagenInvalida_lanza400() {
        BiometricService service = new BiometricService(empleadoRepository, false);
        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.createTemplate(Base64.getEncoder().encodeToString("no es png".getBytes())));
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatusCode());
    }

    @Test
    void simulacion_deshabilitada_lanza403() {
        BiometricService service = new BiometricService(empleadoRepository, false);
        when(empleadoRepository.findByActivoTrue()).thenReturn(List.of());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> service.identifyEmployee(BiometricService.SIMULATED_IMAGE));
        assertEquals(HttpStatus.FORBIDDEN, ex.getStatusCode());
        assertThrows(ResponseStatusException.class,
                () -> service.createTemplate(BiometricService.SIMULATED_IMAGE));
    }

    @Test
    void simulacion_habilitada_identificaEmpleadoConHuellaSimulada() {
        BiometricService service = new BiometricService(empleadoRepository, true);
        Empleado sinHuella = new Empleado();
        sinHuella.setId(1L);
        Empleado conHuella = new Empleado();
        conHuella.setId(2L);
        conHuella.setHuellaDactilar(BiometricService.SIMULATED_TEMPLATE);
        when(empleadoRepository.findByActivoTrue()).thenReturn(List.of(sinHuella, conHuella));

        assertEquals(BiometricService.SIMULATED_TEMPLATE, service.createTemplate(BiometricService.SIMULATED_IMAGE));
        Optional<Empleado> resultado = service.identifyEmployee(BiometricService.SIMULATED_IMAGE);
        assertEquals(Optional.of(conHuella), resultado);
    }
}
