import { useState, useEffect, useCallback } from 'react';
import { FingerprintReader, SampleFormat } from '@digitalpersona/devices';

export function useFingerprint() {
  const [reader] = useState(() => new FingerprintReader());
  const [isReady, setIsReady] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Initial check to see if we can connect to the local device
    reader.enumerateDevices()
      .then(devices => {
        if (devices.length > 0) {
          setIsReady(true);
        } else {
          setError('No se detectó ningún lector conectado.');
        }
      })
      .catch(err => {
        console.error("Error connecting to fingerprint reader:", err);
        setError('No se pudo comunicar con el lector. ¿Está instalado el HID ADC?');
      });

    return () => {
      // Clean up
      reader.off();
    };
  }, [reader]);

  const captureFingerprint = useCallback((): Promise<string> => {
    return new Promise((resolve, reject) => {
      if (!isReady) {
        reject(new Error("Lector no está listo"));
        return;
      }

      const onSamplesAcquired = (event: any) => {
        try {
          const rawData = event.samples[0]; // Base64url encoded
          
          // Cleanup listeners and stop acquisition after first successful read
          reader.off("SamplesAcquired", onSamplesAcquired);
          reader.stopAcquisition();
          
          resolve(rawData);
        } catch (e) {
          reject(e);
        }
      };

      const onError = (event: any) => {
        reader.off("SamplesAcquired", onSamplesAcquired);
        reader.off("ErrorOccurred", onError);
        reader.stopAcquisition();
        reject(event.error);
      };

      reader.on("SamplesAcquired", onSamplesAcquired);
      reader.on("ErrorOccurred", onError);

      // Start acquiring PNG images
      reader.startAcquisition(SampleFormat.PngImage)
        .catch(err => {
          reader.off("SamplesAcquired", onSamplesAcquired);
          reader.off("ErrorOccurred", onError);
          reject(err);
        });
    });
  }, [reader, isReady]);

  return { isReady, error, captureFingerprint };
}
