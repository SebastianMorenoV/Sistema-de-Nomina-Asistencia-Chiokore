import React, { useEffect, useState } from 'react';

export default function Screensaver({ onWake }: { onWake: () => void }) {
  return (
    <div 
      onClick={onWake}
      className="fixed inset-0 bg-stone-950 z-[100] flex flex-col items-center justify-center cursor-pointer transition-opacity duration-1000"
    >
      <div className="animate-pulse flex flex-col items-center">
        <h1 className="text-8xl font-black text-white mb-4 tracking-tighter">
          Chiokore <span className="text-orange-500">Bazar</span>
        </h1>
        <p className="text-2xl text-stone-400 font-medium tracking-widest uppercase mt-4 border-t border-stone-800 pt-4">
          Toca la pantalla para iniciar
        </p>
      </div>
    </div>
  );
}