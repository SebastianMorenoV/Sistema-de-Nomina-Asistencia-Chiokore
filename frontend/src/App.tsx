import { Routes, Route } from 'react-router-dom';
import Checador from './pages/Checador';
import AdminDashboard from './pages/AdminDashboard';

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Checador />} />
      <Route path="/admin" element={<AdminDashboard />} />
    </Routes>
  );
}