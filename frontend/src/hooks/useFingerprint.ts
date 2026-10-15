import { useState, useEffect, useCallback, useRef } from 'react';
import { FingerprintReader, SampleFormat, type SamplesAcquired, type ErrorOccurred } from '@digitalpersona/devices';

export const CAPTURE_CANCELLED = 'CAPTURE_CANCELLED';

type PendingCapture = {
  reject: (reason: Error) => void;
  cleanup: () => void;
};

function extractPngBase64(samples: unknown): string {
  const list = typeof samples === 'string' ? JSON.parse(samples) : samples;
  const first = Array.isArray(list) ? list[0] : undefined;
  const raw: unknown = typeof first === 'string' ? first : first?.Data;
  if (typeof raw !== 'string' || raw.length === 0) {
    throw new Error('El lector no devolvió una imagen válida.');
  }
  const base64 = raw.replace(/-/g, '+').replace(/_/g, '/');
  return base64 + '='.repeat((4 - (base64.length % 4)) % 4);
}

export function useFingerprint() {
  const [reader] = useState(() => new FingerprintReader());
  const [isReady, setIsReady] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const pending = useRef<PendingCapture | null>(null);

  useEffect(() => {
    let active = true;
    reader.enumerateDevices()
      .then(devices => {
        if (!active) return;
        if (devices.length > 0) {
          setIsReady(true);
          setError(null);
        } else {
          setError('No se detectó ningún lector conectado.');
        }
      })
      .catch(err => {
        console.error("Error connecting to fingerprint reader:", err);
        if (active) setError('No se pudo comunicar con el lector. ¿Está instalado el cliente de DigitalPersona?');
      });

    return () => {
      active = false;
      pending.current?.reject(new Error(CAPTURE_CANCELLED));
      reader.off();
    };
  }, [reader]);

  const cancelCapture = useCallback(() => {
    pending.current?.reject(new Error(CAPTURE_CANCELLED));
  }, []);

  const captureFingerprint = useCallback((): Promise<string> => {
    if (!isReady) {
      return Promise.reject(new Error('Lector no está listo'));
    }
    pending.current?.reject(new Error(CAPTURE_CANCELLED));

    return new Promise((resolve, reject) => {
      let done = false;

      const cleanup = () => {
        reader.off('SamplesAcquired', onSamplesAcquired);
        reader.off('ErrorOccurred', onError);
        if (pending.current === entry) pending.current = null;
        reader.stopAcquisition().catch(() => { });
      };

      const finish = (fn: () => void) => {
        if (done) return;
        done = true;
        cleanup();
        fn();
      };

      const onSamplesAcquired = (event: SamplesAcquired) => {
        finish(() => {
          try {
            resolve(extractPngBase64(event.samples));
          } catch (e) {
            reject(e instanceof Error ? e : new Error(String(e)));
          }
        });
      };

      const onError = (event: ErrorOccurred) => {
        finish(() => reject(new Error(`Error del lector (${event?.error ?? 'desconocido'})`)));
      };

      const entry: PendingCapture = {
        reject: (reason) => finish(() => reject(reason)),
        cleanup,
      };
      pending.current = entry;

      reader.on('SamplesAcquired', onSamplesAcquired);
      reader.on('ErrorOccurred', onError);

      reader.startAcquisition(SampleFormat.PngImage)
        .catch(err => finish(() => reject(err instanceof Error ? err : new Error(String(err)))));
    });
  }, [reader, isReady]);

  return { isReady, error, captureFingerprint, cancelCapture };
}
