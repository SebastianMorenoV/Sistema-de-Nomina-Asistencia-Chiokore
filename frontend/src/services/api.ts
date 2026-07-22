import axios from 'axios';

type Rol = { id: number; nombre: string };
type TipoContrato = { id: number; nombre: string };
type Empleado = {
  id: number;
  nombre: string;
  contrasena?: string;
  rol: Rol;
  tipoContrato: TipoContrato;
  tarifaHora: number;
  activo: boolean;
  requiereApoyo: boolean;
  tipoSangre?: string;
  condicionesMedicas?: string;
  contactoEmergenciaNombre?: string;
  contactoEmergenciaTelefono?: string;
  urlAvatar?: string;
};

type AuthResponse = {
  accessToken: string;
  empleadoId: number;
  nombre: string;
  rol: string;
};

type KioskResponse = AuthResponse & {
  asistenciaId: number;
  fecha: string;
  entrada?: string;
  salida?: string;
  horasCalculadas?: number;
  movimiento: 'ENTRADA' | 'SALIDA';
};

type PublicKeyResponse = {
  issuer: string;
  keyId: string;
  algorithm: string;
  publicKeyPem: string;
  ephemeral: boolean;
};

type PagedResponse<T> = {
  content?: T[];
};

type EmpleadoFormPayload = Partial<Empleado> & {
  id?: number;
  nombre: string;
  rolId?: number;
  tipoContratoId?: number;
  tarifaHora?: number | string;
  activo?: boolean;
  requiereApoyo?: boolean;
  tipoSangre?: string;
  condicionesMedicas?: string;
  contactoEmergenciaNombre?: string;
  contactoEmergenciaTelefono?: string;
  urlAvatar?: string;
};

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080/api',
});

function unwrapListResponse<T>(data: T[] | PagedResponse<T>): T[] {
  return Array.isArray(data) ? data : data.content ?? [];
}

export async function getEmpleados() {
  const response = await api.get<Empleado[] | PagedResponse<Empleado>>('/empleados');
  return { data: unwrapListResponse(response.data) };
}

export async function saveEmpleado(data: EmpleadoFormPayload) {
  if (data.id) return api.put(`/empleados/${data.id}`, data);
  return api.post('/empleados', data);
}

export const enrollFingerprint = (empleadoId: number, imagenB64: string) => api.post(`/empleados/${empleadoId}/huella`, { imagenB64 });
export const kioskFingerprintLogin = (imagenB64: string) => api.post(`/asistencias/fingerprint`, { imagenB64 });

export async function deleteEmpleado(id: number) {
  return api.delete(`/empleados/${id}`);
}

export async function getHorarios() {
  return api.get('/horarios');
}

export async function assignHorario(payload: { diaSemana: string; horaInicio: string; oldEmpleadoId?: number | null; newEmpleadoId?: number | null }) {
  return api.post('/horarios/assign', payload);
}

export async function getNominaSemanal(query: string) {
  try {
    const response = await api.get(`/nominas/semanal${query}`);
    return response;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 404) {
      return { data: [] };
    }

    if (axios.isAxiosError(error) && error.response?.status === 500) {
      console.warn('El backend no expone aún el cálculo de nómina; se mostrará vacío.');
      return { data: [] };
    }

    throw error;
  }
}

export async function registrarAsistencia(empleadoId: number) {
  return api.post(`/asistencias/registrar/${empleadoId}`);
}

export async function getAllAsistencias(startDate?: string, endDate?: string) {
  const params = new URLSearchParams();
  if (startDate) params.append('startDate', startDate);
  if (endDate) params.append('endDate', endDate);
  
  const queryString = params.toString() ? `?${params.toString()}` : '';
  return api.get<any[]>(`/asistencias${queryString}`);
}

export async function getAsistenciaHoy(empleadoId: number) {
  try {
    const res = await api.get(`/asistencias/hoy/${empleadoId}`);
    return res.data;
  } catch (error) {
    if (axios.isAxiosError(error) && error.response?.status === 404) {
      return null;
    }
    throw error;
  }
}

export async function loginAdmin(nombre: string, contrasena: string) {
  return api.post<AuthResponse>('/auth/login', { nombre, contrasena });
}

export async function kioskLogin(empleadoId: number, movimiento?: 'ENTRADA' | 'SALIDA' | null) {
  return api.post<KioskResponse>('/auth/kiosk-login', { empleadoId, movimiento });
}

export async function getPublicKey() {
  return api.get<PublicKeyResponse>('/auth/public-key');
}

export async function getCandidatos() {
  return api.get<any>('/candidatos?size=100');
}

export async function saveCandidato(data: any) {
  if (data.id) {
    return api.put<any>(`/candidatos/${data.id}`, data);
  }
  return api.post<any>('/candidatos', data);
}

export async function deleteCandidato(id: number) {
  return api.delete(`/candidatos/${id}`);
}

export function getApiErrorMessage(error: unknown, fallback = 'Error al procesar la solicitud') {
  if (!axios.isAxiosError(error)) {
    return error instanceof Error && error.message ? error.message : fallback;
  }

  const data = error.response?.data as {
    message?: string;
    error?: string;
    detail?: string;
    title?: string;
    violations?: string[];
    fieldErrors?: string[];
  } | undefined;

  return data?.message
    || data?.error
    || data?.detail
    || data?.title
    || data?.violations?.[0]
    || data?.fieldErrors?.[0]
    || error.message
    || fallback;
}
