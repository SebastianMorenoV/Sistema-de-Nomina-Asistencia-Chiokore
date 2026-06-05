import React, { useState, useEffect, useRef } from 'react';
import { Link } from 'react-router-dom';
import { Settings, Users } from 'lucide-react';
import axios from 'axios';
import EmpleadoCard from '../components/EmpleadoCard';
import ActionModal from '../components/ActionModal';
import SuccessScreen from '../components/SuccessScreen';
import Screensaver from '../components/Screensaver';

const API_URL = 'http://localhost:8080/api';

export default function Checador() {
  const [empleados, setEmpleados] = useState<any[]>([]);
  const [selectedEmpleado, setSelectedEmpleado] = useState<any | null>(null);
  const [showSuccess, setShowSuccess] = useState<string | null>(null);
  const [isCompanionMode, setIsCompanionMode] = useState(false);
  
  // Anti-spam sin pantalla de bloqueo
  const [registrosHoy, setRegistrosHoy] = useState<{[id: number]: string}>({});

  // Privacidad: Inactivity Timer
  const [isIdle, setIsIdle] = useState(false);
  const idleTimeout = useRef<any>(null);

  const resetIdleTimer = () => {
    setIsIdle(false);
    if (idleTimeout.current) clearTimeout(idleTimeout.current);
    // 15 segundos de inactividad activa el screensaver
    idleTimeout.current = setTimeout(() => {
      if (!showSuccess && !selectedEmpleado) {
        setIsIdle(true);
      }
    }, 15000);
  };

  // Fetch empleados from API
  useEffect(() => {
    const fetchEmpleados = async () => {
      try {
        const res = await axios.get(`${API_URL}/empleados`);
        setEmpleados(res.data);
      } catch (err) {
        console.error("Error al cargar empleados:", err);
      }
    };
    fetchEmpleados();
  }, []);

  useEffect(() => {
    window.addEventListener('mousemove', resetIdleTimer);
    window.addEventListener('touchstart', resetIdleTimer);
    window.addEventListener('keydown', resetIdleTimer);
    resetIdleTimer();

    return () => {
      window.removeEventListener('mousemove', resetIdleTimer);
      window.removeEventListener('touchstart', resetIdleTimer);
      window.removeEventListener('keydown', resetIdleTimer);
      if (idleTimeout.current) clearTimeout(idleTimeout.current);
    };
  }, [showSuccess, selectedEmpleado]);

  const handleAction = async (tipo: 'ENTRADA' | 'SALIDA') => {
    if (!selectedEmpleado) return;
    
    // Dejamos la validación anti-spam y dejamos que el backend se encargue del registro y toggle
    try {
      await axios.post(`${API_URL}/asistencias/registrar/${selectedEmpleado.id}`);
      setRegistrosHoy(prev => ({ ...prev, [selectedEmpleado.id]: tipo }));
      setShowSuccess(`¡${tipo} registrada con éxito!`);
    } catch(err: any) {
      alert(err.response?.data?.message || "Error al registrar asistencia");
    }
    
    setSelectedEmpleado(null);
  };

  const finishAction = () => {
    setShowSuccess(null);
    resetIdleTimer();
  };

  return (
    <div className="min-h-screen bg-stone-50 p-12 font-sans relative select-none">
      {isIdle && <Screensaver onWake={resetIdleTimer} />}

      {showSuccess && (
        <div className="fixed inset-0 z-[95]">
          <SuccessScreen mensaje={showSuccess} onFinish={finishAction} />
        </div>
      )}

      <div className="absolute top-8 right-12 flex gap-4 z-50">
        <button 
          onClick={() => setIsCompanionMode(!isCompanionMode)}
          className={`flex items-center gap-2 p-3 px-6 rounded-full font-bold transition-colors ${isCompanionMode ? 'bg-blue-600 text-white shadow-lg' : 'bg-stone-200 text-stone-600 hover:bg-stone-300'}`}
        >
          <Users size={24} />
          {isCompanionMode ? 'Modo Tutor: Activo' : 'Modo Acompañante'}
        </button>

        <Link to="/admin" className="p-3 bg-stone-200 text-stone-600 rounded-full hover:bg-stone-300 transition-colors">
          <Settings size={24} />
        </Link>
      </div>

      <header className="mb-16 text-center mt-8">
        <h1 className="text-6xl font-black text-stone-800 tracking-tight drop-shadow-sm">Chiokore <span className="text-orange-500">Bazar</span></h1>
        <p className="text-3xl text-stone-500 mt-4 font-medium">Toque su fotografía para registrarse</p>
      </header>
      
      <main className={`max-w-[1400px] mx-auto grid gap-10 ${isCompanionMode ? 'grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5' : 'grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4'}`}>
        {empleados.map(emp => (
          <EmpleadoCard 
            key={emp.id} 
            empleado={emp} 
            onSelect={setSelectedEmpleado} 
            isCompanionMode={isCompanionMode}
          />
        ))}
      </main>

      {selectedEmpleado && (
        <ActionModal 
          empleado={selectedEmpleado} 
          onAction={handleAction} 
          onCancel={() => { setSelectedEmpleado(null); resetIdleTimer(); }} 
          isCompanionMode={isCompanionMode}
        />
      )}
    </div>
  );
}