import React, { useState } from "react";
import { Button, TextField } from '@mui/material';

function NewItem(props) {
  const [titulo, setTitulo] = useState('');
  const [descripcion, setDescripcion] = useState('');
  const [usuarioId, setUsuarioId] = useState('');
  const [equipoId, setEquipoId] = useState('');
  const [proyectoId, setProyectoId] = useState('');
  const [horasEstimadas, setHorasEstimadas] = useState('');

  function handleSubmit(e) {
    e.preventDefault();
    if (!titulo.trim() || !descripcion.trim()) return;

    props.addItem(
      titulo,
      descripcion,
      parseInt(usuarioId),
      parseInt(equipoId),
      parseInt(proyectoId),
      parseFloat(horasEstimadas)
    );

    // Reset all fields
    setTitulo('');
    setDescripcion('');
    setUsuarioId('');
    setEquipoId('');
    setProyectoId('');
    setHorasEstimadas('');
  }

  return (
    <div id="newinputform">
      <form onSubmit={handleSubmit}>
        <TextField label="Título" value={titulo} onChange={e => setTitulo(e.target.value)} fullWidth />
        <TextField label="Descripción" value={descripcion} onChange={e => setDescripcion(e.target.value)} fullWidth />
        <TextField label="Usuario ID" value={usuarioId} onChange={e => setUsuarioId(e.target.value)} type="number" fullWidth />
        <TextField label="Equipo ID" value={equipoId} onChange={e => setEquipoId(e.target.value)} type="number" fullWidth />
        <TextField label="Proyecto ID" value={proyectoId} onChange={e => setProyectoId(e.target.value)} type="number" fullWidth />
        <TextField label="Horas Estimadas" value={horasEstimadas} onChange={e => setHorasEstimadas(e.target.value)} type="number" fullWidth />

        <Button
          className="AddButton"
          variant="contained"
          disabled={props.isInserting}
          onClick={!props.isInserting ? handleSubmit : null}
          size="small"
          style={{ marginTop: '10px' }}
        >
          {props.isInserting ? 'Adding…' : 'Add'}
        </Button>
      </form>
    </div>
  );
}

export default NewItem;
