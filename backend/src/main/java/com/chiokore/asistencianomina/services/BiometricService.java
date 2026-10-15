package com.chiokore.asistencianomina.services;

import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BiometricService {

    public static final String SIMULATED_IMAGE = "SIMULATED_FINGERPRINT_DATA";
    public static final String SIMULATED_TEMPLATE = "SIMULATED_TEMPLATE";

    /** Puntaje mínimo de SourceAFIS para considerar coincidencia (FMR ~0.01%). */
    private static final double MATCH_THRESHOLD = 40;
    /** Resolución del lector DigitalPersona U.are.U 4500 (DPA-URU4500). */
    private static final double DPI = 512;

    private final EmpleadoRepository empleadoRepository;
    private final boolean simulacionHabilitada;

    public BiometricService(EmpleadoRepository empleadoRepository,
                            @Value("${app.huella.simulacion:false}") boolean simulacionHabilitada) {
        this.empleadoRepository = empleadoRepository;
        this.simulacionHabilitada = simulacionHabilitada;
    }

    /**
     * Convierte la imagen PNG (Base64 o Base64URL, con o sin prefijo data:) capturada por el
     * SDK de DigitalPersona en una plantilla SourceAFIS serializada en Base64.
     */
    public String createTemplate(String imagen) {
        if (SIMULATED_IMAGE.equals(imagen)) {
            validarSimulacion();
            return SIMULATED_TEMPLATE;
        }
        return Base64.getEncoder().encodeToString(toTemplate(imagen).toByteArray());
    }

    /**
     * Identifica al empleado activo cuya huella registrada coincide con la imagen escaneada.
     */
    public Optional<Empleado> identifyEmployee(String imagen) {
        List<Empleado> empleados = empleadoRepository.findByActivoTrue();

        if (SIMULATED_IMAGE.equals(imagen)) {
            validarSimulacion();
            return empleados.stream()
                    .filter(e -> SIMULATED_TEMPLATE.equals(e.getHuellaDactilar()))
                    .findFirst();
        }

        FingerprintMatcher matcher = new FingerprintMatcher(toTemplate(imagen));
        Empleado mejor = null;
        double mejorPuntaje = 0;

        for (Empleado emp : empleados) {
            String huella = emp.getHuellaDactilar();
            if (huella == null || huella.isBlank() || SIMULATED_TEMPLATE.equals(huella)) {
                continue;
            }
            try {
                FingerprintTemplate candidato = new FingerprintTemplate(Base64.getDecoder().decode(huella));
                double puntaje = matcher.match(candidato);
                if (puntaje >= MATCH_THRESHOLD && puntaje > mejorPuntaje) {
                    mejorPuntaje = puntaje;
                    mejor = emp;
                }
            } catch (Exception ex) {
                log.error("Plantilla de huella inválida para el empleado {}", emp.getId(), ex);
            }
        }

        if (mejor != null) {
            log.info("Huella identificada: empleado {} (puntaje {})", mejor.getId(), mejorPuntaje);
        }
        return Optional.ofNullable(mejor);
    }

    private void validarSimulacion() {
        if (!simulacionHabilitada) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La simulación de huella está deshabilitada.");
        }
    }

    private static FingerprintTemplate toTemplate(String imagen) {
        byte[] bytes;
        try {
            bytes = decodeImage(imagen);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La imagen de la huella no es Base64 válido.");
        }
        try {
            return new FingerprintTemplate(new FingerprintImage(bytes, new FingerprintImageOptions().dpi(DPI)));
        } catch (Exception ex) {
            log.error("Error al procesar la imagen de la huella", ex);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No se pudo procesar la imagen de la huella.");
        }
    }

    /** Acepta Base64 estándar o Base64URL (lo que entrega el WebSDK), con o sin relleno y prefijo data:. */
    static byte[] decodeImage(String imagen) {
        String datos = imagen.trim();
        int coma = datos.indexOf(',');
        if (datos.startsWith("data:") && coma >= 0) {
            datos = datos.substring(coma + 1);
        }
        datos = datos.replaceAll("\\s", "").replace('-', '+').replace('_', '/');
        int resto = datos.length() % 4;
        if (resto > 0) {
            datos = datos + "=".repeat(4 - resto);
        }
        return Base64.getDecoder().decode(datos);
    }
}
