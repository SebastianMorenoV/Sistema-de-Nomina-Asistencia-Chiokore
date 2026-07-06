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

type Asistencia = {
  id: number;
  empleadoId: number;
  fecha: string;
  entrada?: string;
  salida?: string;
  horasCalculadas?: number;
};

type Horario = {
  id: number;
  diaSemana: string;
  horaInicio: string;
  empleadoId: number;
};

type NominaRow = {
  emp: string;
  lu: { e: string; s: string };
  ma: { e: string; s: string };
  mi: { e: string; s: string };
  ju: { e: string; s: string };
  vi: { e: string; s: string };
  sa: { e: string; s: string };
  horas: number;
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

const STORAGE_KEYS = {
  empleados: 'chiokore.mock.empleados',
  asistencias: 'chiokore.mock.asistencias',
  horarios: 'chiokore.mock.horarios',
};

const USE_MOCK_API = import.meta.env.VITE_USE_REAL_API !== 'true';

const api = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8081/api',
});

const seedRoles = {
  admin: { id: 2, nombre: 'ADMINISTRADOR' },
  trabajador: { id: 1, nombre: 'TRABAJADOR' },
};

const seedContratos = {
  pagado: { id: 1, nombre: 'PAGADO' },
  voluntario: { id: 2, nombre: 'VOLUNTARIO' },
};

const initialEmpleados: Empleado[] = [
  {
    id: 1,
    nombre: 'admin',
    contrasena: '1234',
    rol: seedRoles.admin,
    tipoContrato: seedContratos.pagado,
    tarifaHora: 0,
    activo: true,
    requiereApoyo: false,
    urlAvatar: 'https://ui-avatars.com/api/?name=Admin&size=200&background=1F2937&color=F9FAFB',
  },
  {
    id: 2,
    nombre: 'Sofía Martínez',
    contrasena: '1234',
    rol: seedRoles.trabajador,
    tipoContrato: seedContratos.pagado,
    tarifaHora: 50,
    activo: true,
    requiereApoyo: false,
    tipoSangre: 'O+',
    condicionesMedicas: '',
    contactoEmergenciaNombre: 'Laura Martínez',
    urlAvatar: 'https://ui-avatars.com/api/?name=Sofia+Martinez&size=200&background=FDE68A&color=92400E',
  },
  {
    id: 3,
    nombre: 'Mateo López',
    contrasena: '1234',
    rol: seedRoles.trabajador,
    tipoContrato: seedContratos.voluntario,
    tarifaHora: 0,
    activo: true,
    requiereApoyo: true,
    tipoSangre: 'A+',
    condicionesMedicas: 'Neurodivergente',
    contactoEmergenciaNombre: 'Ana López',
    urlAvatar: 'https://ui-avatars.com/api/?name=Mateo+Lopez&size=200&background=A7F3D0&color=065F46',
  },
  {
    id: 4,
    nombre: 'Valentina Ruiz',
    contrasena: '1234',
    rol: seedRoles.trabajador,
    tipoContrato: seedContratos.pagado,
    tarifaHora: 60,
    activo: true,
    requiereApoyo: false,
    tipoSangre: 'B-',
    condicionesMedicas: '',
    contactoEmergenciaNombre: 'Carlos Ruiz',
    urlAvatar: 'https://ui-avatars.com/api/?name=Valentina+Ruiz&size=200&background=FECACA&color=991B1B',
  },
];

const initialHorarios: Horario[] = [
  { id: 1, diaSemana: 'LUNES', horaInicio: '09:00', empleadoId: 2 },
  { id: 2, diaSemana: 'MARTES', horaInicio: '09:00', empleadoId: 2 },
  { id: 3, diaSemana: 'MIERCOLES', horaInicio: '15:00', empleadoId: 4 },
  { id: 4, diaSemana: 'JUEVES', horaInicio: '15:00', empleadoId: 4 },
  { id: 5, diaSemana: 'VIERNES', horaInicio: '09:00', empleadoId: 3 },
];

