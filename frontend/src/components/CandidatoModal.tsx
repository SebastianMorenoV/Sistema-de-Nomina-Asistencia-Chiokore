import { useState } from 'react';

export default function CandidatoModal({ cand, onSave, onClose }: any) {
  const [formData, setFormData] = useState({
    id: cand?.id,
    nombre: cand?.nombre || '', 
    rolId: cand?.rol?.id || 1, 
    tipoContratoId: cand?.tipoContrato?.id || 1, 
    tipoSangre: cand?.tipoSangre || '', 
    condicionesMedicas: cand?.condicionesMedicas || '', 
    contactoEmergenciaNombre: cand?.contactoEmergenciaNombre || '', 
    requiereApoyo: cand?.requiereApoyo || false, 
    urlAvatar: cand?.urlAvatar || '',
    notas: cand?.notas || ''
  });

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50 print:hidden">
      <div className="bg-white rounded-2xl p-8 max-w-2xl w-full shadow-2xl h-[90vh] overflow-y-auto">
        <h2 className="text-3xl font-bold text-stone-800 mb-6">{cand ? 'Editar Candidato' : 'Nuevo Candidato'}</h2>
        
        <div className="grid grid-cols-2 gap-4">
          <div className="col-span-2">
            <label className="block text-sm font-bold text-stone-600">Nombre Completo</label>
            <input type="text" className="w-full border p-2 rounded" value={formData.nombre} onChange={e => setFormData({...formData, nombre: e.target.value})} />
          </div>
          <div className="col-span-2">
            <label className="block text-sm font-bold text-stone-600">Fotografía (Subir Archivo)</label>
            <div className="flex items-center gap-4 mt-1">
              {formData.urlAvatar && (
                <img src={formData.urlAvatar} alt="Preview" className="w-12 h-12 rounded-full object-cover shadow-sm border-2 border-orange-500" />
              )}
              <input 
                type="file" 
                accept="image/*"
                className="w-full border p-1.5 rounded text-sm text-stone-600 file:mr-4 file:py-2 file:px-4 file:rounded file:border-0 file:text-sm file:font-semibold file:bg-orange-50 file:text-orange-700 hover:file:bg-orange-100" 
                onChange={e => {
                  const file = e.target.files?.[0];
                  if (file) {
                    const reader = new FileReader();
                    reader.onloadend = () => {
                      setFormData({...formData, urlAvatar: reader.result});
                    };
                    reader.readAsDataURL(file);
                  }
                }} 
              />
            </div>
          </div>
          <div>
            <label className="block text-sm font-bold text-stone-600">Rol Esperado</label>
            <select className="w-full border p-2 rounded" value={formData.rolId} onChange={e => setFormData({...formData, rolId: Number(e.target.value)})}>
              <option value={1}>Trabajador</option><option value={2}>Administrador</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-bold text-stone-600">Contrato Esperado</label>
            <select className="w-full border p-2 rounded" value={formData.tipoContratoId} onChange={e => setFormData({...formData, tipoContratoId: Number(e.target.value)})}>
              <option value={1}>Pagado</option><option value={2}>Voluntariado</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-bold text-stone-600">Tipo de Sangre</label>
            <input type="text" className="w-full border p-2 rounded" value={formData.tipoSangre} onChange={e => setFormData({...formData, tipoSangre: e.target.value})} />
          </div>
          <div className="col-span-2">
            <label className="block text-sm font-bold text-stone-600">Condiciones Médicas</label>
            <textarea className="w-full border p-2 rounded" value={formData.condicionesMedicas} onChange={e => setFormData({...formData, condicionesMedicas: e.target.value})} />
          </div>
          <div className="col-span-2">
            <label className="block text-sm font-bold text-stone-600">Contacto de Emergencia</label>
            <input type="text" className="w-full border p-2 rounded" value={formData.contactoEmergenciaNombre} onChange={e => setFormData({...formData, contactoEmergenciaNombre: e.target.value})} />
          </div>
          <div className="col-span-2">
            <label className="block text-sm font-bold text-stone-600">Notas de Entrevista</label>
            <textarea className="w-full border p-2 rounded h-24" value={formData.notas} onChange={e => setFormData({...formData, notas: e.target.value})} />
          </div>
          <div className="col-span-2 flex items-center gap-2 mt-2">
            <input type="checkbox" id="apoyo" checked={formData.requiereApoyo} onChange={e => setFormData({...formData, requiereApoyo: e.target.checked})} />
            <label htmlFor="apoyo" className="font-bold text-stone-600">Requiere Acompañante / Apoyo Visual</label>
          </div>
        </div>

        <div className="mt-8 flex justify-end gap-4">
          <button onClick={onClose} className="px-6 py-2 text-stone-500 font-bold hover:bg-stone-100 rounded">Cancelar</button>
          <button onClick={() => onSave({
            ...formData,
            fechaSolicitud: new Date().toISOString().split('T')[0] // Set fechaSolicitud to today if new
          })} className="px-6 py-2 bg-orange-500 text-white font-bold rounded hover:bg-orange-600">Guardar Candidato</button>
        </div>
      </div>
    </div>
  );
}
