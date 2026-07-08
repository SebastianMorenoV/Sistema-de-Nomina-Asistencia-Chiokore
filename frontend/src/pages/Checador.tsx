import { useCallback, useEffect, useRef, useState } from 'react';
import { Link } from 'react-router-dom';
import { BadgeCheck, Settings, Users } from 'lucide-react';
import EmpleadoCard from '../components/EmpleadoCard';
import ActionModal from '../components/ActionModal';
import Screensaver from '../components/Screensaver';
import SuccessScreen from '../components/SuccessScreen';
import { getApiErrorMessage, getEmpleados, kioskLogin } from '../services/api';

export default function Checador() {
  type Employee = {
    id: number;
    nombre: string;
    urlAvatar?: string;
    requiereApoyo?: boolean;
    tipoContrato?: { id?: number; nombre?: string };
    rol?: { id?: number; nombre?: string };
  };

  const [empleados, setEmpleados] = useState<Employee[]>([]);
  const [selectedEmpleado, setSelectedEmpleado] = useState<Employee | null>(null);
  const [showSuccess, setShowSuccess] = useState<string | null>(null);
  const [isCompanionMode, setIsCompanionMode] = useState(false);
  const [registrosHoy, setRegistrosHoy] = useState<Record<number, string>>({});
  const [isRegistering, setIsRegistering] = useState(false);
  const [isIdle, setIsIdle] = useState(false);
  const idleTimeout = useRef<ReturnType<typeof setTimeout> | null>(null);

  const resetIdleTimer = useCallback(() => {
    setIsIdle(false);
    if (idleTimeout.current) clearTimeout(idleTimeout.current);
    idleTimeout.current = setTimeout(() => {
      if (!showSuccess && !selectedEmpleado) {
        setIsIdle(true);
      }
    }, 15000);
  }, [selectedEmpleado, showSuccess]);

  useEffect(() => {
    const fetchEmpleados = async () => {
      try {
        const res = await getEmpleados();
        setEmpleados(res.data);
      } catch (err) {
        console.error('Error al cargar empleados:', err);
      }
    };

    fetchEmpleados();
  }, []);

  useEffect(() => {
    window.addEventListener('mousemove', resetIdleTimer);
    window.addEventListener('touchstart', resetIdleTimer);
    window.addEventListener('keydown', resetIdleTimer);

    return () => {
      window.removeEventListener('mousemove', resetIdleTimer);
      window.removeEventListener('touchstart', resetIdleTimer);
      window.removeEventListener('keydown', resetIdleTimer);
      if (idleTimeout.current) clearTimeout(idleTimeout.current);
    };
  }, [resetIdleTimer]);

  const handleAction = async (tipo: 'ENTRADA' | 'SALIDA') => {
    if (!selectedEmpleado) return;

    setIsRegistering(true);
    try {
      const response = await kioskLogin(selectedEmpleado.id, tipo);
      setRegistrosHoy((prev) => ({ ...prev, [selectedEmpleado.id]: response.data.movimiento }));
      console.info('JWT emitido y enviado al sistema consumidor', {
        empleadoId: response.data.empleadoId,
        movimiento: response.data.movimiento,
        rol: response.data.rol,
      });
      setShowSuccess(`${response.data.movimiento} registrada con éxito`);
    } catch (err: unknown) {
      alert(getApiErrorMessage(err, 'Error al registrar asistencia'));
    } finally {
      setIsRegistering(false);
      setSelectedEmpleado(null);
    }
  };

  const finishAction = () => {
    setShowSuccess(null);
    resetIdleTimer();
  };

  return (
    <div className="min-h-screen bg-stone-50 p-12 font-sans relative select-none">
      {isIdle && <Screensaver onWake={resetIdleTimer} />}

      <div className="mb-8 rounded-3xl border border-emerald-200 bg-emerald-50 px-6 py-4 shadow-sm flex flex-col gap-3 lg:flex-row lg:items-center lg:justify-between print:hidden">
        <div className="flex items-center gap-3 text-emerald-900">
          <BadgeCheck size={22} />
          <div>
            <p className="font-black uppercase tracking-wide text-sm">Kiosco de asistencia</p>
            <p className="text-sm text-emerald-800">Registro en tiempo real con emisión de JWT para el sistema consumidor.</p>
          </div>
        </div>
      </div>

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
        {empleados.map((emp) => (
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
          isLoading={isRegistering}
          onCancel={() => {
            if (isRegistering) return;
            setSelectedEmpleado(null);
            resetIdleTimer();
          }}
          isCompanionMode={isCompanionMode}
        />
      )}

      {showSuccess && (
        <div className="fixed inset-0 z-[95]">
          <SuccessScreen mensaje={showSuccess} onFinish={finishAction} />
        </div>
      )}

      {Object.keys(registrosHoy).length > 0 && (
        <div className="fixed bottom-6 left-6 z-40 rounded-2xl bg-stone-900 text-white px-4 py-3 shadow-2xl text-sm border border-stone-700">
          Último movimiento: {Object.values(registrosHoy).at(-1)}
        </div>
      )}
    </div>
  );
}