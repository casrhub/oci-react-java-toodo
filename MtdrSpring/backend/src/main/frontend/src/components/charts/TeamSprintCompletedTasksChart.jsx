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

export default function TeamSprintCompletedTasksChart({ equipoId = 1, usuarioId = null, onLoad }) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const tasksRes = await fetch(API_TAREAS);
        const allTasks = await tasksRes.json();
        let tasks = allTasks.filter((t) => t.equipoId === equipoId);
        if (usuarioId) tasks = tasks.filter((t) => t.usuarioId === usuarioId);
        const bySprint = tasks.reduce((acc, task) => {
          if (task.estado !== 'completado') return acc;
          const sprintKey = task.sprintId != null ? `Sprint ${task.sprintId}` : 'Sin Sprint';
          if (!acc[sprintKey]) {
            acc[sprintKey] = { sprint: sprintKey };
            if (usuarioId) {
              const m = teamMembers.find((mm) => mm.id === usuarioId);
              acc[sprintKey][m.name] = 0;
            } else {
              teamMembers.forEach((m) => (acc[sprintKey][m.name] = 0));
            }
          }
          const member = teamMembers.find((m) => m.id === task.usuarioId);
          if (member) acc[sprintKey][member.name] += 1;
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
    };
    fetchData();
  }, [equipoId, usuarioId]);

  useEffect(() => {
    if (!loading && onLoad) {
      onLoad();
    }
  }, [loading, onLoad]);

  if (loading) return null;
  if (error) return <Typography color="error">Error loading chart: {error.message}</Typography>;

  return (
    <div style={{ margin: '2rem 0' }}>
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
        {usuarioId
          ? 'Tareas Completadas por Sprint'
          : 'Tareas Completadas por Developer por Sprint'}
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
