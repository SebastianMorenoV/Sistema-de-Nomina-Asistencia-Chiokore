import { useState } from 'react';
import { ROL_ADMINISTRADOR, ROL_TRABAJADOR } from '../services/api';
import { hoyISO } from '../utils/fechas';

export default function EmployeeModal({ emp, onSave, onClose }: any) {
  const [formData, setFormData] = useState({
    id: emp?.id,
    nombre: emp?.nombre || '', 
    rolId: emp?.rol?.id || ROL_TRABAJADOR,
    tipoContratoId: emp?.tipoContrato?.id || 1, 
    tarifaHora: emp?.tarifaHora ?? '',
    tipoSangre: emp?.tipoSangre || '', 
    condicionesMedicas: emp?.condicionesMedicas || '', 
    contactoEmergenciaNombre: emp?.contactoEmergenciaNombre || '', 
    requiereApoyo: emp?.requiereApoyo || false, 
    urlAvatar: emp?.urlAvatar || '',
    fechaNacimiento: emp?.fechaNacimiento || '',
    contrasena: ''
  });
  const esAdmin = formData.rolId === ROL_ADMINISTRADOR;

  return (
    <div className="fixed inset-0 bg-black/50 flex items-center justify-center p-4 z-50 print:hidden">
      <div className="bg-white rounded-2xl p-8 max-w-2xl w-full shadow-2xl max-h-[90vh] overflow-y-auto">
        <h2 className="text-3xl font-bold text-stone-800 mb-6">{emp ? 'Editar Empleado' : 'Nuevo Empleado'}</h2>
        
        <div className="grid grid-cols-2 gap-4">
          <div>
            <label className="block text-sm font-bold text-stone-600">Nombre Completo</label>
            <input type="text" className="w-full border p-2 rounded" value={formData.nombre} onChange={e => setFormData({...formData, nombre: e.target.value})} />
          </div>
          <div>
            <label className="block text-sm font-bold text-stone-600">Fecha de Nacimiento</label>
            <input type="date" className="w-full border p-2 rounded" max={hoyISO()} value={formData.fechaNacimiento} onChange={e => setFormData({...formData, fechaNacimiento: e.target.value})} />
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
            <label className="block text-sm font-bold text-stone-600">Rol</label>
            <select className="w-full border p-2 rounded" value={formData.rolId} onChange={e => setFormData({...formData, rolId: Number(e.target.value)})}>
              <option value={ROL_TRABAJADOR}>Trabajador</option><option value={ROL_ADMINISTRADOR}>Administrador</option>
            </select>
          </div>
          <div>
            <label className="block text-sm font-bold text-stone-600">Contrato</label>
            <select className="w-full border p-2 rounded" value={formData.tipoContratoId} onChange={e => setFormData({...formData, tipoContratoId: Number(e.target.value)})}>
              <option value={1}>Pagado</option><option value={2}>Voluntariado</option>
            </select>
          </div>
          {esAdmin && (
            <div className="col-span-2">
              <label className="block text-sm font-bold text-stone-600">Contraseña del Panel</label>
              <input type="password" autoComplete="new-password" className="w-full border p-2 rounded" placeholder={emp ? 'Déjala vacía para no cambiarla' : 'Necesaria para entrar al panel'} value={formData.contrasena} onChange={e => setFormData({...formData, contrasena: e.target.value})} />
            </div>
          )}
          <div>
            <label className="block text-sm font-bold text-stone-600">Tarifa por Hora ($)</label>
            <input type="number" className="w-full border p-2 rounded" value={formData.tarifaHora} onChange={e => setFormData({...formData, tarifaHora: e.target.value})} />
          </div>
          <div>
            <label className="block text-sm font-bold text-stone-600">Tipo de Sangre</label>
            <input type="text" className="w-full border p-2 rounded" value={formData.tipoSangre} onChange={e => setFormData({...formData, tipoSangre: e.target.value})} />
          </div>
          <div className="col-span-2">
            <label className="block text-sm font-bold text-stone-600">Condición</label>
            <textarea className="w-full border p-2 rounded" maxLength={1000} placeholder="Ej. autismo, discapacidad visual, diabetes (opcional)" value={formData.condicionesMedicas} onChange={e => setFormData({...formData, condicionesMedicas: e.target.value})} />
          </div>
          <div className="col-span-2">
            <label className="block text-sm font-bold text-stone-600">Contacto de Emergencia</label>
            <input type="text" className="w-full border p-2 rounded" value={formData.contactoEmergenciaNombre} onChange={e => setFormData({...formData, contactoEmergenciaNombre: e.target.value})} />
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
            fechaNacimiento: formData.fechaNacimiento || null,
            // Vacía conserva la contraseña actual; solo los administradores la usan
            contrasena: esAdmin && formData.contrasena ? formData.contrasena : null
          })} className="px-6 py-2 bg-orange-500 text-white font-bold rounded hover:bg-orange-600">Guardar</button>
        </div>
      </div>
    </div>
  );
}
