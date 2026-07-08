import { Sun, Moon, ArrowLeft, ShieldAlert } from 'lucide-react';

interface Props {
  empleado: any;
  onAction: (tipo: 'ENTRADA' | 'SALIDA') => void;
  onCancel: () => void;
  isCompanionMode?: boolean;
  isLoading?: boolean;
}

export default function ActionModal({ empleado, onAction, onCancel, isCompanionMode, isLoading }: Props) {
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
        
        {isLoading && (
          <div className="mb-8 w-full rounded-3xl border border-stone-200 bg-stone-50 px-6 py-4 text-center text-stone-700 font-bold animate-pulse">
            Registrando asistencia, espere un momento.
          </div>
        )}

        <div className="grid grid-cols-2 gap-12 w-full">
          <button 
            onClick={() => onAction('ENTRADA')}
            disabled={isLoading}
            className="flex flex-col items-center justify-center p-16 bg-gradient-to-br from-emerald-400 to-emerald-600 hover:from-emerald-300 hover:to-emerald-500 rounded-[3rem] shadow-emerald-200 shadow-2xl transition-all hover:scale-105 active:scale-95 disabled:opacity-60 disabled:cursor-not-allowed disabled:hover:scale-100"
          >
            <Sun className="w-48 h-48 text-white mb-8 drop-shadow-md" />
            <span className="text-5xl font-black text-white drop-shadow-md">ENTRADA</span>
          </button>
          
          <button 
            onClick={() => onAction('SALIDA')}
            disabled={isLoading}
            className="flex flex-col items-center justify-center p-16 bg-gradient-to-br from-indigo-400 to-indigo-600 hover:from-indigo-300 hover:to-indigo-500 rounded-[3rem] shadow-indigo-200 shadow-2xl transition-all hover:scale-105 active:scale-95 disabled:opacity-60 disabled:cursor-not-allowed disabled:hover:scale-100"
          >
            <Moon className="w-48 h-48 text-white mb-8 drop-shadow-md" />
            <span className="text-5xl font-black text-white drop-shadow-md">SALIDA</span>
          </button>
        </div>

        <button 
          onClick={onCancel}
          disabled={isLoading}
          className="mt-16 flex items-center gap-4 px-10 py-5 bg-stone-100 rounded-full text-stone-500 hover:text-stone-800 hover:bg-stone-200 text-3xl font-bold transition-colors disabled:cursor-not-allowed disabled:opacity-60"
        >
          <ArrowLeft className="w-10 h-10" />
          Cancelar
        </button>
      </div>
    </div>
  );
}