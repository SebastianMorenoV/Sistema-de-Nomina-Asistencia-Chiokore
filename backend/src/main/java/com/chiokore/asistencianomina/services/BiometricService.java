package com.chiokore.asistencianomina.services;

import com.chiokore.asistencianomina.domain.entities.Empleado;
import com.chiokore.asistencianomina.repositories.EmpleadoRepository;
import com.machinezoo.sourceafis.FingerprintImage;
import com.machinezoo.sourceafis.FingerprintImageOptions;
import com.machinezoo.sourceafis.FingerprintMatcher;
import com.machinezoo.sourceafis.FingerprintTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BiometricService {

    private final EmpleadoRepository empleadoRepository;

    /**
     * Converts a Base64 encoded PNG image (from DigitalPersona SDK) into a SourceAFIS template JSON string.
     */
    public static String createTemplateFromBase64Image(String base64Image) {
        if ("SIMULATED_FINGERPRINT_DATA".equals(base64Image)) {
            return "SIMULATED_TEMPLATE";
        }
        
        try {
            if (base64Image.contains(",")) {
                base64Image = base64Image.split(",")[1];
            }
            
            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            
            FingerprintImage fingerprintImage = new FingerprintImage(
                imageBytes, 
                new FingerprintImageOptions().dpi(500)
            );
            
            FingerprintTemplate template = new FingerprintTemplate(fingerprintImage);
            return template.toByteArray() != null ? Base64.getEncoder().encodeToString(template.toByteArray()) : null;
        } catch (Exception e) {
            log.error("Error creating fingerprint template", e);
            throw new RuntimeException("Error processing fingerprint image.");
        }
    }

    /**
     * Identifies an employee from a base64 scanned image.
     */
    public Optional<Empleado> identifyEmployee(String base64Image) {
        if ("SIMULATED_FINGERPRINT_DATA".equals(base64Image)) {
            // Simulación: retornar el primer empleado que tenga la huella simulada
            return empleadoRepository.findAll().stream()
                    .filter(e -> "SIMULATED_TEMPLATE".equals(e.getHuellaDactilar()) && e.getActivo())
                    .findFirst();
        }

        FingerprintTemplate probeTemplate;
        try {
            // Generate template from the scanned finger
            String scannedBase64Image = base64Image;
            if (scannedBase64Image.contains(",")) {
                scannedBase64Image = scannedBase64Image.split(",")[1];
            }
            byte[] imageBytes = Base64.getDecoder().decode(scannedBase64Image);
            FingerprintImage fingerprintImage = new FingerprintImage(imageBytes, new FingerprintImageOptions().dpi(500));
            FingerprintTemplate probe = new FingerprintTemplate(fingerprintImage);
            
            FingerprintMatcher matcher = new FingerprintMatcher(probe);

            // Fetch all active employees with enrolled fingerprints
            List<Empleado> employees = empleadoRepository.findByActivoTrue();
            
            Empleado bestMatch = null;
            double highScore = 0;

            for (Empleado emp : employees) {
                if (emp.getHuellaDactilar() != null && !emp.getHuellaDactilar().isEmpty()) {
                    try {
                        byte[] serializedTemplate = Base64.getDecoder().decode(emp.getHuellaDactilar());
                        FingerprintTemplate candidate = new FingerprintTemplate(serializedTemplate);
                        
                        double score = matcher.match(candidate);
                        // SourceAFIS score >= 40 is generally considered a strong match
                        if (score >= 40 && score > highScore) {
                            highScore = score;
                            bestMatch = emp;
                        }
                    } catch (Exception ex) {
                        log.error("Error matching template for employee " + emp.getId(), ex);
                    }
                }
            }

            if (bestMatch != null) {
                log.info("Matched fingerprint to employee: {} with score: {}", bestMatch.getNombre(), highScore);
                return Optional.of(bestMatch);
            }

            return Optional.empty();
        } catch (Exception e) {
            log.error("Error identifying fingerprint", e);
            throw new RuntimeException("Error in biometric identification.");
        }
    }
}
