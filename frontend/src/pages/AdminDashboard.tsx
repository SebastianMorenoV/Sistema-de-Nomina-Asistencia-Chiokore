import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Users, CalendarDays, FileSpreadsheet, LogOut, HeartPulse, ChevronDown, ChevronUp, UserPlus, Printer, Trash2, Edit, Send, Search, Clock, Download } from 'lucide-react';
import * as XLSX from 'xlsx';
import axios from 'axios';
import ReceiptTemplate from '../components/ReceiptTemplate';
import EmployeeModal from '../components/EmployeeModal';

const API_URL = 'http://localhost:8080/api';

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState('nomina');
  const [expandedEmp, setExpandedEmp] = useState<number | null>(null);
  const [selectedReceiptEmp, setSelectedReceiptEmp] = useState<any>(null);
  const [selectedCandidato, setSelectedCandidato] = useState<any>(null);
  
  // CRUD State
  const [mockEmpleados, setMockEmpleados] = useState<any[]>([]);
  const [asistenciasNomina, setAsistenciasNomina] = useState<any[]>([]);
  const [horarios, setHorarios] = useState<any[]>([]);
  const [modalEmp, setModalEmp] = useState<{data: any, isNew: boolean} | null>(null);
  const [semana, setSemana] = useState<string>('');

  const dias = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO'];
  const diasFechas = ['2', '3', '4', '5', '6', '7'];
  
  const turnos = [
    { id: 't1', nombre: '9:00 A 1:00 PM', tipo: 'Pagado', cupos: 3 },
    { id: 'v1', nombre: 'VOLUNTARIO', tipo: 'Voluntario', cupos: 1 },
    { id: 't2', nombre: '3:00 A 6:00 PM', tipo: 'Pagado', cupos: 3 },
    { id: 'v2', nombre: 'VOLUNTARIO', tipo: 'Voluntario', cupos: 1 }
  ];

  const getWeekRange = (wStr: string) => {
    if (!wStr) return '';
    const [yearStr, weekStr] = wStr.split('-W');
    const y = parseInt(yearStr);
    const w = parseInt(weekStr);
    const simple = new Date(y, 0, 1 + (w - 1) * 7);
    const dow = simple.getDay();
    const ISOweekStart = simple;
    if (dow <= 4)
        ISOweekStart.setDate(simple.getDate() - simple.getDay() + 1);
    else
        ISOweekStart.setDate(simple.getDate() + 8 - simple.getDay());
    
    const start = new Date(ISOweekStart);
    const end = new Date(start);
    end.setDate(start.getDate() + 6);
    
    return `?fechaInicio=${start.toISOString().split('T')[0]}&fechaFin=${end.toISOString().split('T')[0]}`;
  };

  useEffect(() => {
    fetchData();
  }, [semana]);

  const fetchData = async () => {
    try {
      const resEmp = await axios.get(`${API_URL}/empleados`);
      setMockEmpleados(Array.isArray(resEmp.data) ? resEmp.data : []);
      
      const resNom = await axios.get(`${API_URL}/nominas/semanal${getWeekRange(semana)}`);
      setAsistenciasNomina(Array.isArray(resNom.data) ? resNom.data : []);

      const resHor = await axios.get(`${API_URL}/horarios`);
      setHorarios(Array.isArray(resHor.data) ? resHor.data : []);
    } catch (err) {
      console.error("Error fetching data", err);
      setMockEmpleados([]);
      setAsistenciasNomina([]);
      setHorarios([]);
    }
  };

  const handleSaveEmp = async (data: any) => {
    try {
      const payload = {
        ...data,
        rol: { id: data.rolId },
        tipoContrato: { id: data.tipoContratoId }
      };
      
      if (modalEmp?.isNew) {
        await axios.post(`${API_URL}/empleados`, payload);
      } else {
        await axios.put(`${API_URL}/empleados/${data.id}`, payload);
      }
      fetchData();
      setModalEmp(null);
    } catch(err) {
      alert("Error al guardar empleado");
    }
  };

  const handleDelete = async (id: number) => {
    if(confirm('¿Seguro que deseas eliminar lógicamente a este empleado?')) {
      try {
        await axios.delete(`${API_URL}/empleados/${id}`);
        fetchData();
      } catch(err) {
        alert("Error al eliminar");
      }
    }
  };

  const handleExportExcel = () => {
    // Preparar datos para Excel a partir de asistenciasNomina
    const exportData = asistenciasNomina.map(r => ({
      "Empleado": r.emp,
      "Lunes Entrada": (r as any).lu.e, "Lunes Salida": (r as any).lu.s,
      "Martes Entrada": (r as any).ma.e, "Martes Salida": (r as any).ma.s,
      "Miercoles Entrada": (r as any).mi.e, "Miercoles Salida": (r as any).mi.s,
      "Jueves Entrada": (r as any).ju.e, "Jueves Salida": (r as any).ju.s,
      "Viernes Entrada": (r as any).vi.e, "Viernes Salida": (r as any).vi.s,
      "Sabado Entrada": (r as any).sa.e, "Sabado Salida": (r as any).sa.s,
      "Total Horas": r.horas
    }));

    const worksheet = XLSX.utils.json_to_sheet(exportData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, "Sábana Nómina");
    XLSX.writeFile(workbook, "Sabana_Nomina_Chiokore.xlsx");
  };

  const renderEmpleados = () => (
    <div className="print:hidden">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-4xl font-bold text-stone-800">Catálogo de Personal</h1>
        <button onClick={() => setModalEmp({data: null, isNew: true})} className="bg-orange-500 hover:bg-orange-600 text-white px-6 py-3 rounded-lg font-bold transition-colors flex items-center gap-2">
          <UserPlus size={20}/> Nuevo Empleado
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-stone-200 overflow-hidden">
        <table className="w-full text-left">
          <thead className="bg-stone-50 border-b border-stone-200 text-stone-600">
            <tr>
              <th className="p-4 font-semibold">Nombre</th>
              <th className="p-4 font-semibold">Rol</th>
              <th className="p-4 font-semibold">Contrato</th>
              <th className="p-4 font-semibold text-center">Apoyo Visual</th>
              <th className="p-4 font-semibold text-right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {mockEmpleados.map((emp) => (
              <React.Fragment key={emp.id}>
                <tr className={`border-b border-stone-100 hover:bg-stone-50 transition-colors ${expandedEmp === emp.id ? 'bg-orange-50' : ''}`}>
                  <td className="p-4 font-medium text-stone-800 text-lg">{emp.nombre}</td>
                  <td className="p-4 text-stone-600"><span className="px-3 py-1 bg-blue-100 text-blue-700 rounded-md text-sm font-bold">{emp.rol?.nombre || 'Sin Rol'}</span></td>
                  <td className="p-4 text-stone-600">{emp.tipoContrato?.nombre || 'Sin Contrato'}</td>
                  <td className="p-4 text-center">
                    {emp.apoyo && <span className="inline-block px-3 py-1 bg-amber-100 text-amber-700 rounded-full text-xs font-bold">Requiere Tutor</span>}
                  </td>
                  <td className="p-4 text-right flex justify-end gap-2">
                    <button onClick={() => setModalEmp({data: emp, isNew: false})} className="p-2 text-stone-400 hover:text-blue-500"><Edit size={20}/></button>
                    <button onClick={() => handleDelete(emp.id)} className="p-2 text-stone-400 hover:text-red-500"><Trash2 size={20}/></button>
                    <button 
                      onClick={() => setExpandedEmp(expandedEmp === emp.id ? null : emp.id)}
                      className="text-stone-500 hover:text-orange-500 transition-colors p-2"
                    >
                      {expandedEmp === emp.id ? <ChevronUp size={24} /> : <ChevronDown size={24} />}
                    </button>
                  </td>
                </tr>
                {expandedEmp === emp.id && (
                  <tr className="bg-orange-50/50 border-b border-stone-200">
                    <td colSpan={5} className="p-6">
                      <div className="flex items-start gap-8">
                        <div className="flex items-center gap-4 bg-white p-4 rounded-xl shadow-sm border border-orange-100 flex-1">
                          <HeartPulse className="text-red-500 w-12 h-12" />
                          <div>
                            <p className="text-sm text-stone-500 font-bold uppercase tracking-wider">Ficha Médica</p>
                            <p className="text-stone-800 mt-1"><strong>Sangre:</strong> {emp.tipoSangre}</p>
                            <p className="text-stone-800"><strong>Condiciones:</strong> {emp.condicionesMedicas}</p>
                          </div>
                        </div>
                        <div className="bg-white p-4 rounded-xl shadow-sm border border-orange-100 flex-1">
                          <p className="text-sm text-stone-500 font-bold uppercase tracking-wider mb-1">Emergencia</p>
                          <p className="text-stone-800">{emp.contactoEmergenciaNombre}</p>
                        </div>
                        <div className="bg-white p-4 rounded-xl shadow-sm border border-orange-100 flex-1">
                          <p className="text-sm text-stone-500 font-bold uppercase tracking-wider mb-1">Nómina Base</p>
                          <p className="text-stone-800 text-2xl font-black text-emerald-600">${emp.tarifaHora}<span className="text-sm text-stone-500">/hr</span></p>
                        </div>
                      </div>
                    </td>
                  </tr>
                )}
              </React.Fragment>
            ))}
          </tbody>
        </table>
      </div>
      {modalEmp && <EmployeeModal emp={modalEmp.data} onSave={handleSaveEmp} onClose={() => setModalEmp(null)} />}
    </div>
  );

  const getAssignedEmployeeId = (dia: string, turnoId: string, slotIndex: number) => {
    let horaInicio = '';
    if (turnoId === 't1' || turnoId === 'v1') horaInicio = '09:00';
    if (turnoId === 't2' || turnoId === 'v2') horaInicio = '15:00';

    const assigned = horarios.filter(h => {
       if (!h.diaSemana || !h.horaInicio) return false;
       const isSameDay = h.diaSemana === dia;
       const isSameTime = h.horaInicio.startsWith(horaInicio);
       const emp = mockEmpleados.find(e => e.id === h.empleadoId);
       if (!emp) return false;
       const isVoluntario = emp.tipoContrato?.nombre === 'Voluntariado';
       const isTurnoVoluntario = turnoId.startsWith('v');
       return isSameDay && isSameTime && (isVoluntario === isTurnoVoluntario);
    });

    return assigned[slotIndex]?.empleadoId || "";
  };

  const handleAssignShift = async (dia: string, turnoId: string, oldEmpId: string, newEmpId: string) => {
    try {
      let horaInicio = '';
      if (turnoId === 't1' || turnoId === 'v1') horaInicio = '09:00';
      if (turnoId === 't2' || turnoId === 'v2') horaInicio = '15:00';
      
      await axios.post(`${API_URL}/horarios/assign`, {
        diaSemana: dia,
        horaInicio: horaInicio,
        oldEmpleadoId: oldEmpId ? parseInt(oldEmpId) : null,
        newEmpleadoId: newEmpId ? parseInt(newEmpId) : null
      });
      fetchData(); // Recargar matriz
    } catch (err) {
      alert("Error al asignar el horario");
    }
  };

  const renderHorarios = () => (
    <div className="print:hidden">
      <div className="flex justify-between items-center mb-8">
        <div>
          <h1 className="text-4xl font-bold text-stone-800">Matriz de Horarios</h1>
          <p className="text-stone-500 mt-2">Asignación de turnos por día de la semana.</p>
        </div>
        <button className="bg-stone-800 text-white px-6 py-3 rounded-lg font-bold flex items-center gap-2">
          <Clock size={20} /> Configurar Turnos Base
        </button>
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-stone-200 overflow-x-auto">
        <table className="w-full text-center border-collapse min-w-[1000px]">
          <thead className="bg-stone-50 border-b-2 border-stone-800 text-stone-800 uppercase font-black text-sm">
            <tr>
              <th className="p-4 border-r border-stone-200 w-48">Turno</th>
              {dias.map(dia => (
                <th key={dia} className="p-4 border-r border-stone-200">{dia}</th>
              ))}
            </tr>
          </thead>
          <tbody>
            {turnos.map((turno) => (
              <React.Fragment key={turno.id}>
                {Array.from({length: turno.cupos}).map((_, i) => (
                  <tr key={`${turno.id}-${i}`} className="border-b border-stone-200">
                    {i === 0 && (
                      <td rowSpan={turno.cupos} className="p-4 font-bold border-r border-stone-200 bg-stone-50 text-stone-700">
                        {turno.nombre}
                      </td>
                    )}
                    {dias.map(dia => (
                      <td key={`${dia}-${turno.id}-${i}`} className="p-2 border-r border-stone-200">
                        <select 
                          className={`w-full p-2 text-sm font-bold border-none outline-none rounded ${turno.tipo === 'Voluntario' ? 'bg-purple-50 text-purple-700' : 'bg-blue-50 text-blue-700'}`}
                          value={getAssignedEmployeeId(dia, turno.id, i)}
                          onChange={(e) => {
                             const oldEmpId = getAssignedEmployeeId(dia, turno.id, i);
                             handleAssignShift(dia, turno.id, oldEmpId, e.target.value);
                          }}
                        >
                          <option value="">- Vacío -</option>
                          {mockEmpleados.filter(e => turno.tipo === 'Voluntario' ? e.tipoContrato?.nombre === 'Voluntariado' : e.tipoContrato?.nombre === 'Pagado').map(emp => (
                            <option key={emp.id} value={emp.id}>{emp.nombre}</option>
                          ))}
                        </select>
                      </td>
                    ))}
                  </tr>
                ))}
              </React.Fragment>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );

  const renderNomina = () => (
    <>
      <div className="flex justify-between items-center mb-8 print:hidden">
        <div>
          <h1 className="text-4xl font-bold text-stone-800">Cálculo de Nómina</h1>
          <p className="text-stone-500 mt-2">Visión semanal de Entradas y Salidas.</p>
        </div>
        <div className="flex gap-4 items-end">
          <div className="flex flex-col">
            <label className="text-xs font-bold text-stone-500 uppercase mb-1 tracking-wider">Semana</label>
            <input 
              type="week" 
              className="border-2 border-stone-200 focus:border-orange-500 p-3 rounded-lg text-sm font-bold text-stone-800 outline-none w-48"
              value={semana}
              onChange={(e) => setSemana(e.target.value)}
            />
          </div>
          <div className="flex flex-col">
             <label className="text-xs font-bold text-stone-500 uppercase mb-1 tracking-wider">Seleccionar Empleado (Para Ticket)</label>
             <select 
              className="border-2 border-stone-200 focus:border-orange-500 p-3 rounded-lg text-sm font-bold text-stone-800 outline-none w-64"
              onChange={(e) => {
                const emp = mockEmpleados.find(m => m.id === parseInt(e.target.value));
                if(emp) {
                  setSelectedReceiptEmp({
                    nombre: emp.nombre,
                    fechaInicio: "02/02/2026", fechaFin: "07/02/2026",
                    horas: "12 hrs", tarifa: emp.tarifa, 
                    total: (parseFloat(emp.tarifa) * 12).toFixed(2),
                    fechaEmision: "08/02/2026"
                  });
                } else {
                  setSelectedReceiptEmp(null);
                }
              }}
             >
              <option value="">- Todos (Ver Tabla) -</option>
              {(Array.isArray(mockEmpleados) ? mockEmpleados : []).filter(e => e.tipoContrato?.nombre === 'Pagado').map(e => (
                <option key={e.id} value={e.id}>{e.nombre}</option>
              ))}
            </select>
          </div>
          <button onClick={() => alert("Simulando envío al módulo de Gastos...")} className="bg-indigo-600 hover:bg-indigo-700 text-white px-6 py-3 rounded-lg font-bold transition-colors flex items-center gap-2">
            <Send size={20}/> Enviar a Gastos
          </button>
          <button 
            disabled={!selectedReceiptEmp}
            onClick={() => window.print()} 
            className={`px-6 py-3 rounded-lg font-bold transition-colors flex items-center gap-2 ${selectedReceiptEmp ? 'bg-stone-800 hover:bg-stone-900 text-white shadow-xl' : 'bg-stone-200 text-stone-400 cursor-not-allowed'}`}
          >
            <Printer size={20}/> Imprimir Recibo de Nómina
          </button>
          <button 
            onClick={handleExportExcel}
            className="bg-emerald-600 hover:bg-emerald-700 text-white px-6 py-3 rounded-lg font-bold transition-colors flex items-center gap-2 shadow-xl"
          >
            <Download size={20}/> Exportar Sábana (Excel)
          </button>
        </div>
      </div>

      {/* Sábana Semanal (Calendario) */}
      <div className="bg-white rounded-xl shadow-sm border border-stone-200 overflow-x-auto print:hidden">
        <table className="w-full text-center border-collapse min-w-[1200px] text-xs">
          <thead>
            {/* Header Fechas */}
            <tr className="bg-stone-50">
              <th className="p-2 border border-stone-300 bg-yellow-200 text-left font-black" colSpan={1}>
                {semana ? `Semana: ${semana}` : 'SEMANA ACTUAL'}
              </th>
              {diasFechas.map(num => (
                <th key={num} colSpan={2} className="p-1 border border-stone-300 font-bold text-stone-600">{num}</th>
              ))}
              <th className="border border-stone-300"></th>
            </tr>
            {/* Header Dias */}
            <tr className="bg-stone-50">
              <th className="border border-stone-300"></th>
              {dias.map(dia => (
                <th key={dia} colSpan={2} className="p-1 border border-stone-300 text-stone-600 font-bold uppercase">{dia}</th>
              ))}
              <th className="p-2 border border-stone-300 text-stone-800 font-black">TOTAL HORAS</th>
            </tr>
            {/* Header Entrada/Salida */}
            <tr className="bg-stone-50">
              <th className="p-2 border border-stone-300 text-left font-bold text-stone-600">HORA<br/>EMPLEADOS</th>
              {dias.map(dia => (
                <React.Fragment key={`${dia}-es`}>
                  <th className="p-1 border border-stone-300 text-stone-500">ENTRADA</th>
                  <th className="p-1 border border-stone-300 text-stone-500">SALIDA</th>
                </React.Fragment>
              ))}
              <th className="border border-stone-300 text-stone-500 font-bold">SEMANAL</th>
            </tr>
          </thead>
          <tbody>
            {asistenciasNomina.map((r, i) => (
              <tr key={i} className="hover:bg-blue-50/50">
                <td className="p-2 border border-stone-300 font-bold text-left text-stone-800">{r.emp}</td>
                {['lu', 'ma', 'mi', 'ju', 'vi', 'sa'].map((d: any) => (
                  <React.Fragment key={d}>
                    <td className={`p-1 border border-stone-300 ${(r as any)[d].e ? 'bg-orange-100' : ''}`}>{(r as any)[d].e}</td>
                    <td className={`p-1 border border-stone-300 ${(r as any)[d].s ? 'bg-orange-100' : ''}`}>{(r as any)[d].s}</td>
                  </React.Fragment>
                ))}
                <td className="p-2 border border-stone-300 font-bold bg-green-100 text-green-800">{r.horas}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      {/* Recibo físico oculto (Ticket) */}
      <div className="hidden print:block">
        <ReceiptTemplate data={selectedReceiptEmp} />
      </div>
    </>
  );

  const renderCandidatos = () => (
    <div className="print:hidden">
      <div>
        <h1 className="text-4xl font-bold text-stone-800">Lista de Espera</h1>
        <p className="text-stone-500 mt-2">Personal interesado en unirse a Chiokore Bazar.</p>
      </div>
      <div className="mt-8 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {[
          { id: 1, nombre: 'Laura Gómez', tipo: 'Voluntariado', fechaRegistro: 'Hace 2 días', notas: 'Disponibilidad por las mañanas.' },
          { id: 2, nombre: 'Pedro Pascal', tipo: 'Pagado', fechaRegistro: 'Hace 1 semana', notas: 'Experiencia previa en ventas y caja.' }
        ].map((candidato) => (
          <div key={candidato.id} className="bg-white p-6 rounded-xl shadow-sm border border-stone-200 flex flex-col items-center text-center hover:shadow-md transition-shadow relative">
            <div className="absolute top-4 right-4 bg-yellow-100 text-yellow-800 text-xs font-black px-3 py-1 rounded-full">EN ESPERA</div>
            <div className="w-16 h-16 bg-stone-100 rounded-full flex items-center justify-center mb-4 mt-2">
              <UserPlus size={32} className="text-orange-500" />
            </div>
            <h3 className="font-bold text-stone-800 text-lg">{candidato.nombre}</h3>
            <p className="text-sm font-bold mt-1 text-indigo-600 uppercase">{candidato.tipo}</p>
            <p className="text-xs text-stone-400 mt-4">Registrado: {candidato.fechaRegistro}</p>
            <button onClick={() => setSelectedCandidato(candidato)} className="mt-4 w-full py-2 bg-stone-50 hover:bg-orange-50 text-stone-600 hover:text-orange-600 font-bold border border-stone-200 hover:border-orange-200 rounded transition-colors">
              Ver Detalles
            </button>
          </div>
        ))}
      </div>

      {/* Modal Candidato */}
      {selectedCandidato && (
        <div className="fixed inset-0 bg-stone-900/50 flex items-center justify-center p-4 z-50">
          <div className="bg-white rounded-2xl w-full max-w-md overflow-hidden shadow-2xl relative">
            <button onClick={() => setSelectedCandidato(null)} className="absolute top-4 right-4 text-stone-400 hover:text-stone-600"><Trash2 size={24} /></button>
            <div className="p-8 text-center">
              <div className="w-20 h-20 bg-stone-100 rounded-full flex items-center justify-center mx-auto mb-4">
                <UserPlus size={40} className="text-orange-500" />
              </div>
              <h2 className="text-2xl font-black text-stone-800">{selectedCandidato.nombre}</h2>
              <p className="text-sm font-bold mt-1 text-indigo-600 uppercase">{selectedCandidato.tipo}</p>
              
              <div className="mt-6 text-left bg-stone-50 p-4 rounded-lg">
                <p className="text-xs font-bold text-stone-500 uppercase tracking-wider mb-2">Detalles Adicionales</p>
                <p className="text-stone-800 text-sm"><strong>Registro:</strong> {selectedCandidato.fechaRegistro}</p>
                <p className="text-stone-800 text-sm mt-2"><strong>Notas:</strong> {selectedCandidato.notas}</p>
              </div>

              <div className="mt-8 flex gap-4">
                <button onClick={() => alert("Simulando promover a empleado...")} className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white py-3 rounded-lg font-bold">Aceptar y Contratar</button>
                <button onClick={() => setSelectedCandidato(null)} className="flex-1 bg-stone-200 hover:bg-stone-300 text-stone-800 py-3 rounded-lg font-bold">Cerrar</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );

  return (
    <div className="flex h-screen bg-stone-100 font-sans print:bg-white print:h-auto">
      <div className="w-72 bg-stone-900 text-white flex flex-col print:hidden">
        <div className="p-8">
          <h2 className="text-3xl font-black text-orange-500">Chiokore</h2>
          <p className="text-stone-400 font-bold text-sm tracking-widest mt-1">ADMINISTRACIÓN</p>
        </div>
        <nav className="flex-1 mt-6">
          <button onClick={() => setActiveTab('empleados')} className={`flex items-center gap-4 w-full p-5 transition-colors ${activeTab === 'empleados' ? 'bg-stone-800 border-l-4 border-orange-500 text-white' : 'border-l-4 border-transparent text-stone-400 hover:bg-stone-800/50 hover:text-stone-200'}`}>
            <Users size={24} /> Catálogo de Personal
          </button>
          <button onClick={() => setActiveTab('horarios')} className={`flex items-center gap-4 w-full p-5 transition-colors ${activeTab === 'horarios' ? 'bg-stone-800 border-l-4 border-orange-500 text-white' : 'border-l-4 border-transparent text-stone-400 hover:bg-stone-800/50 hover:text-stone-200'}`}>
            <CalendarDays size={24} /> Horarios por Turno
          </button>
          <button onClick={() => setActiveTab('nomina')} className={`flex items-center gap-4 w-full p-5 transition-colors ${activeTab === 'nomina' ? 'bg-stone-800 border-l-4 border-orange-500 text-white' : 'border-l-4 border-transparent text-stone-400 hover:bg-stone-800/50 hover:text-stone-200'}`}>
            <FileSpreadsheet size={24} /> Nóminas y Recibos
          </button>
          <button onClick={() => setActiveTab('candidatos')} className={`flex items-center gap-4 w-full p-5 transition-colors ${activeTab === 'candidatos' ? 'bg-stone-800 border-l-4 border-orange-500 text-white' : 'border-l-4 border-transparent text-stone-400 hover:bg-stone-800/50 hover:text-stone-200'}`}>
            <UserPlus size={24} /> Lista de Espera
          </button>
        </nav>
        <div className="p-6 border-t border-stone-800">
          <Link to="/" className="flex items-center gap-3 w-full p-4 hover:bg-stone-800 text-stone-400 hover:text-white rounded-xl transition-colors font-bold">
            <LogOut size={20} /> Volver al Checador
          </Link>
        </div>
      </div>

      <main className="flex-1 p-12 overflow-y-auto print:p-0">
          {activeTab === 'empleados' && renderEmpleados()}
          {activeTab === 'horarios' && renderHorarios()}
          {activeTab === 'nomina' && renderNomina()}
          {activeTab === 'candidatos' && renderCandidatos()}
        </main>
    </div>
  );
}