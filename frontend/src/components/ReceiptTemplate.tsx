import React from 'react';

export default function ReceiptTemplate({ data }: any) {
  if (!data) return null;
  return (
    <div className="hidden print:block font-sans text-xs w-[80mm] p-2 text-black bg-white mx-auto">
       <h1 className="text-center font-bold text-lg leading-tight mb-2">RECIBO DE PAGO POR HORAS TRABAJADAS</h1>
       <div className="text-center font-bold text-sm">Chiokore - Bazar con Causa</div>
       <div className="text-center text-[11px] leading-tight mb-4">Puebla #438 Sur, Col. Centro, Enseguida de Bicicentro</div>
       <hr className="my-2 border-dashed border-black"/>
       
       <div className="mb-2">
         <span className="font-bold block">Nombre del trabajador(a):</span>
         <span className="text-lg uppercase border-b border-black inline-block w-full">{data.nombre}</span>
       </div>
       
       <div className="mb-2">
         <span className="font-bold">Periodo de trabajo:</span><br/>
         Del <span className="border-b border-black">{data.fechaInicio}</span> al <span className="border-b border-black">{data.fechaFin}</span>
       </div>
       
       <div className="mb-2">
         <span className="font-bold">Horas trabajadas:</span> <span className="border-b border-black px-2">{data.horas}</span>
       </div>
       
       <div className="mb-2">
         <span className="font-bold">Pago por hora:</span> $ <span className="border-b border-black px-2">{data.tarifa}</span> MXN
       </div>
       
       <div className="mt-4 pt-2 border-t-2 border-black text-right font-bold text-lg">
         Total a pagar: $ {data.total} MXN
       </div>
       <div className="text-right text-[10px] leading-tight mb-4">
         (Horas trabajadas x Pago por hora)
       </div>
       
       <hr className="my-2 border-dashed border-black"/>
       
       <div className="font-bold mb-1">Declaración</div>
       <div className="text-[11px] text-justify mb-8">
         Declaro haber recibido el pago correspondiente al periodo y horas señaladas en este recibo.
       </div>
       
       <div className="mt-12 flex items-end">
         <span className="font-bold text-xs whitespace-nowrap mr-2">Firma del trabajador(a):</span>
         <div className="border-b border-black flex-1"></div>
       </div>
       
       <div className="mt-12 flex items-end">
         <span className="font-bold text-xs whitespace-nowrap mr-2">Firma responsable de Chiokore:</span>
         <div className="border-b border-black flex-1"></div>
       </div>
       
       <div className="mt-8 flex items-end">
         <span className="font-bold">Fecha:</span>
         <div className="border-b border-black ml-2 px-8">{data.fechaEmision}</div>
       </div>
       
       <div className="mt-8 text-center text-[10px] italic text-gray-500">
         -- Fin del recibo --
       </div>
    </div>
  );
}
