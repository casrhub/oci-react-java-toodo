import React, { useEffect, useState } from 'react';
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer, CartesianGrid } from 'recharts';
import { CircularProgress, Typography } from '@mui/material';
import { API_TAREAS } from '../../api';

// Hardcoded team members for Equipo 1 (same as in ManagerKpisPage)
const teamMembers = [
  { id: 102, name: 'Cesar Alan Silva Ramos' },
  { id: 101, name: 'Jose Maria' },
  { id: 104, name: 'Miguel Angel Barrientos Ballesteros' },
  { id: 100, name: 'Diego Iván Morales Gallardo' },
  { id: 103, name: 'Fernanda Díaz Gutiérrez' }
];

const COLORS = ['#4fc3f7', '#81c784', '#ba68c8', '#ffd54f', '#ff8a65'];

function TeamSprintCompletedTasksChart({ equipoId = 1 }) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        // Fetch all tasks
        const tasksRes = await fetch(API_TAREAS);
        const allTasks = await tasksRes.json();
        console.log('All tasks:', allTasks);

        // Filter tasks for the specified team
        const teamTasks = allTasks.filter(task => task.equipoId === equipoId);
        console.log('Team tasks:', teamTasks);

        // Group tasks by sprint
        const tasksBySprint = teamTasks.reduce((acc, task) => {
          // Get sprint ID from the task
          const sprintId = task.sprintId;
          if (!sprintId) {
            // If no sprint ID, group under "Sin Sprint"
            if (!acc['no-sprint']) {
              acc['no-sprint'] = {
                sprint: 'Sin Sprint',
                ...teamMembers.reduce((obj, member) => ({ ...obj, [member.name]: 0 }), {})
              };
            }
            
            if (task.estado === 'completado') {
              const member = teamMembers.find(m => m.id === task.usuarioId);
              if (member) {
                acc['no-sprint'][member.name]++;
              }
            }
            return acc;
          }

          if (!acc[sprintId]) {
            acc[sprintId] = {
              sprint: `Sprint ${sprintId}`,
              ...teamMembers.reduce((obj, member) => ({ ...obj, [member.name]: 0 }), {})
            };
          }

          if (task.estado === 'completado') {
            const member = teamMembers.find(m => m.id === task.usuarioId);
            if (member) {
              acc[sprintId][member.name]++;
            }
          }

          return acc;
        }, {});

        // Sort sprints by ID, keeping "Sin Sprint" at the end
        const results = Object.values(tasksBySprint).sort((a, b) => {
          if (a.sprint === 'Sin Sprint') return 1;
          if (b.sprint === 'Sin Sprint') return -1;
          const sprintA = parseInt(a.sprint.split(' ')[1]);
          const sprintB = parseInt(b.sprint.split(' ')[1]);
          return sprintA - sprintB;
        });

        console.log('Final chart data:', results);
        setData(results);
      } catch (err) {
        console.error('Error in TeamSprintCompletedTasksChart:', err);
        setError(err);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, [equipoId]);

  if (loading) return <CircularProgress />;
  if (error) return <Typography color="error">Error loading chart: {error.message}</Typography>;

  return (
    <div style={{ margin: '2rem 0' }}>
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
        Tareas Completadas por Developer por Sprint
      </Typography>
      <ResponsiveContainer width="100%" height={340}>
        <BarChart data={data} margin={{ top: 20, right: 30, left: 0, bottom: 5 }}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="sprint" />
          <YAxis allowDecimals={false} label={{ value: 'Tareas completadas', angle: -90, position: 'insideLeft' }} />
          <Tooltip />
          <Legend />
          {teamMembers.map((member, idx) => (
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

export default TeamSprintCompletedTasksChart; 