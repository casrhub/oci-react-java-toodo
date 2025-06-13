import React, { useEffect, useState } from 'react';
import {
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
} from '@mui/material';
import { API_SPRINTS, API_TEAM_KPIS } from '../api';

export default function TeamKpiReport({ equipoId, onLoad }) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchData = async () => {
      try {
        const sprintRes = await fetch(API_SPRINTS);
        const sprints = await sprintRes.json();
        const results = await Promise.all(
          sprints.map(async (sprint) => {
            const [horasRes, tareasRes] = await Promise.all([
              fetch(`${API_TEAM_KPIS}${equipoId}/sprint/${sprint.sprintId}/horas-trabajadas`),
              fetch(`${API_TEAM_KPIS}${equipoId}/sprint/${sprint.sprintId}/tareas-completadas`),
            ]);
            const horas = await horasRes.json();
            const tareas = await tareasRes.json();
            return {
              sprintId: sprint.sprintId,
              sprintNombre: sprint.nombre ?? `Sprint ${sprint.sprintId}`,
              horasTrabajadas: Number(horas),
              tareasCompletadas: Number(tareas),
            };
          })
        );
        setData(results);
      } catch (err) {
        setError(err);
      } finally {
        setLoading(false);
      }
    };
    fetchData();
  }, [equipoId]);

  useEffect(() => {
    if (!loading && onLoad) {
      onLoad();
    }
  }, [loading, onLoad]);

  if (loading) return null;
  if (error) return <Typography color="error">Error loading report: {error.message}</Typography>;

  return (
    <div style={{ marginTop: '2rem' }}>
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
        Reporte por Sprint (Equipo)
      </Typography>
      <TableContainer component={Paper}>
        <Table>
          <TableHead sx={{ backgroundColor: '#f5f5f5' }}>
            <TableRow>
              <TableCell>
                <strong>Sprint</strong>
              </TableCell>
              <TableCell>
                <strong>Horas Trabajadas</strong>
              </TableCell>
              <TableCell>
                <strong>Tareas Completadas</strong>
              </TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {Array.isArray(data) &&
              data.map((row) => (
                <TableRow key={row.sprintId}>
                  <TableCell>{row.sprintNombre}</TableCell>
                  <TableCell>{row.horasTrabajadas}</TableCell>
                  <TableCell>{row.tareasCompletadas}</TableCell>
                </TableRow>
              ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}
