import React from 'react';
import { Sun, Moon, ArrowLeft } from 'lucide-react';

interface Props {
  empleado: any;
  onAction: (tipo: 'ENTRADA' | 'SALIDA') => void;
  onCancel: () => void;
}

// NOTE: This component was accidentally mixed with ActionModal logic before, 
// let's restore the pure EmpleadoCard functionality
export default function EmpleadoCard({ empleado, onSelect, isCompanionMode }: any) {
  const handleClick = () => {
    if (!isCompanionMode) {
      const utterance = new SpeechSynthesisUtterance(empleado.nombre);
      utterance.lang = 'es-MX';
      window.speechSynthesis.speak(utterance);
    }
    onSelect(empleado);
  };

  return (
    <button 
      onClick={handleClick}
      className={`flex flex-col items-center bg-white rounded-3xl shadow-lg border border-amber-100 hover:shadow-xl hover:bg-amber-50 transition-all focus:outline-none focus:ring-4 focus:ring-orange-400 hover:-translate-y-2 group ${isCompanionMode ? 'p-4' : 'p-6'}`}
    >
      <img 
        src={empleado.urlAvatar || "https://ui-avatars.com/api/?name=" + empleado.nombre + "&size=200&background=random"} 
        alt={"Fotografía de " + empleado.nombre}
        className={`rounded-full object-cover border-4 border-white shadow-md group-hover:border-orange-300 transition-colors ${isCompanionMode ? 'w-32 h-32 mb-4' : 'w-48 h-48 mb-6'}`}
      />
      <h2 className={`font-bold text-stone-800 tracking-wide ${isCompanionMode ? 'text-2xl' : 'text-3xl'}`}>
        {empleado.nombre}
      </h2>
      {empleado.requiereApoyo && (
        <span className="mt-3 px-3 py-1 bg-amber-100 text-amber-700 text-xs font-bold rounded-full">
          Requiere Apoyo
        </span>
      )}
    </button>
  );
}