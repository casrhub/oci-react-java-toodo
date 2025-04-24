/* eslint-disable react/prop-types */      // ← IGNORA “missing in props validation”
/* eslint-disable @typescript-eslint/no-empty-function */
import React, { useState } from 'react';
import {
  Box, TextField, FormControl,
  InputLabel, Select, MenuItem, Button,
} from '@mui/material';

export default function NewItem({ addItem, isInserting, users }) {
  /* ---- local state ---- */
  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [usuarioId, setUsuarioId] = useState('');  // '' | '101'
  const [equipoId, setEquipoId]   = useState('');
  const [proyectoId, setProyectoId] = useState('');
  const [horas, setHoras] = useState('');

  /* ---- submit ---- */
  const handleSubmit = (e) => {
    e.preventDefault();
    if (!titulo.trim() || !descripcion.trim() || usuarioId === '') return;

    addItem(
      titulo,
      descripcion,
      Number(usuarioId),
      equipoId   ? Number(equipoId)   : null,
      proyectoId ? Number(proyectoId) : null,
      Number(horas) || 0,
    );

    // reset
    setTitulo('');
    setDescripcion('');
    setUsuarioId('');
    setEquipoId('');
    setProyectoId('');
    setHoras('');
  };

  /* ---- UI ---- */
  return (
    <Box
      component="form"
      onSubmit={handleSubmit}
      sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}
    >
      <TextField
        label="Título"
        required
        value={titulo}
        onChange={(e) => setTitulo(e.target.value)}
      />
      <TextField
        label="Descripción"
        multiline
        rows={3}
        value={descripcion}
        onChange={(e) => setDescripcion(e.target.value)}
      />

      <FormControl required size="small" fullWidth>
        <InputLabel id="dev-label">Asignar a</InputLabel>
        <Select
          labelId="dev-label"
          value={usuarioId}
          label="Asignar a"
          onChange={(e) => setUsuarioId(e.target.value)}
          renderValue={(val) =>
            val === ''
              ? <em style={{ color: '#888' }}>Selecciona un dev…</em>
              : users.find((u) => String(u.id) === val)?.nombre
          }
        >
          <MenuItem value="">
            <em>Selecciona un dev…</em>
          </MenuItem>
          {users.map((u) => (
            <MenuItem key={u.id} value={String(u.id)}>
              {u.nombre}
            </MenuItem>
          ))}
        </Select>
      </FormControl>

      <TextField
        label="Equipo ID"
        type="number"
        value={equipoId}
        onChange={(e) => setEquipoId(e.target.value)}
      />
      <TextField
        label="Proyecto ID"
        type="number"
        value={proyectoId}
        onChange={(e) => setProyectoId(e.target.value)}
      />
      <TextField
        label="Horas Estimadas"
        required
        type="number"
        value={horas}
        onChange={(e) => setHoras(e.target.value)}
      />

      <Button
        type="submit"
        variant="contained"
        disabled={isInserting || usuarioId === ''}
      >
        {isInserting ? 'Adding…' : 'Add'}
      </Button>
    </Box>
  );
}
