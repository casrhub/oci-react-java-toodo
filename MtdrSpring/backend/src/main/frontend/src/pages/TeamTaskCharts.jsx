import React, { useEffect, useState } from 'react';
import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  Tooltip,
  Legend,
  ResponsiveContainer,
  CartesianGrid,
} from 'recharts';
import { CircularProgress, Typography } from '@mui/material';
import { API_TEAM_KPIS } from '../api';

/**
 * Gráfica de tareas completadas por el equipo:
 *   • Una barra = Completadas ANTES del deadline
 *   • Otra barra = Completadas DESPUÉS del deadline
 *
 * @param {number} equipoId – ID del equipo (por defecto lo pasa la página)
 */
function TeamTaskCharts({ equipoId }) {
  const [summary, setSummary] = useState(null);
  const [err, setErr] = useState(null);

  /* ─── Fetch resumen ───────────────────────────────────── */
  useEffect(() => {
    fetch(`${API_TEAM_KPIS}${equipoId}/summary`)
      .then(async (r) => {
        if (!r.ok) throw new Error(`HTTP ${r.status} – ${await r.text()}`);
        const ct = r.headers.get('content-type') || '';
        return ct.includes('application/json') ? r.json() : Promise.reject('Respuesta no-JSON');
      })
      .then(setSummary)
      .catch(setErr);
  }, [equipoId]);

  /* ─── Estados de carga / error ────────────────────────── */
  if (err)
    return (
      <div style={{ color: 'red', padding: '1rem' }}>
        <h3>Error al cargar KPIs del equipo</h3>
        <pre>{String(err.message || err)}</pre>
      </div>
    );

  if (!summary) return <CircularProgress />;

  /* ─── Datos para la gráfica ───────────────────────────── */
  const data = [
    {
      name: 'Tareas Completadas',
      'Antes del deadline': summary.completadasAntes,
      'Después del deadline': summary.completadasDespues,
    },
  ];

  /* ─── UI ──────────────────────────────────────────────── */
  return (
    <div style={{ marginTop: '2rem' }}>
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
        Tareas Completadas (Antes vs. Después del Deadline)
      </Typography>

      <ResponsiveContainer width="100%" height={260}>
        <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Legend />
          <Bar dataKey="Antes del deadline" fill="#2EAD5F" barSize={40} />
          <Bar dataKey="Después del deadline" fill="#C74634" barSize={40} />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default TeamTaskCharts;
