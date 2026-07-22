import { Sun, Moon, ArrowLeft, ShieldAlert, CheckCircle2 } from 'lucide-react';
import { useEffect, useState } from 'react';
import { getAsistenciaHoy } from '../services/api';

interface Props {
  empleado: any;
  onAction: () => void;
  onCancel: () => void;
  isCompanionMode?: boolean;
  isLoading?: boolean;
}

export default function ActionModal({ empleado, onAction, onCancel, isCompanionMode, isLoading }: Props) {
  const [status, setStatus] = useState<'LOADING' | 'PENDING' | 'ENTERED' | 'COMPLETED'>('LOADING');
  const [asistencia, setAsistencia] = useState<any>(null);

  useEffect(() => {
    const fetchStatus = async () => {
      try {
        const asis = await getAsistenciaHoy(empleado.id);
        if (!asis) {
          setStatus('PENDING');
        } else if (!asis.salida) {
          setStatus('ENTERED');
          setAsistencia(asis);
        } else {
          setStatus('COMPLETED');
          setAsistencia(asis);
        }
      } catch (err) {
        console.error("Error al obtener estatus", err);
        setStatus('PENDING'); // fallback
      }
    };
    fetchStatus();
  }, [empleado.id]);

  const formatTime = (isoString: string) => {
    return new Date(isoString).toLocaleTimeString('es-MX', { hour: '2-digit', minute: '2-digit' });
  };

  return (
    <div className="fixed inset-0 bg-stone-900/40 backdrop-blur-md flex items-center justify-center p-4 z-50 transition-opacity">
      <div className="bg-white p-12 rounded-[3rem] shadow-2xl max-w-5xl w-full flex flex-col items-center border border-amber-50 relative">
        
        {isCompanionMode && (
          <div className="absolute top-6 right-6 flex items-center gap-2 bg-blue-100 text-blue-700 px-4 py-2 rounded-full font-bold">
            <ShieldAlert size={20} /> Asistencia de Tutor Activa
          </div>
        )}

        <h2 className="text-6xl font-black text-stone-800 mb-16 text-center mt-4">
          Hola, <span className="text-orange-600">{empleado.nombre}</span>
        </h2>
        
        {isLoading || status === 'LOADING' ? (
          <div className="mb-8 w-full rounded-3xl border border-stone-200 bg-stone-50 px-6 py-4 text-center text-stone-700 font-bold animate-pulse">
            {isLoading ? 'Registrando asistencia, espere un momento...' : 'Verificando estatus...'}
          </div>
        ) : (
          <div className="flex justify-center w-full">
            {status === 'PENDING' && (
              <button 
                onClick={() => onAction()}
                disabled={isLoading}
                className="flex flex-col items-center justify-center p-16 bg-gradient-to-br from-emerald-400 to-emerald-600 hover:from-emerald-300 hover:to-emerald-500 rounded-[3rem] shadow-emerald-200 shadow-2xl transition-all hover:scale-105 active:scale-95 disabled:opacity-60 disabled:cursor-not-allowed disabled:hover:scale-100 w-full max-w-xl"
              >
                <Sun className="w-32 h-32 text-white mb-8 drop-shadow-md" />
                <span className="text-4xl font-black text-white drop-shadow-md">REGISTRAR ENTRADA</span>
              </button>
            )}
            
            {status === 'ENTERED' && (
              <button 
                onClick={() => onAction()}
                disabled={isLoading}
                className="flex flex-col items-center justify-center p-16 bg-gradient-to-br from-indigo-400 to-indigo-600 hover:from-indigo-300 hover:to-indigo-500 rounded-[3rem] shadow-indigo-200 shadow-2xl transition-all hover:scale-105 active:scale-95 disabled:opacity-60 disabled:cursor-not-allowed disabled:hover:scale-100 w-full max-w-xl"
              >
                <Moon className="w-32 h-32 text-white mb-8 drop-shadow-md" />
                <span className="text-4xl font-black text-white drop-shadow-md">REGISTRAR SALIDA</span>
                <span className="text-white/80 mt-4 text-xl">Entraste a las {formatTime(asistencia.entrada)}</span>
              </button>
            )}

            {status === 'COMPLETED' && (
              <div className="flex flex-col items-center justify-center p-16 bg-stone-100 rounded-[3rem] shadow-sm w-full max-w-xl border border-stone-200">
                <CheckCircle2 className="w-32 h-32 text-emerald-500 mb-8 drop-shadow-sm" />
                <span className="text-4xl font-black text-stone-800 drop-shadow-sm text-center">JORNADA COMPLETADA</span>
                <div className="mt-6 flex flex-col gap-2 text-2xl text-stone-600 text-center font-medium">
                  <p>Entrada: <span className="font-bold text-stone-800">{formatTime(asistencia.entrada)}</span></p>
                  <p>Salida: <span className="font-bold text-stone-800">{formatTime(asistencia.salida)}</span></p>
                </div>
              </div>
            )}
          </div>
        )}

        <button 
          onClick={onCancel}
          disabled={isLoading}
          className="mt-16 flex items-center gap-4 px-10 py-5 bg-stone-100 rounded-full text-stone-500 hover:text-stone-800 hover:bg-stone-200 text-3xl font-bold transition-colors disabled:cursor-not-allowed disabled:opacity-60"
        >
          <ArrowLeft className="w-10 h-10" />
          {status === 'COMPLETED' ? 'Volver' : 'Cancelar'}
        </button>
      </div>
    </div>
  );
}