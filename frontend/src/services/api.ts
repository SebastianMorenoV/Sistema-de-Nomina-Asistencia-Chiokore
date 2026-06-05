import axios from 'axios';

const api = axios.create({
  baseURL: 'http://localhost:8080/api'
});

export const getEmpleados = () => api.get('/empleados');
export const registrarAsistencia = (data: any) => api.post('/asistencias/registrar', data);
