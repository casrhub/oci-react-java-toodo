// src/pages/DevTasksPage.jsx
import React, { useState, useEffect } from 'react';
import {
  Table, TableHead, TableBody, TableRow, TableCell,
  TableContainer, Paper, Button, Toolbar, Menu, MenuItem,
  Typography, Dialog, DialogTitle, DialogContent, DialogActions,
  TextField, CircularProgress, FormControl, InputLabel, Select
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import FilterListIcon from '@mui/icons-material/FilterList';
import Moment from 'react-moment';

import { API_TAREAS, API_SUBTAREAS, API_USUARIOS } from '../api';
import NewItem from '../components/tasks/NewItem';

/* ────────────────────────────────────────────────────────── */
export default function DevTasksPage() {
  /* ---------- STATE ---------- */
  const [tasks, setTasks]   = useState([]);
  const [users, setUsers]   = useState([]);       // {id, nombre}
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(null);

  /* dialogs */
  const [newDlgOpen, setNewDlgOpen] = useState(false);
  const [inserting, setInserting]   = useState(false);

  const [deadlineDlg, setDeadlineDlg] = useState({ open: false, task: null, value: '' });
  const [completeDlg, setCompleteDlg] = useState({ open: false, task: null, hours: '' });

  /* subtasks & menu */
  const [expanded, setExpanded] = useState(null);
  const [anchorEl, setAnchorEl] = useState(null);

  /* ---------- EFFECTS ---------- */
  useEffect(() => {
    Promise.all([fetchTasks(), fetchUsers()]).finally(() => setLoading(false));
  }, []);

  /* ---------- API ---------- */
  const fetchTasks = () =>
    fetch(API_TAREAS).then(r=>r.ok?r.json():Promise.reject('tasks')).then(setTasks).catch(setError);

  const fetchUsers = () =>
    fetch(API_USUARIOS)
      .then(r=>r.ok?r.json():Promise.reject('users'))
      .then(arr => setUsers(arr.map(u=>({ id: u.usuario_id ?? u.usuarioId ?? u.id, nombre: u.nombre }))))
      .catch(setError);

  const reloadOne = (id) =>
    fetch(`${API_TAREAS}/${id}`)
      .then(r=>r.ok?r.json():Promise.reject())
      .then(t=>setTasks(p=>p.map(x=>x.tareaId===id?{...x,...t}:x)))
      .catch(setError);

  const addItem = (tit, desc, uid, eq, pid, hrs) => {
    setInserting(true);
    const body = {
      titulo: tit, descripcion: desc, usuarioId: uid,
      equipoId: eq, proyectoId: pid,
      horasEstimadas: Math.min(hrs,4),
      estado:'pendiente',
      fechaCreacion:new Date().toISOString()
    };
    fetch(API_TAREAS,{method:'POST',headers:{'Content-Type':'application/json'},body:JSON.stringify(body)})
      .then(r=>r.ok?r.json():Promise.reject())
      .then(fetchTasks)
      .catch(setError)
      .finally(()=>{ setInserting(false); setNewDlgOpen(false); });
  };

  const updateAssignee = (tid, uid) =>
    fetch(`${API_TAREAS}/${tid}`,{
      method:'PUT',headers:{'Content-Type':'application/json'},
      body:JSON.stringify({usuarioId:uid})
    }).then(r=>r.ok?reloadOne(tid):Promise.reject()).catch(setError);

  const deleteTask = (tid) =>
    fetch(`${API_TAREAS}/${tid}`,{method:'DELETE'}).then(fetchTasks).catch(setError);

  const markDone = (tid, hrs) =>
    fetch(`${API_TAREAS}/${tid}/complete`,{
      method:'PUT',headers:{'Content-Type':'application/json'},
      body:JSON.stringify({estado:'completado',horasReales:hrs})
    }).then(r=>r.ok?reloadOne(tid):Promise.reject()).catch(setError);

  const setDeadline = (tid, iso) =>
    fetch(`${API_TAREAS}/${tid}/deadline`,{
      method:'PUT',headers:{'Content-Type':'application/json'},
      body:JSON.stringify({deadline:iso})
    }).then(r=>r.ok?reloadOne(tid):Promise.reject()).catch(setError);

  /* ---------- HELPERS ---------- */
  const renderAssignee = (task) => (
    <FormControl size="small" fullWidth>
      <InputLabel id={`ass-${task.tareaId}`}>Asignado</InputLabel>
      <Select
        labelId={`ass-${task.tareaId}`}
        value={task.usuarioId!=null?String(task.usuarioId):''}
        label="Asignado"
        onChange={e=>updateAssignee(task.tareaId,e.target.value===''?null:Number(e.target.value))}
      >
        <MenuItem value=""><em>No asignado</em></MenuItem>
        {users.map(u=><MenuItem key={u.id} value={String(u.id)}>{u.nombre}</MenuItem>)}
      </Select>
    </FormControl>
  );

  const openDeadline = (t) =>
    setDeadlineDlg({ open:true, task:t, value:t.deadline?new Date(t.deadline).toISOString().slice(0,16):'' });

  const saveDeadline = () => {
    const {task,value}=deadlineDlg;
    const iso = new Date(value).toISOString();
    setDeadline(task.tareaId, iso);
    setDeadlineDlg({open:false,task:null,value:''});
  };

  const openComplete = (t) => setCompleteDlg({open:true,task:t,hours:''});

  const confirmComplete = () => {
    markDone(completeDlg.task.tareaId, completeDlg.hours);
    setCompleteDlg({open:false,task:null,hours:''});
  };

  const toggleExpand = (id) => setExpanded(expanded===id?null:id);

  /* ---------- RENDER ---------- */
  const pending   = tasks.filter(t=>t.estado!=='completado');
  const completed = tasks.filter(t=>t.estado==='completado');

  if (loading) return <CircularProgress sx={{m:4}} />;
  if (error)   return <Typography color="error">{error.toString()}</Typography>;

  return (
    <div style={{padding:16}}>
      <Toolbar sx={{justifyContent:'space-between'}}>
        <Typography variant="h5" fontWeight="bold">My Tasks</Typography>
        <div>
          <Button variant="outlined" startIcon={<FilterListIcon/>}
                  onClick={e=>setAnchorEl(e.currentTarget)} sx={{mr:2}}>Filter</Button>
          <Menu anchorEl={anchorEl} open={Boolean(anchorEl)} onClose={()=>setAnchorEl(null)}>
            <MenuItem onClick={()=>setAnchorEl(null)}>No filters yet</MenuItem>
          </Menu>
          <Button startIcon={<AddIcon/>} variant="contained"
                  onClick={()=>setNewDlgOpen(true)}
                  sx={{bgcolor:'#C74634','&:hover':{bgcolor:'#b63f2e'}}}>
            Add Task
          </Button>
        </div>
      </Toolbar>

      {/* ---------- Pending ---------- */}
      {pending.length>0 && (
        <>
          <Typography variant="h6" sx={{mt:3}}>Pending</Typography>
          <TableContainer component={Paper} sx={{mt:1}}>
            <Table size="small">
              <TableHead sx={{bgcolor:'#C74634'}}>
                <TableRow>
                  {['#','Title','Assignee','Status','Deadline','Actions'].map(h=>(
                    <TableCell key={h} sx={{color:'white',fontWeight:'bold'}}>{h}</TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {pending.map((t,i)=>(
                  <React.Fragment key={t.tareaId}>
                    <TableRow>
                      <TableCell>{i+1}</TableCell>
                      <TableCell>
                        <Button onClick={()=>toggleExpand(t.tareaId)}>{t.titulo}</Button>
                      </TableCell>
                      <TableCell>{renderAssignee(t)}</TableCell>
                      <TableCell>{t.estado==='en progreso'?'In Progress':'To Do'}</TableCell>
                      <TableCell>
                        {t.deadline
                          ? <Moment format="DD/MM/YYYY HH:mm" utc>{t.deadline}</Moment>
                          : <Button size="small" onClick={()=>openDeadline(t)}>Set</Button>}
                      </TableCell>
                      <TableCell>
                        <Button size="small" variant="contained" onClick={()=>openComplete(t)}>
                          Done
                        </Button>
                      </TableCell>
                    </TableRow>
                    {expanded===t.tareaId && (
                      <TableRow>
                        <TableCell colSpan={6} sx={{bgcolor:'#fafafa'}}>
                          <strong>Description: </strong>{t.descripcion || '—'}
                        </TableCell>
                      </TableRow>
                    )}
                  </React.Fragment>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      )}

      {/* ---------- Completed ---------- */}
      {completed.length>0 && (
        <>
          <Typography variant="h6" sx={{mt:4}}>Completed</Typography>
          <TableContainer component={Paper} sx={{mt:1}}>
            <Table size="small">
              <TableHead sx={{bgcolor:'#C74634'}}>
                <TableRow>
                  {['#','Title','Assignee','Status','Deadline','Actions'].map(h=>(
                    <TableCell key={h} sx={{color:'white',fontWeight:'bold'}}>{h}</TableCell>
                  ))}
                </TableRow>
              </TableHead>
              <TableBody>
                {completed.map((t,i)=>(
                  <TableRow key={t.tareaId}>
                    <TableCell>{i+1}</TableCell>
                    <TableCell>{t.titulo}</TableCell>
                    <TableCell>{renderAssignee(t)}</TableCell>
                    <TableCell>Done</TableCell>
                    <TableCell>
                      <Moment format="DD/MM/YYYY HH:mm" utc>{t.deadline}</Moment>
                    </TableCell>
                    <TableCell>
                      <Button startIcon={<DeleteIcon/>} color="error"
                              onClick={()=>deleteTask(t.tareaId)}>
                        Delete
                      </Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      )}

      {/* ---------- Dialogs ---------- */}
      <Dialog open={newDlgOpen} onClose={()=>setNewDlgOpen(false)} fullWidth maxWidth="sm">
        <DialogTitle>Nueva Tarea</DialogTitle>
        <DialogContent>
          <NewItem addItem={addItem} isInserting={inserting} users={users}/>
        </DialogContent>
      </Dialog>

      <Dialog open={deadlineDlg.open} onClose={()=>setDeadlineDlg({open:false,task:null,value:''})}>
        <DialogTitle>Set Deadline</DialogTitle>
        <DialogContent>
          <TextField type="datetime-local" fullWidth
                     value={deadlineDlg.value}
                     onChange={e=>setDeadlineDlg({...deadlineDlg,value:e.target.value})}/>
        </DialogContent>
        <DialogActions>
          <Button onClick={()=>setDeadlineDlg({open:false,task:null,value:''})}>Cancel</Button>
          <Button onClick={saveDeadline}>Save</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={completeDlg.open} onClose={()=>setCompleteDlg({open:false,task:null,hours:''})}>
        <DialogTitle>Complete Task</DialogTitle>
        <DialogContent>
          <TextField type="number" fullWidth label="Real hours"
                     value={completeDlg.hours}
                     onChange={e=>setCompleteDlg({...completeDlg,hours:e.target.value})}/>
        </DialogContent>
        <DialogActions>
          <Button onClick={()=>setCompleteDlg({open:false,task:null,hours:''})}>Cancel</Button>
          <Button onClick={confirmComplete}>Confirm</Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}
