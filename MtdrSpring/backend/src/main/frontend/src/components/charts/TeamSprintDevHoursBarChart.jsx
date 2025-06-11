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
import { API_SPRINTS, API_TEAM_KPIS } from '../../api';

// ─── Miembros (hard-codeados para Equipo 1) ──────────────────────────────────
const teamMembers = [
  { id: 102, name: 'Cesar Alan Silva Ramos' },
  { id: 101, name: 'Jose Maria' },
  { id: 104, name: 'Miguel Angel Barrientos Ballesteros' },
  { id: 100, name: 'Diego Iván Morales Gallardo' },
  { id: 103, name: 'Fernanda Díaz Gutiérrez' },
];

const COLORS = ['#4fc3f7', '#81c784', '#ba68c8', '#ffd54f', '#ff8a65'];

/**
 * ● Sin `usuarioId`  →  Gráfica de todas las barras (horas por developer).
 * ● Con  `usuarioId`  →  Gráfica de una sola barra (horas del developer).
 */
function TeamSprintDevHoursBarChart({ equipoId = 1, usuarioId = null }) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const sprintRes = await fetch(API_SPRINTS);
        const sprints = await sprintRes.json();

        let results;

        // ─── Vista de DEVELOPER ────────────────────────────────────────────
        if (usuarioId) {
          const member = teamMembers.find((m) => m.id === usuarioId);
          if (!member) throw new Error('Developer no encontrado en la lista local.');

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
        }
        // ─── Vista de EQUIPO (comportamiento original) ────────────────────
        else {
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
              const sprintData = memberHours.reduce((acc, cur) => ({ ...acc, ...cur }), {});
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
    };

    fetchData();
  }, [equipoId, usuarioId]);

  if (loading) return <CircularProgress />;
  if (error) return <Typography color="error">Error loading chart: {error.message}</Typography>;

  /* ────────────────────────────────────────────────────────────────────────── */
  return (
    <div style={{ margin: '2rem 0' }}>
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
        {usuarioId ? 'Horas Trabajadas por Sprint' : 'Horas Trabajadas por Developer por Sprint'}
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
                const colorIndex = teamMembers.findIndex((m) => m.id === usuarioId);
                return (
                  <Bar
                    dataKey={member.name}
                    fill={COLORS[colorIndex % COLORS.length]}
                    name={member.name}
                    barSize={30}
                  />
                );
              })()
            : teamMembers.map((member, idx) => (
                <Bar
                  key={member.id}
                  dataKey={member.name}
                  fill={COLORS[idx % COLORS.length]}
                  name={member.name}
                  barSize={30}
                />
              ))}
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}

export default TeamSprintDevHoursBarChart;
