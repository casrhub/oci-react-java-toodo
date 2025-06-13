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
import { API_TAREAS } from '../../api';

const teamMembers = [
  { id: 102, name: 'Cesar Alan Silva Ramos' },
  { id: 101, name: 'Jose Maria' },
  { id: 104, name: 'Miguel Angel Barrientos Ballesteros' },
  { id: 100, name: 'Diego Iván Morales Gallardo' },
  { id: 103, name: 'Fernanda Díaz Gutiérrez' },
];

const COLORS = ['#4fc3f7', '#81c784', '#ba68c8', '#ffd54f', '#ff8a65'];

export default function TeamSprintCompletedTasksChart({
  equipoId = 1,
  usuarioId = null,
  chartKey,
  onLoad,
}) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const loadedRef = useRef(false);

  useEffect(() => {
    (async () => {
      try {
        const res = await fetch(API_TAREAS);
        const tasks = await res.json();
        let list = tasks.filter((t) => t.equipoId === equipoId && t.estado === 'completado');
        if (usuarioId) list = list.filter((t) => t.usuarioId === usuarioId);
        const bySprint = list.reduce((acc, t) => {
          const k = t.sprintId != null ? `Sprint ${t.sprintId}` : 'Sin Sprint';
          if (!acc[k]) {
            acc[k] = { sprint: k };
            if (usuarioId) {
              const m = teamMembers.find((mm) => mm.id === usuarioId);
              acc[k][m.name] = 0;
            } else {
              teamMembers.forEach((m) => (acc[k][m.name] = 0));
            }
          }
          const member = teamMembers.find((m) => m.id === t.usuarioId);
          if (member) acc[k][member.name] += 1;
          return acc;
        }, {});
        const result = Object.values(bySprint).sort((a, b) => {
          if (a.sprint === 'Sin Sprint') return 1;
          if (b.sprint === 'Sin Sprint') return -1;
          return Number(a.sprint.split(' ')[1]) - Number(b.sprint.split(' ')[1]);
        });
        setData(result);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    })();
  }, [equipoId, usuarioId]);

  useEffect(() => {
    if (!loading && !loadedRef.current) {
      loadedRef.current = true;
      if (onLoad) onLoad(chartKey);
    }
  }, [loading, onLoad, chartKey]);

  if (loading) return null;
  if (error)
    return <Typography color="error">Error al cargar la gráfica: {error.message}</Typography>;

  return (
    <div style={{ margin: '2rem 0' }}>
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
        {usuarioId
          ? 'Tareas completadas por sprint'
          : 'Tareas completadas por desarrollador por sprint'}
      </Typography>
      <ResponsiveContainer width="100%" height={340}>
        <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="sprint" />
          <YAxis
            allowDecimals={false}
            label={{ value: 'Tareas', angle: -90, position: 'insideLeft' }}
          />
          <Tooltip />
          <Legend />
          {usuarioId
            ? (() => {
                const m = teamMembers.find((mm) => mm.id === usuarioId);
                const idx = teamMembers.findIndex((mm) => mm.id === usuarioId);
                return (
                  <Bar
                    dataKey={m.name}
                    fill={COLORS[idx % COLORS.length]}
                    barSize={30}
                    isAnimationActive={false}
                  />
                );
              })()
            : teamMembers.map((m, idx) => (
                <Bar
                  key={m.id}
                  dataKey={m.name}
                  fill={COLORS[idx % COLORS.length]}
                  barSize={30}
                  isAnimationActive={false}
                />
              ))}
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