function readStore<T>(key: string, fallback: T): T {
  const raw = localStorage.getItem(key);
  if (!raw) return fallback;
  try {
    return JSON.parse(raw) as T;
  } catch {
    return fallback;
  }
}

function writeStore<T>(key: string, value: T) {
  localStorage.setItem(key, JSON.stringify(value));
}

function ensureMockState() {
  if (!localStorage.getItem(STORAGE_KEYS.empleados)) {
    writeStore(STORAGE_KEYS.empleados, initialEmpleados);
  }
  if (!localStorage.getItem(STORAGE_KEYS.asistencias)) {
    writeStore<Asistencia[]>(STORAGE_KEYS.asistencias, []);
  }
  if (!localStorage.getItem(STORAGE_KEYS.horarios)) {
    writeStore(STORAGE_KEYS.horarios, initialHorarios);
  }
}

function clone<T>(value: T): T {
  return JSON.parse(JSON.stringify(value)) as T;
}

function nowIso() {
  return new Date().toISOString();
}

function todayIsoDate() {
  return new Date().toISOString().split('T')[0];
}

function base64Url(input: string) {
  return btoa(input)
    .replaceAll('+', '-')
    .replaceAll('/', '_')
    .replaceAll('=', '');
}

function buildMockToken(payload: Record<string, unknown>) {
  const header = base64Url(JSON.stringify({ alg: 'RS256', typ: 'JWT', kid: 'chiokore-nomina-v1' }));
  const body = base64Url(JSON.stringify({
    iss: 'chiokore-nomina-idp',
    iat: Math.floor(Date.now() / 1000),
    exp: Math.floor(Date.now() / 1000) + 86400,
    ...payload,
  }));
  return `${header}.${body}.mock-signature`;
}

function formatTime(value?: string) {
  if (!value) return '';
  return new Date(value).toLocaleTimeString('es-MX', { hour: '2-digit', minute: '2-digit' });
}

function getDayKey(date: Date) {
  return ['DOM', 'LU', 'MA', 'MI', 'JU', 'VI', 'SA'][date.getDay()];
}

function buildNominaRows(): NominaRow[] {
  const empleados = readStore<Empleado[]>(STORAGE_KEYS.empleados, clone(initialEmpleados));
  const asistencias = readStore<Asistencia[]>(STORAGE_KEYS.asistencias, []);

  return empleados
    .filter((empleado) => empleado.rol.nombre === 'TRABAJADOR')
    .map((empleado) => {
      const daily = {
        lu: { e: '', s: '' },
        ma: { e: '', s: '' },
        mi: { e: '', s: '' },
        ju: { e: '', s: '' },
        vi: { e: '', s: '' },
        sa: { e: '', s: '' },
      };

      let totalHoras = 0;
      for (const asistencia of asistencias.filter((item) => item.empleadoId === empleado.id)) {
        const fecha = new Date(`${asistencia.fecha}T12:00:00`);
        const dia = getDayKey(fecha);
        const map: Record<string, keyof typeof daily> = { LU: 'lu', MA: 'ma', MI: 'mi', JU: 'ju', VI: 'vi', SA: 'sa' };
        const key = map[dia];
        if (!key) continue;

        daily[key].e = formatTime(asistencia.entrada);
        daily[key].s = formatTime(asistencia.salida);
        totalHoras += asistencia.horasCalculadas || 0;
      }

      return {
        emp: empleado.nombre,
        ...daily,
        horas: Number(totalHoras.toFixed(2)),
      };
    });
}

function isMockEnabled() {
  return USE_MOCK_API;
}

async function mockDelay<T>(value: T): Promise<{ data: T }> {
  return Promise.resolve({ data: clone(value) });
}

export async function getEmpleados() {
  if (!isMockEnabled()) return api.get('/empleados');
  ensureMockState();
  return mockDelay(readStore<Empleado[]>(STORAGE_KEYS.empleados, clone(initialEmpleados)));
}

