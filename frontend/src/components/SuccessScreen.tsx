import { useEffect } from 'react';
import { CheckCircle } from 'lucide-react';

interface Props {
  mensaje: string;
  onFinish: () => void;
}

export default function SuccessScreen({ mensaje, onFinish }: Props) {
  useEffect(() => {
    // Audio feedback
    const utterance = new SpeechSynthesisUtterance(mensaje);
    utterance.lang = 'es-MX';
    window.speechSynthesis.speak(utterance);
    
    // Auto-close after 3 seconds
    const timer = setTimeout(onFinish, 3500);
    return () => clearTimeout(timer);
  }, [mensaje, onFinish]);

  return (
    <div className="fixed inset-0 bg-emerald-950 flex flex-col items-center justify-center p-4 z-50">
      <CheckCircle className="w-72 h-72 text-emerald-400 mb-12 animate-bounce" />
      <h1 className="text-7xl font-black text-white text-center tracking-tight">{mensaje}</h1>
    </div>
  );
}
