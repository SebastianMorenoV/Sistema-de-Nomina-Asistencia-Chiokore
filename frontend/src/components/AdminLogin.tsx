import { useEffect, useState, type FormEvent, type ReactNode } from 'react';
import { Link } from 'react-router-dom';
import { ArrowLeft, LogIn } from 'lucide-react';
import { getAdminToken, getApiErrorMessage, loginAdmin, setAdminToken, SESION_EXPIRADA } from '../services/api';

/** Pide usuario y contraseña de administrador antes de mostrar el panel. */
export default function AdminLogin({ children }: { children: ReactNode }) {
  const [sesionActiva, setSesionActiva] = useState(() => getAdminToken() !== null);
  const [nombre, setNombre] = useState('');
  const [contrasena, setContrasena] = useState('');
  const [error, setError] = useState<string | null>(null);
  const [enviando, setEnviando] = useState(false);

  useEffect(() => {
    const alExpirar = () => {
      setSesionActiva(false);
      setError('Tu sesión expiró. Vuelve a iniciar sesión.');
    };
    window.addEventListener(SESION_EXPIRADA, alExpirar);
    return () => window.removeEventListener(SESION_EXPIRADA, alExpirar);
  }, []);

  if (sesionActiva) return <>{children}</>;

  const entrar = async (e: FormEvent) => {
    e.preventDefault();
    setEnviando(true);
    setError(null);
    try {
      const { data } = await loginAdmin(nombre.trim(), contrasena);
      setAdminToken(data.accessToken);
      setContrasena('');
      setSesionActiva(true);
    } catch (err) {
      setError(getApiErrorMessage(err, 'No se pudo iniciar sesión.'));
    } finally {
      setEnviando(false);
    }
  };

  return (
    <div className="min-h-screen bg-stone-100 flex items-center justify-center p-4 font-sans">
      <form onSubmit={entrar} className="bg-white rounded-3xl shadow-xl border border-stone-200 p-10 w-full max-w-md">
        <h1 className="text-4xl font-black text-orange-500">Chiokore</h1>
        <p className="text-stone-400 font-bold text-sm tracking-widest mt-1 mb-8">ADMINISTRACIÓN</p>

        <label htmlFor="usuario" className="block text-sm font-bold text-stone-600 mb-1">Usuario</label>
        <input
          id="usuario"
          autoFocus
          required
          autoComplete="username"
          className="w-full border border-stone-200 p-3 rounded-lg mb-4 text-stone-800 focus:outline-none focus:border-orange-500"
          value={nombre}
          onChange={e => setNombre(e.target.value)}
        />

        <label htmlFor="contrasena" className="block text-sm font-bold text-stone-600 mb-1">Contraseña</label>
        <input
          id="contrasena"
          type="password"
          required
          autoComplete="current-password"
          className="w-full border border-stone-200 p-3 rounded-lg mb-6 text-stone-800 focus:outline-none focus:border-orange-500"
          value={contrasena}
          onChange={e => setContrasena(e.target.value)}
        />

        {error && <p className="mb-6 rounded-lg bg-red-50 px-4 py-3 text-sm font-bold text-red-700">{error}</p>}

        <button type="submit" disabled={enviando} className="w-full bg-orange-500 hover:bg-orange-600 disabled:opacity-60 text-white py-3 rounded-lg font-bold flex items-center justify-center gap-2 transition-colors">
          <LogIn size={20} /> {enviando ? 'Entrando...' : 'Entrar'}
        </button>

        <Link to="/" className="mt-6 flex items-center justify-center gap-2 text-stone-500 hover:text-stone-800 font-bold transition-colors">
          <ArrowLeft size={18} /> Volver al Checador
        </Link>
      </form>
    </div>
  );
}