export async function saveEmpleado(data: EmpleadoFormPayload) {
  if (!isMockEnabled()) {
    if (data.id) return api.put(`/empleados/${data.id}`, data);
    return api.post('/empleados', data);
  }

  ensureMockState();
  const empleados = readStore<Empleado[]>(STORAGE_KEYS.empleados, clone(initialEmpleados));
  const nextId = Math.max(0, ...empleados.map((emp) => emp.id)) + 1;
  const payload: Empleado = {
    id: data.id || nextId,
    nombre: data.nombre,
    rol: data.rol?.nombre ? data.rol : { id: data.rolId ?? 1, nombre: data.rolId === 2 ? 'ADMINISTRADOR' : 'TRABAJADOR' },
    tipoContrato: data.tipoContrato?.nombre ? data.tipoContrato : { id: data.tipoContratoId ?? 1, nombre: data.tipoContratoId === 2 ? 'VOLUNTARIO' : 'PAGADO' },
    tarifaHora: Number(data.tarifaHora || 0),
    activo: data.activo !== false,
    requiereApoyo: Boolean(data.requiereApoyo),
    tipoSangre: data.tipoSangre || '',
    condicionesMedicas: data.condicionesMedicas || '',
    contactoEmergenciaNombre: data.contactoEmergenciaNombre || '',
    contactoEmergenciaTelefono: data.contactoEmergenciaTelefono || '',
    urlAvatar: data.urlAvatar || '',
  };

  const existingIndex = empleados.findIndex((emp) => emp.id === payload.id);
  if (existingIndex >= 0) {
    empleados[existingIndex] = { ...empleados[existingIndex], ...payload };
  } else {
    empleados.push(payload);
  }

  writeStore(STORAGE_KEYS.empleados, empleados);
  return mockDelay(payload);
}

export async function deleteEmpleado(id: number) {
  if (!isMockEnabled()) return api.delete(`/empleados/${id}`);
  ensureMockState();
  const empleados = readStore<Empleado[]>(STORAGE_KEYS.empleados, clone(initialEmpleados)).filter((emp) => emp.id !== id);
  writeStore(STORAGE_KEYS.empleados, empleados);
  return mockDelay({ ok: true });
}

export async function getHorarios() {
  if (!isMockEnabled()) return api.get('/horarios');
  ensureMockState();
  return mockDelay(readStore<Horario[]>(STORAGE_KEYS.horarios, clone(initialHorarios)));
}

export async function assignHorario(payload: { diaSemana: string; horaInicio: string; oldEmpleadoId?: number | null; newEmpleadoId?: number | null }) {
  if (!isMockEnabled()) return api.post('/horarios/assign', payload);
  ensureMockState();

  const horarios = readStore<Horario[]>(STORAGE_KEYS.horarios, clone(initialHorarios));
  const filtered = horarios.filter((item) => !(item.diaSemana === payload.diaSemana && item.horaInicio.startsWith(payload.horaInicio)));
  const nextId = Math.max(0, ...filtered.map((item) => item.id)) + 1;

  if (payload.newEmpleadoId) {
    filtered.push({
      id: nextId,
      diaSemana: payload.diaSemana,
      horaInicio: payload.horaInicio,
      empleadoId: payload.newEmpleadoId,
    });
  }

  writeStore(STORAGE_KEYS.horarios, filtered);
  return mockDelay({ ok: true });
}

export async function getNominaSemanal(query: string) {
  if (!isMockEnabled()) return api.get(`/nominas/semanal${query}`);
  ensureMockState();
  return mockDelay(buildNominaRows());
}

