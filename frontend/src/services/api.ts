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
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8081/api',
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

export async function deleteEmpleado(id: number) {
  return api.delete(`/empleados/${id}`);
}

export async function getHorarios() {
  return api.get('/horarios');
}

export async function assignHorario(payload: { diaSemana: string; horaInicio: string; oldEmpleadoId?: number | null; newEmpleadoId?: number | null }) {
  return api.post('/horarios/assign', payload);
}

export async function registrarAsistencia(empleadoId: number) {
  return api.post(`/asistencias/registrar/${empleadoId}`);
}

export async function loginAdmin(nombre: string, contrasena: string) {
  return api.post<AuthResponse>('/auth/login', { nombre, contrasena });
}

export async function kioskLogin(empleadoId: number, movimiento?: 'ENTRADA' | 'SALIDA') {
  return api.post<KioskResponse>('/auth/kiosk-login', { empleadoId, movimiento });
}

export async function getPublicKey() {
  return api.get<PublicKeyResponse>('/auth/public-key');
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
