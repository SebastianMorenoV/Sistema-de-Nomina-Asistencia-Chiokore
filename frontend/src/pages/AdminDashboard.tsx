import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Users, CalendarDays, LogOut, HeartPulse, ChevronDown, ChevronUp, UserPlus, Trash2, Edit, Clock, ShieldCheck, Search, Fingerprint } from 'lucide-react';
import EmployeeModal from '../components/EmployeeModal';
import CandidatoModal from '../components/CandidatoModal';
import {
  assignHorario,
  deleteEmpleado,
  getEmpleados,
  getHorarios,
  saveEmpleado,
  getCandidatos,
  saveCandidato,
  deleteCandidato,
  enrollFingerprint,
  getAllAsistencias
} from '../services/api';
import { useFingerprint } from '../hooks/useFingerprint';

export default function AdminDashboard() {
  const [activeTab, setActiveTab] = useState('empleados');
  const [expandedEmp, setExpandedEmp] = useState<number | null>(null);
  const [selectedCandidato, setSelectedCandidato] = useState<any>(null);
  const [searchQuery, setSearchQuery] = useState('');
  const [candidatos, setCandidatos] = useState<any[]>([]);
  const { isReady: fpReady, error: fpError, captureFingerprint } = useFingerprint();
  const [isScanning, setIsScanning] = useState(false);
  const [asistStartDate, setAsistStartDate] = useState('');
  const [asistEndDate, setAsistEndDate] = useState('');
  
  // CRUD State
  const [mockEmpleados, setMockEmpleados] = useState<any[]>([]);
  const [horarios, setHorarios] = useState<any[]>([]);
  const [asistencias, setAsistencias] = useState<any[]>([]);
  const [modalEmp, setModalEmp] = useState<{data: any, isNew: boolean} | null>(null);
  const [modalCand, setModalCand] = useState<{data: any, isNew: boolean} | null>(null);
  const [payoutModalCand, setPayoutModalCand] = useState<any>(null);
  const [tempPayout, setTempPayout] = useState<string>('');

  const dias = ['LUNES', 'MARTES', 'MIERCOLES', 'JUEVES', 'VIERNES', 'SABADO'];
  const diasFechas = ['2', '3', '4', '5', '6', '7'];
  
  const turnos = [
    { id: 't1', nombre: '9:00 A 1:00 PM', tipo: 'Pagado', cupos: 3 },
    { id: 'v1', nombre: 'VOLUNTARIO', tipo: 'Voluntario', cupos: 1 },
    { id: 't2', nombre: '3:00 A 6:00 PM', tipo: 'Pagado', cupos: 3 },
    { id: 'v2', nombre: 'VOLUNTARIO', tipo: 'Voluntario', cupos: 1 }
  ];


  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const resEmp = await getEmpleados();
      setMockEmpleados(Array.isArray(resEmp.data) ? resEmp.data : []);
      
      const resCand = await getCandidatos();
      setCandidatos(Array.isArray(resCand.data?.content) ? resCand.data.content : (Array.isArray(resCand.data) ? resCand.data : []));

      const resHor = await getHorarios();
      setHorarios(Array.isArray(resHor.data) ? resHor.data : []);

      await fetchAsistencias();
    } catch (err) {
      console.error("Error fetching data", err);
      setMockEmpleados([]);
      setCandidatos([]);
      setHorarios([]);
      setAsistencias([]);
    }
  };

  const fetchAsistencias = async () => {
    try {
      const resAsist = await getAllAsistencias(asistStartDate, asistEndDate);
      setAsistencias(Array.isArray(resAsist.data) ? resAsist.data : []);
    } catch (err) {
      console.error("Error fetching asistencias", err);
      setAsistencias([]);
    }
  };

  useEffect(() => {
    fetchAsistencias();
  }, [asistStartDate, asistEndDate]);

  const handleSaveCand = async (data: any) => {
    try {
      const payload = {
        ...data,
        rol: { id: data.rolId },
        tipoContrato: { id: data.tipoContratoId }
      };
      await saveCandidato(payload);
      fetchData();
      setModalCand(null);
    } catch(err) {
      alert("Error al guardar candidato");
    }
  };

  const handleDeleteCand = async (id: number) => {
    if(confirm('¿Seguro que deseas rechazar y eliminar a este candidato?')) {
      try {
        await deleteCandidato(id);
        fetchData();
        setSelectedCandidato(null);
      } catch(err) {
        alert("Error al eliminar");
      }
    }
  };

  const handleEnrollFingerprint = async (empId: number, simulate = false) => {
    if (simulate) {
      if (!confirm('¿Usar lector simulado para enrolamiento de prueba?')) return;
      try {
        await enrollFingerprint(empId, "SIMULATED_FINGERPRINT_DATA");
        alert("¡Huella simulada registrada con éxito!");
        fetchData();
      } catch(e) {
        alert("Error en simulación");
      }
      return;
    }

    if (!fpReady) {
      alert(fpError || "El lector de huellas no está listo. Asegúrate de tener el cliente instalado.");
      return;
    }
    
    setIsScanning(true);
    try {
      const base64Image = await captureFingerprint();
      await enrollFingerprint(empId, base64Image);
      alert("¡Huella registrada con éxito!");
      fetchData();
    } catch (error: any) {
      console.error("Error al registrar huella:", error);
      alert("Error al capturar la huella: " + error.message);
    } finally {
      setIsScanning(false);
    }
  };

  const handleHireCandidato = async (cand: any, tarifa: number) => {
    try {
      const empPayload = {
        nombre: cand.nombre,
        urlAvatar: cand.urlAvatar,
        rolId: cand.rol?.id,
        tipoContratoId: cand.tipoContrato?.id,
        tipoSangre: cand.tipoSangre,
        condicionesMedicas: cand.condicionesMedicas,
        contactoEmergenciaNombre: cand.contactoEmergenciaNombre,
        contactoEmergenciaTelefono: cand.contactoEmergenciaTelefono,
        requiereApoyo: cand.requiereApoyo,
        tarifaHora: tarifa
      };
      await saveEmpleado(empPayload);
      await deleteCandidato(cand.id);
      
      fetchData();
      setSelectedCandidato(null);
      setPayoutModalCand(null);
      setActiveTab('empleados');
    } catch(err) {
      alert("Error al contratar candidato");
    }
  };

  const handleSaveEmp = async (data: any) => {
    try {
      const payload = {
        ...data,
        rol: { id: data.rolId },
        tipoContrato: { id: data.tipoContratoId }
      };

      await saveEmpleado(payload);
      fetchData();
      setModalEmp(null);
    } catch(err) {
      alert("Error al guardar empleado");
    }
  };

  const handleDelete = async (id: number) => {
    if(confirm('¿Seguro que deseas eliminar lógicamente a este empleado?')) {
      try {
        await deleteEmpleado(id);
        fetchData();
      } catch(err) {
        alert("Error al eliminar");
      }
    }
  };


  const renderEmpleados = () => (
    <div className="print:hidden">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-4xl font-bold text-stone-800">Catálogo de Personal</h1>
        <button onClick={() => setModalEmp({data: null, isNew: true})} className="bg-orange-500 hover:bg-orange-600 text-white px-6 py-3 rounded-lg font-bold transition-colors flex items-center gap-2">
          <UserPlus size={20}/> Nuevo Empleado
        </button>
      </div>

      <div className="mb-6 relative">
        <div className="absolute inset-y-0 left-0 pl-4 flex items-center pointer-events-none">
          <Search className="h-5 w-5 text-stone-400" />
        </div>
        <input
          type="text"
          className="block w-full pl-12 pr-3 py-4 border border-stone-200 rounded-xl leading-5 bg-white placeholder-stone-400 focus:outline-none focus:border-orange-500 focus:ring-1 focus:ring-orange-500 font-medium text-stone-800 transition-colors shadow-sm"
          placeholder="Buscar empleado por nombre..."
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
        />
      </div>

      <div className="bg-white rounded-xl shadow-sm border border-stone-200 overflow-hidden">
        <table className="w-full text-left">
          <thead className="bg-stone-50 border-b border-stone-200 text-stone-600">
            <tr>
              <th className="p-4 font-semibold">Nombre</th>
              <th className="p-4 font-semibold">Rol</th>
              <th className="p-4 font-semibold">Contrato</th>
              <th className="p-4 font-semibold text-center">Condiciones Médicas</th>
              <th className="p-4 font-semibold text-center">Apoyo Visual</th>
              <th className="p-4 font-semibold text-right">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {mockEmpleados.filter(emp => emp.nombre.toLowerCase().includes(searchQuery.toLowerCase())).map((emp) => (
              <React.Fragment key={emp.id}>
                <tr className={`border-b border-stone-100 hover:bg-stone-50 transition-colors ${expandedEmp === emp.id ? 'bg-orange-50' : ''}`}>
                  <td className="p-4 font-medium text-stone-800 text-lg flex items-center gap-3">
                    <img src={emp.urlAvatar} alt={emp.nombre} className="w-14 h-14 shadow-md rounded-full object-cover border-2 border-stone-200" />
                    {emp.nombre}
                  </td>
                  <td className="p-4 text-stone-600"><span className="px-3 py-1 bg-blue-100 text-blue-700 rounded-md text-sm font-bold">{emp.rol?.nombre || 'Sin Rol'}</span></td>
                  <td className="p-4 text-stone-600">{emp.tipoContrato?.nombre || 'Sin Contrato'}</td>
                  <td className="p-4 text-center">
                    {emp.condicionesMedicas ? <span className="inline-block px-3 py-1 bg-red-100 text-red-700 rounded-full text-xs font-bold max-w-[150px] truncate" title={emp.condicionesMedicas}>{emp.condicionesMedicas}</span> : <span className="text-stone-400 text-sm font-medium">Ninguna</span>}
                  </td>
                  <td className="p-4 text-center">
                    {emp.requiereApoyo && <span className="inline-block px-3 py-1 bg-amber-100 text-amber-700 rounded-full text-xs font-bold">Requiere Tutor</span>}
                  </td>
                  <td className="p-4 text-right flex justify-end gap-2">
                    <button onClick={() => handleEnrollFingerprint(emp.id, true)} title="Simular Registro (Dev)" className="p-2 text-stone-300 hover:text-purple-500"><Fingerprint size={20}/></button>
                    <button onClick={() => handleEnrollFingerprint(emp.id)} title="Registrar Huella" className={`p-2 ${emp.huellaDactilar ? 'text-emerald-500 hover:text-emerald-600' : 'text-stone-400 hover:text-orange-500'}`}><Fingerprint size={20}/></button>
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
      
      await assignHorario({
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


  const renderCandidatos = () => (
    <div className="print:hidden">
      <div className="flex justify-between items-center">
        <div>
          <h1 className="text-4xl font-bold text-stone-800">Lista de Espera</h1>
          <p className="text-stone-500 mt-2">Personal interesado en unirse a Chiokore Bazar.</p>
        </div>
        <button onClick={() => setModalCand({data: null, isNew: true})} className="bg-orange-500 hover:bg-orange-600 text-white px-6 py-3 rounded-lg font-bold transition-colors flex items-center gap-2">
          <UserPlus size={20}/> Nuevo Candidato
        </button>
      </div>
      <div className="mt-8 grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        {candidatos.map((candidato) => (
          <div key={candidato.id} className="bg-white p-6 rounded-xl shadow-sm border border-stone-200 flex flex-col items-center text-center hover:shadow-md transition-shadow relative">
            <div className="absolute top-4 right-4 bg-yellow-100 text-yellow-800 text-xs font-black px-3 py-1 rounded-full">EN ESPERA</div>
            <div className="w-16 h-16 bg-stone-100 rounded-full flex items-center justify-center mb-4 mt-2 overflow-hidden shadow-sm">
              {candidato.urlAvatar ? (
                <img src={candidato.urlAvatar} alt={candidato.nombre} className="w-full h-full object-cover" />
              ) : (
                <UserPlus size={32} className="text-orange-500" />
              )}
            </div>
            <h3 className="font-bold text-stone-800 text-lg">{candidato.nombre}</h3>
            <p className="text-sm font-bold mt-1 text-indigo-600 uppercase">{candidato.tipoContrato?.nombre || 'Por Definir'}</p>
            <p className="text-xs text-stone-400 mt-4">Registrado: {candidato.fechaSolicitud}</p>
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
              <div className="w-24 h-24 bg-stone-100 rounded-full flex items-center justify-center mx-auto mb-4 overflow-hidden shadow-md border-4 border-white">
                {selectedCandidato.urlAvatar ? (
                  <img src={selectedCandidato.urlAvatar} alt={selectedCandidato.nombre} className="w-full h-full object-cover" />
                ) : (
                  <UserPlus size={40} className="text-orange-500" />
                )}
              </div>
              <h2 className="text-2xl font-black text-stone-800">{selectedCandidato.nombre}</h2>
              <p className="text-sm font-bold mt-1 text-indigo-600 uppercase">{selectedCandidato.tipoContrato?.nombre || 'Por Definir'}</p>
              
              <div className="mt-6 text-left bg-stone-50 p-4 rounded-lg overflow-y-auto max-h-[40vh]">
                <p className="text-xs font-bold text-stone-500 uppercase tracking-wider mb-4">Detalles Adicionales</p>
                <div className="grid grid-cols-2 gap-y-4 gap-x-4 text-sm text-stone-800">
                  <p><strong>Registro:</strong><br/>{selectedCandidato.fechaSolicitud}</p>
                  <p><strong>Tipo de Sangre:</strong><br/>{selectedCandidato.tipoSangre || 'N/A'}</p>
                  <p><strong>Rol:</strong><br/>{selectedCandidato.rol?.nombre || 'N/A'}</p>
                  <p><strong>Contrato:</strong><br/>{selectedCandidato.tipoContrato?.nombre || 'N/A'}</p>
                  <p className="col-span-2"><strong>Condiciones Médicas:</strong><br/>{selectedCandidato.condicionesMedicas || 'Ninguna'}</p>
                  <p className="col-span-2"><strong>Contacto de Emergencia:</strong><br/>{selectedCandidato.contactoEmergenciaNombre || 'N/A'} {selectedCandidato.contactoEmergenciaTelefono ? `(${selectedCandidato.contactoEmergenciaTelefono})` : ''}</p>
                  <p className="col-span-2"><strong>Apoyo Visual:</strong><br/>{selectedCandidato.requiereApoyo ? 'Sí, requiere acompañante' : 'No'}</p>
                  <p className="col-span-2 mt-2 pt-4 border-t border-stone-200"><strong>Notas de Entrevista:</strong><br/>{selectedCandidato.notas || 'Sin notas.'}</p>
                </div>
              </div>

              <div className="mt-8 flex gap-4">
                <button onClick={() => {
                  const isPagado = selectedCandidato.tipoContrato?.nombre?.toUpperCase() === 'PAGADO' || selectedCandidato.tipoContrato?.id === 1;
                  if (isPagado) {
                     setPayoutModalCand(selectedCandidato);
                     setTempPayout('');
                  } else {
                     handleHireCandidato(selectedCandidato, 0);
                  }
                }} className="flex-1 bg-emerald-600 hover:bg-emerald-700 text-white py-3 rounded-lg font-bold shadow-sm">Aceptar y Contratar</button>
                <button onClick={() => setSelectedCandidato(null)} className="flex-1 bg-stone-200 hover:bg-stone-300 text-stone-800 py-3 rounded-lg font-bold">Cerrar</button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );

  const renderAsistencias = () => {
    // Calcular horas totales por empleado para Empleado del Mes
    const horasPorEmpleado: { [key: number]: { nombre: string; urlAvatar?: string; horas: number } } = {};
    
    asistencias.forEach(a => {
      const empId = a.empleado.id;
      if (!horasPorEmpleado[empId]) {
        horasPorEmpleado[empId] = {
          nombre: a.empleado.nombre,
          urlAvatar: a.empleado.urlAvatar,
          horas: 0
        };
      }
      if (a.horasCalculadas) {
        horasPorEmpleado[empId].horas += a.horasCalculadas;
      }
    });

    const empleadosOrdenados = Object.values(horasPorEmpleado).sort((a, b) => b.horas - a.horas);
    const empleadoDelMes = empleadosOrdenados.length > 0 && empleadosOrdenados[0].horas > 0 ? empleadosOrdenados[0] : null;

    return (
      <div className="print:hidden">
        <div className="flex justify-between items-center mb-8">
          <div>
            <h1 className="text-4xl font-bold text-stone-800">Asistencias y Rendimientos</h1>
            <p className="text-stone-500 mt-2">Seguimiento de horas trabajadas y desempeño del personal.</p>
          </div>
          <div className="flex gap-4">
            <div>
              <label className="block text-xs font-bold text-stone-500 uppercase mb-1">Fecha Inicial</label>
              <input type="date" value={asistStartDate} onChange={e => setAsistStartDate(e.target.value)} className="border border-stone-200 p-2 rounded-lg text-stone-800 font-medium outline-none focus:border-orange-500" />
            </div>
            <div>
              <label className="block text-xs font-bold text-stone-500 uppercase mb-1">Fecha Final</label>
              <input type="date" value={asistEndDate} onChange={e => setAsistEndDate(e.target.value)} className="border border-stone-200 p-2 rounded-lg text-stone-800 font-medium outline-none focus:border-orange-500" />
            </div>
            <div className="flex items-end">
              <button onClick={() => { setAsistStartDate(''); setAsistEndDate(''); }} className="p-2 h-[42px] bg-stone-100 text-stone-500 hover:bg-stone-200 hover:text-stone-800 font-bold rounded-lg transition-colors">Limpiar Filtros</button>
            </div>
          </div>
        </div>

        {empleadoDelMes && (
          <div className="mb-10 bg-gradient-to-r from-amber-400 to-orange-500 rounded-3xl p-8 text-white shadow-xl flex items-center gap-8 relative overflow-hidden">
            <div className="absolute right-0 top-0 w-64 h-64 bg-white/10 rounded-full -translate-y-1/2 translate-x-1/3 blur-2xl"></div>
            
            <div className="w-32 h-32 rounded-full border-4 border-white/30 shadow-2xl overflow-hidden bg-white/20 flex-shrink-0 flex items-center justify-center">
              {empleadoDelMes.urlAvatar ? (
                <img src={empleadoDelMes.urlAvatar} alt={empleadoDelMes.nombre} className="w-full h-full object-cover" />
              ) : (
                <Users size={48} className="text-white/80" />
              )}
            </div>
            
            <div className="z-10 relative">
              <div className="flex items-center gap-2 text-amber-100 font-bold tracking-widest text-sm mb-2">
                <ShieldCheck size={18} /> EMPLEADO DEL MES
              </div>
              <h2 className="text-5xl font-black mb-2 drop-shadow-md">{empleadoDelMes.nombre}</h2>
              <p className="text-2xl font-medium text-orange-50">Líder en rendimiento con <strong className="font-black text-white">{empleadoDelMes.horas.toFixed(2)} horas</strong> registradas.</p>
            </div>
          </div>
        )}

        <div className="bg-white rounded-xl shadow-sm border border-stone-200 overflow-hidden">
          <table className="w-full text-left">
            <thead className="bg-stone-50 border-b border-stone-200 text-stone-600">
              <tr>
                <th className="p-4 font-semibold">Fecha</th>
                <th className="p-4 font-semibold">Empleado</th>
                <th className="p-4 font-semibold text-center">Entrada</th>
                <th className="p-4 font-semibold text-center">Salida</th>
                <th className="p-4 font-semibold text-right">Horas Calculadas</th>
              </tr>
            </thead>
            <tbody>
              {asistencias.length === 0 ? (
                <tr>
                  <td colSpan={5} className="p-8 text-center text-stone-500 font-medium">No hay registros de asistencias.</td>
                </tr>
              ) : (
                asistencias.map(a => (
                  <tr key={a.id} className="border-b border-stone-100 hover:bg-stone-50 transition-colors">
                    <td className="p-4 font-bold text-stone-700">{a.fecha}</td>
                    <td className="p-4 font-medium text-stone-800 flex items-center gap-3">
                      {a.empleado.urlAvatar ? (
                        <img src={a.empleado.urlAvatar} alt={a.empleado.nombre} className="w-10 h-10 rounded-full object-cover border border-stone-200" />
                      ) : (
                        <div className="w-10 h-10 bg-stone-200 rounded-full flex items-center justify-center"><Users size={16} className="text-stone-500"/></div>
                      )}
                      {a.empleado.nombre}
                    </td>
                    <td className="p-4 text-center text-emerald-600 font-bold">{a.entrada ? new Date(a.entrada).toLocaleTimeString('es-MX', {hour: '2-digit', minute:'2-digit'}) : '--:--'}</td>
                    <td className="p-4 text-center text-indigo-600 font-bold">{a.salida ? new Date(a.salida).toLocaleTimeString('es-MX', {hour: '2-digit', minute:'2-digit'}) : '--:--'}</td>
                    <td className="p-4 text-right font-black text-stone-800">{a.horasCalculadas ? `${a.horasCalculadas.toFixed(2)}h` : '-'}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </div>
    );
  };

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
          <button onClick={() => setActiveTab('candidatos')} className={`flex items-center gap-4 w-full p-5 transition-colors ${activeTab === 'candidatos' ? 'bg-stone-800 border-l-4 border-orange-500 text-white' : 'border-l-4 border-transparent text-stone-400 hover:bg-stone-800/50 hover:text-stone-200'}`}>
            <UserPlus size={24} /> Lista de Espera
          </button>
          <button onClick={() => setActiveTab('asistencias')} className={`flex items-center gap-4 w-full p-5 transition-colors ${activeTab === 'asistencias' ? 'bg-stone-800 border-l-4 border-orange-500 text-white' : 'border-l-4 border-transparent text-stone-400 hover:bg-stone-800/50 hover:text-stone-200'}`}>
            <Clock size={24} /> Asistencias y R.
          </button>
        </nav>
        <div className="p-6 border-t border-stone-800">
          <Link to="/" className="flex items-center gap-3 w-full p-4 hover:bg-stone-800 text-stone-400 hover:text-white rounded-xl transition-colors font-bold">
            <LogOut size={20} /> Volver al Checador
          </Link>
        </div>
      </div>

      <main className="flex-1 p-12 overflow-y-auto print:p-0">
          <div className="mb-8 rounded-3xl border border-sky-200 bg-sky-50 px-6 py-4 shadow-sm flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between print:hidden">
            <div className="flex items-center gap-3 text-sky-900">
              <ShieldCheck size={22} />
              <div>
                <p className="font-black uppercase tracking-wide text-sm">Panel administrativo</p>
                <p className="text-sm text-sky-800">Conectado al flujo de autenticación y operación del sistema.</p>
              </div>
            </div>
          </div>
          {activeTab === 'empleados' && renderEmpleados()}
          {activeTab === 'horarios' && renderHorarios()}
          {activeTab === 'candidatos' && renderCandidatos()}
          {activeTab === 'asistencias' && renderAsistencias()}

          {isScanning && (
            <div className="fixed inset-0 bg-black/60 z-[100] flex items-center justify-center backdrop-blur-sm">
              <div className="bg-white p-10 rounded-3xl text-center shadow-2xl max-w-sm w-full mx-4 border border-orange-100 animate-in fade-in zoom-in duration-300">
                <div className="bg-orange-50 w-24 h-24 mx-auto rounded-full flex items-center justify-center mb-6 shadow-inner">
                  <Fingerprint className="w-12 h-12 text-orange-500 animate-pulse" />
                </div>
                <h3 className="text-2xl font-black text-stone-800 mb-2">Escáner Activo</h3>
                <p className="text-stone-600 font-medium">Por favor, coloca tu dedo en el lector DigitalPersona...</p>
              </div>
            </div>
          )}

          {modalEmp && (
            <EmployeeModal 
              emp={modalEmp.data} 
              onSave={handleSaveEmp} 
              onClose={() => setModalEmp(null)} 
            />
          )}

          {modalCand && (
            <CandidatoModal 
              cand={modalCand.data} 
              onSave={handleSaveCand} 
              onClose={() => setModalCand(null)} 
            />
          )}

          {payoutModalCand && (
            <div className="fixed inset-0 bg-stone-900/50 flex items-center justify-center p-4 z-50">
              <div className="bg-white rounded-2xl p-8 max-w-sm w-full shadow-2xl relative text-center">
                <h3 className="text-xl font-black text-stone-800 mb-2">Asignar Tarifa</h3>
                <p className="text-sm text-stone-500 mb-6">El candidato tendrá contrato pagado. Ingresa su tarifa por hora.</p>
                <div className="mb-6 relative">
                  <span className="absolute left-4 top-1/2 -translate-y-1/2 font-black text-stone-400">$</span>
                  <input type="number" className="w-full border-2 border-stone-200 p-3 pl-8 rounded-lg font-bold text-stone-800 focus:border-orange-500 focus:outline-none transition-colors" placeholder="0.00" value={tempPayout} onChange={e => setTempPayout(e.target.value)} />
                </div>
                <div className="flex gap-3">
                  <button onClick={() => setPayoutModalCand(null)} className="flex-1 py-2 font-bold text-stone-500 hover:bg-stone-100 rounded-lg">Cancelar</button>
                  <button onClick={() => handleHireCandidato(payoutModalCand, Number(tempPayout))} className="flex-1 py-2 font-bold text-white bg-emerald-600 hover:bg-emerald-700 rounded-lg shadow-sm">Confirmar</button>
                </div>
              </div>
            </div>
          )}
        </main>
    </div>
  );
}