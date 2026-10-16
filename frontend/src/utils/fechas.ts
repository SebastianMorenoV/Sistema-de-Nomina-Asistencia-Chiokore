/** Fecha local de hoy en AAAA-MM-DD. toISOString() usa UTC y después de las 5 p.m. ya da la de mañana. */
export function hoyISO(): string {
  return aISO(new Date());
}

/** Primer y último día del mes actual en AAAA-MM-DD. */
export function rangoMesActual(): { inicio: string; fin: string } {
  const hoy = new Date();
  return {
    inicio: aISO(new Date(hoy.getFullYear(), hoy.getMonth(), 1)),
    fin: aISO(new Date(hoy.getFullYear(), hoy.getMonth() + 1, 0)),
  };
}

function aISO(fecha: Date): string {
  const mes = String(fecha.getMonth() + 1).padStart(2, '0');
  const dia = String(fecha.getDate()).padStart(2, '0');
  return `${fecha.getFullYear()}-${mes}-${dia}`;
}
