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
import { API_SPRINTS, API_TEAM_KPIS } from '../../api';

const teamMembers = [
  { id: 102, name: 'Cesar Alan Silva Ramos' },
  { id: 101, name: 'Jose Maria' },
  { id: 104, name: 'Miguel Angel Barrientos Ballesteros' },
  { id: 100, name: 'Diego Iván Morales Gallardo' },
  { id: 103, name: 'Fernanda Díaz Gutiérrez' },
];

const COLORS = ['#4fc3f7', '#81c784', '#ba68c8', '#ffd54f', '#ff8a65'];

export default function TeamSprintDevHoursBarChart({
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
        const sprintRes = await fetch(API_SPRINTS);
        const sprints = await sprintRes.json();
        let results;
        if (usuarioId) {
          const member = teamMembers.find((m) => m.id === usuarioId);
          if (!member) throw new Error('Developer no encontrado');
          results = await Promise.all(
            sprints.map(async (sprint) => {
              const res = await fetch(
                `${API_TEAM_KPIS}${equipoId}/sprint/${sprint.sprintId}/usuario/${usuarioId}/horas-trabajadas`
              );
              const horas = await res.json();
              return {
                sprint: sprint.nombre ?? `Sprint ${sprint.sprintId}`,
                sprintId: sprint.sprintId,
                [member.name]: Number(horas),
              };
            })
          );
        } else {
          results = await Promise.all(
            sprints.map(async (sprint) => {
              const memberHours = await Promise.all(
                teamMembers.map(async (member) => {
                  const res = await fetch(
                    `${API_TEAM_KPIS}${equipoId}/sprint/${sprint.sprintId}/usuario/${member.id}/horas-trabajadas`
                  );
                  const horas = await res.json();
                  return { [member.name]: Number(horas) };
                })
              );
              const sprintData = memberHours.reduce((a, b) => ({ ...a, ...b }), {});
              return {
                sprint: sprint.nombre ?? `Sprint ${sprint.sprintId}`,
                sprintId: sprint.sprintId,
                ...sprintData,
              };
            })
          );
        }
        results.sort((a, b) => a.sprintId - b.sprintId);
        setData(results);
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
          ? 'Horas trabajadas por sprint'
          : 'Horas trabajadas por desarrollador por sprint'}
      </Typography>
      <ResponsiveContainer width="100%" height={340}>
        <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="sprint" />
          <YAxis
            allowDecimals={false}
            label={{ value: 'Horas', angle: -90, position: 'insideLeft' }}
          />
          <Tooltip />
          <Legend />
          {usuarioId
            ? (() => {
                const member = teamMembers.find((m) => m.id === usuarioId);
                const idx = teamMembers.findIndex((m) => m.id === usuarioId);
                return (
                  <Bar
                    dataKey={member.name}
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