export async function registrarAsistencia(empleadoId: number) {
  if (!isMockEnabled()) return api.post(`/asistencias/registrar/${empleadoId}`);

  ensureMockState();
  const asistencias = readStore<Asistencia[]>(STORAGE_KEYS.asistencias, []);
  const hoy = todayIsoDate();
  const empleado = readStore<Empleado[]>(STORAGE_KEYS.empleados, clone(initialEmpleados)).find((item) => item.id === empleadoId);
  if (!empleado) {
    throw new Error('Empleado no encontrado');
  }

  if (empleado.rol.nombre === 'ADMINISTRADOR') {
    throw new Error('El acceso por kiosco no está permitido para administradores');
  }

  const actual = asistencias.find((item) => item.empleadoId === empleadoId && item.fecha === hoy);
  if (!actual) {
    const asistencia: Asistencia = {
      id: Math.max(0, ...asistencias.map((item) => item.id)) + 1,
      empleadoId,
      fecha: hoy,
      entrada: nowIso(),
    };
    asistencias.push(asistencia);
    writeStore(STORAGE_KEYS.asistencias, asistencias);
    return mockDelay({
      asistencia,
      movimiento: 'ENTRADA' as const,
      accessToken: buildMockToken({ empleadoId, nombre: empleado.nombre, rol: empleado.rol.nombre, kiosk: true }),
    });
  }

  if (!actual.salida) {
    actual.salida = nowIso();
    const entrada = actual.entrada ? new Date(actual.entrada).getTime() : Date.now();
    const salida = new Date(actual.salida).getTime();
    actual.horasCalculadas = Number(((salida - entrada) / 1000 / 60 / 60).toFixed(2));
    writeStore(STORAGE_KEYS.asistencias, asistencias);
    return mockDelay({
      asistencia: actual,
      movimiento: 'SALIDA' as const,
      accessToken: buildMockToken({ empleadoId, nombre: empleado.nombre, rol: empleado.rol.nombre, kiosk: true }),
    });
  }

  throw new Error('El empleado ya completó su turno de hoy');
}

export async function loginAdmin(nombre: string, contrasena: string) {
  if (!isMockEnabled()) return api.post('/auth/login', { nombre, contrasena });

  ensureMockState();
  const empleado = readStore<Empleado[]>(STORAGE_KEYS.empleados, clone(initialEmpleados)).find(
    (item) => item.nombre.toLowerCase() === nombre.toLowerCase() && item.contrasena === contrasena,
  );

  if (!empleado || empleado.rol.nombre !== 'ADMINISTRADOR') {
    throw new Error('Credenciales inválidas para el acceso de administrador');
  }

  const response: AuthResponse = {
    accessToken: buildMockToken({ empleadoId: empleado.id, nombre: empleado.nombre, rol: empleado.rol.nombre, kiosk: false }),
    empleadoId: empleado.id,
    nombre: empleado.nombre,
    rol: empleado.rol.nombre,
  };

  return mockDelay(response);
}

export async function kioskLogin(empleadoId: number) {
  const response = await registrarAsistencia(empleadoId);
  const asistencia = response.data.asistencia as Asistencia;
  const empleados = readStore<Empleado[]>(STORAGE_KEYS.empleados, clone(initialEmpleados));
  const empleado = empleados.find((item) => item.id === empleadoId)!;

  const kioskResponse: KioskResponse = {
    accessToken: response.data.accessToken,
    empleadoId: empleado.id,
    nombre: empleado.nombre,
    rol: empleado.rol.nombre,
    asistenciaId: asistencia.id,
    fecha: asistencia.fecha,
    entrada: asistencia.entrada,
    salida: asistencia.salida,
    horasCalculadas: asistencia.horasCalculadas,
    movimiento: response.data.movimiento,
  };

  return mockDelay(kioskResponse);
}

export async function getPublicKey() {
  if (!isMockEnabled()) return api.get('/auth/public-key');

  const response: PublicKeyResponse = {
    issuer: 'chiokore-nomina-idp',
    keyId: 'chiokore-nomina-v1',
    algorithm: 'RS256',
    publicKeyPem: '-----BEGIN PUBLIC KEY-----\nMOCK-CHI0K0RE-KEY\n-----END PUBLIC KEY-----',
    ephemeral: true,
  };

  return mockDelay(response);
}

export function isUsingMockApi() {
  return isMockEnabled();
}
