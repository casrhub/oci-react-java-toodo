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

export default function TeamSprintHoursBarChart({ equipoId = 1, chartKey, onLoad }) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const loadedRef = useRef(false);

  useEffect(() => {
    (async () => {
      try {
        const sprintRes = await fetch(API_SPRINTS);
        const sprints = await sprintRes.json();
        const results = await Promise.all(
          sprints.map(async (sprint) => {
            const horasRes = await fetch(
              `${API_TEAM_KPIS}${equipoId}/sprint/${sprint.sprintId}/horas-trabajadas`
            );
            const horas = await horasRes.json();
            return { sprint: sprint.nombre ?? `Sprint ${sprint.sprintId}`, horas: Number(horas) };
          })
        );
        setData(results);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    })();
  }, [equipoId]);

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
        Horas totales trabajadas por sprint
      </Typography>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="sprint" />
          <YAxis
            allowDecimals={false}
            label={{ value: 'Horas', angle: -90, position: 'insideLeft' }}
          />
          <Tooltip />
          <Legend />
          <Bar
            dataKey="horas"
            fill="#8884d8"
            name="Horas trabajadas"
            barSize={40}
            isAnimationActive={false}
          />
        </BarChart>
      </ResponsiveContainer>
    </div>
  );
}
