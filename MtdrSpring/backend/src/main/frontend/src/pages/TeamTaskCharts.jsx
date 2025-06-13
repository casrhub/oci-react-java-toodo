// File: MtdrSpring/backend/src/main/frontend/src/pages/TeamTaskCharts.jsx
import React, { useEffect, useState, useRef } from 'react';
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
import { Typography } from '@mui/material';
import { API_TEAM_KPIS } from '../api';

export default function TeamTaskCharts({ equipoId, chartKey, onLoad }) {
  const [summary, setSummary] = useState(null);
  const [err, setErr] = useState(null);
  const loadedRef = useRef(false);

  useEffect(() => {
    (async () => {
      try {
        const r = await fetch(`${API_TEAM_KPIS}${equipoId}/summary`);
        if (!r.ok) throw new Error(`Error HTTP ${r.status}`);
        const ct = r.headers.get('content-type') || '';
        const payload = ct.includes('application/json')
          ? await r.json()
          : Promise.reject('No es JSON');
        setSummary(payload);
      } catch (e) {
        setErr(e);
      }
    })();
  }, [equipoId]);

  useEffect(() => {
    if (summary && !loadedRef.current) {
      loadedRef.current = true;
      if (onLoad) onLoad(chartKey);
    }
  }, [summary, onLoad, chartKey]);

  if (err)
    return (
      <div style={{ color: 'red', padding: '1rem' }}>
        <Typography variant="h6">Error al cargar KPIs del equipo</Typography>
        <pre>{String(err.message || err)}</pre>
      </div>
    );

  if (!summary) return null;

  const data = [
    {
      name: 'Tareas completadas',
      'Antes de la fecha límite': summary.completadasAntes,
      'Después de la fecha límite': summary.completadasDespues,
    },
  ];

  return (
    <div style={{ marginTop: '2rem' }}>
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
        Tareas completadas (antes vs. después de la fecha límite)
      </Typography>
      <ResponsiveContainer width="100%" height={260}>
        <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="name" />
          <YAxis allowDecimals={false} />
          <Tooltip />
          <Legend />
          <Bar
            dataKey="Antes de la fecha límite"
            fill="#2EAD5F"
            barSize={40}
            isAnimationActive={false}
          />
          <Bar
            dataKey="Después de la fecha límite"
            fill="#C74634"
            barSize={40}
            isAnimationActive={false}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
