import React, { useEffect, useState, useRef } from 'react';
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
import { API_SPRINTS, API_USER_KPIS } from '../api';

export default function UserKpiReport({ usuarioId, chartKey, onLoad }) {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const loadedRef = useRef(false);

  useEffect(() => {
    (async () => {
      try {
        const sRes = await fetch(API_SPRINTS);
        const sprints = await sRes.json();
        const results = await Promise.all(
          sprints.map(async (sprint) => {
            const [hRes, tRes] = await Promise.all([
              fetch(`${API_USER_KPIS}${usuarioId}/sprint/${sprint.sprintId}/horas-trabajadas`),
              fetch(`${API_USER_KPIS}${usuarioId}/sprint/${sprint.sprintId}/tareas-completadas`),
            ]);
            const horas = await hRes.json();
            const tareas = await tRes.json();
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
    })();
  }, [usuarioId]);

  useEffect(() => {
    if (!loading && !loadedRef.current) {
      loadedRef.current = true;
      if (onLoad) onLoad(chartKey);
    }
  }, [loading, onLoad, chartKey]);

  if (loading) return null;
  if (error) return <Typography color="error">Error loading report: {error.message}</Typography>;

  return (
    <div style={{ marginTop: '2rem' }}>
      <Typography variant="h6" sx={{ fontWeight: 'bold', mb: 2 }}>
        Reporte por Sprint
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
            {data.map((r) => (
              <TableRow key={r.sprintId}>
                <TableCell>{r.sprintNombre}</TableCell>
                <TableCell>{r.horasTrabajadas}</TableCell>
                <TableCell>{r.tareasCompletadas}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}
