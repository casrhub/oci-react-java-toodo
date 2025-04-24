// src/pages/DevTasksPage.jsx
import React, { useState, useEffect } from 'react';
import {
  Table, TableHead, TableBody, TableRow, TableCell,
  TableContainer, Paper, Button, Menu, MenuItem, Toolbar,
  Typography, Dialog, DialogTitle, DialogContent,
  DialogActions, TextField, CircularProgress,
  FormControl, InputLabel, Select, Box
} from '@mui/material';
import FilterListIcon from '@mui/icons-material/FilterList';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import Moment from 'react-moment';

import { API_TAREAS, API_SUBTAREAS, API_USUARIOS } from '../api';
import NewItem from '../components/tasks/NewItem';

/* ────────────────────────────────────────────────────────── */
export default function DevTasksPage() {
  /* ---------- STATE ---------- */
  const [tasks, setTasks]   = useState([]);
  const [users, setUsers]   = useState([]);          // {id,nombre}
  const [loading, setLoading] = useState(true);
  const [error, setError]     = useState(null);

  /* dialogs */
  const [newDlg, setNewDlg] = useState(false);
  const [inserting, setInserting] = useState(false);
  const [deadlineDlg, setDeadlineDlg] = useState({open:false,task:null,val:''});
  const [completeDlg, setCompleteDlg] = useState({open:false,task:null,hours:''});
  const [pendingSplit, setPendingSplit] = useState(null);

  /* subtasks / filter */
  const [expanded, setExpanded] = useState(null);
  const [anchorEl, setAnchorEl] = useState(null);

  /* new-subtask fields */
  const [newSubTitle, setNewSubTitle] = useState('');
  const [newSubHours, setNewSubHours] = useState('');

  /* ---------- EFFECTS ---------- */
  useEffect(() => {
    Promise.all([fetchTasks(), fetchUsers()]).finally(() => setLoading(false));
  }, []);

  /* ---------- API ---------- */
  const fetchTasks = () =>
    fetch(API_TAREAS).then(r => r.ok ? r.json() : Promise.reject())
      .then(setTasks).catch(setError);

  const fetchUsers = () =>
    fetch(API_USUARIOS).then(r => r.ok ? r.json() : Promise.reject())
      .then(arr => setUsers(arr.map(u => ({
        id: u.usuario_id ?? u.usuarioId ?? u.id,
        nombre: u.nombre
      }))))
      .catch(setError);

  const reloadOne = (id) =>
    fetch(`${API_TAREAS}/${id}`).then(r => r.ok ? r.json() : Promise.reject())
      .then(t => setTasks(p => p.map(x => x.tareaId === id ? { ...x, ...t } : x)))
      .catch(setError);

  const fetchSubs = (tid) =>
    fetch(`${API_SUBTAREAS}?tareaId=${tid}`).then(r => r.ok ? r.json() : Promise.reject());

  const addSub = (tid, title, hrs) =>
    fetch(API_SUBTAREAS, {
      method:'POST',
      headers:{'Content-Type':'application/json'},
      body:JSON.stringify({
        tareaId:tid,titulo:title,descripcion:'Subtask',
        estado:'pendiente',horasEstimadas:hrs,horasReales:0,
        fechaCreacion:new Date().toISOString(),deadline:null
      })
    }).then(() => reloadOne(tid)).catch(setError);

  const addItem = (tit,desc,uid,eq,pid,hrs) => {
    setInserting(true);
    fetch(API_TAREAS,{
      method:'POST',headers:{'Content-Type':'application/json'},
      body:JSON.stringify({
        titulo:tit,descripcion:desc,usuarioId:uid,
        equipoId:eq,proyectoId:pid,
        horasEstimadas:Math.min(hrs,4),
        estado:'pendiente',fechaCreacion:new Date().toISOString()
      })
    })
    .then(r=>r.ok?r.json():Promise.reject())
    .then(created=>{
      if(hrs>4) setPendingSplit({tareaId:created.tareaId,remainingHours:hrs-4});
      fetchTasks();
    })
    .catch(setError)
    .finally(()=>{setInserting(false);setNewDlg(false);});
  };

  /* ---------- PATCH SOLO USUARIO ---------- */
  const updateAssignee = (tid, uid) =>
    fetch(`${API_TAREAS}/${tid}/assignee`,{
      method :'PATCH',
      headers : { 'Content-Type':'application/json' },
      body    : JSON.stringify({ usuarioId: uid })
    })
    .then(() => reloadOne(tid))
    .catch(setError);

  const setDeadline = (tid,iso) =>
    fetch(`${API_TAREAS}/${tid}/deadline`,{
      method:'PUT',headers:{'Content-Type':'application/json'},
      body:JSON.stringify({deadline:iso})
    }).then(()=>reloadOne(tid)).catch(setError);

  const markDone = (tid,hrs) =>
    fetch(`${API_TAREAS}/${tid}/complete`,{
      method:'PUT',headers:{'Content-Type':'application/json'},
      body:JSON.stringify({estado:'completado',horasReales:hrs})
    }).then(()=>reloadOne(tid)).catch(setError);

  const deleteTask = (tid) =>
    fetch(`${API_TAREAS}/${tid}`,{method:'DELETE'}).then(fetchTasks).catch(setError);

  /* ---------- helpers ---------- */
  const renderAssignee = (t) => (
    <FormControl size="small" fullWidth>
      <InputLabel id={`ass-${t.tareaId}`}>Asignado</InputLabel>
      <Select
        labelId={`ass-${t.tareaId}`}
        value={t.usuarioId != null ? String(t.usuarioId) : ''}
        label="Asignado"
        onChange={e =>
          updateAssignee(t.tareaId, e.target.value === '' ? null : Number(e.target.value))
        }
      >
        <MenuItem value=""><em>No asignado</em></MenuItem>
        {users.map(u => (
          <MenuItem key={u.id} value={String(u.id)}>{u.nombre}</MenuItem>
        ))}
      </Select>
    </FormControl>
  );

  const toggleExpand = (id) => {
    if (expanded === id) { setExpanded(null); return; }
    fetchSubs(id).then(subs => {
      console.log('Task details:', tasks.find(t => t.tareaId === id));  // Debug log
      setTasks(p => p.map(t => t.tareaId === id ? { ...t, subTareas: subs } : t));
      setExpanded(id);
    }).catch(setError);
  };

  /* ---------- rendering data ---------- */
  const pending   = tasks.filter(t => t.estado !== 'completado');
  const completed = tasks.filter(t => t.estado === 'completado');

  if (loading) return <CircularProgress sx={{ m:4 }} />;
  if (error)   return <Typography color="error">{error.toString()}</Typography>;

  return (
    <div style={{ padding:16 }}>
      {/* header */}
      <Toolbar sx={{ justifyContent:'space-between' }}>
        <Typography variant="h5" fontWeight="bold">My Tasks</Typography>
        <div>
          <Button variant="outlined" startIcon={<FilterListIcon />}
                  onClick={e=>setAnchorEl(e.currentTarget)} sx={{ mr:2 }}>
            Filter
          </Button>
          <Menu anchorEl={anchorEl} open={Boolean(anchorEl)}
                onClose={()=>setAnchorEl(null)}>
            <MenuItem onClick={()=>setAnchorEl(null)}>No filters yet</MenuItem>
          </Menu>
          <Button startIcon={<AddIcon />} variant="contained"
                  onClick={()=>setNewDlg(true)}
                  sx={{ bgcolor:'#C74634', '&:hover':{bgcolor:'#b63f2e'} }}>
            Add Task
          </Button>
        </div>
      </Toolbar>

      {/* ---------------- Pending ---------------- */}
      {pending.length>0 && (
        <>
          <Typography variant="h6" sx={{ mt:3 }}>Pending</Typography>
          <TableContainer component={Paper} sx={{ mt:1 }}>
            <Table size="small">
              <TableHead sx={{ bgcolor:'#C74634' }}>
                <TableRow>
                  {['#','Title','Assignee','Status','Deadline','Actions']
                    .map(h=>(
                      <TableCell key={h} sx={{ color:'white', fontWeight:'bold' }}>
                        {h}
                      </TableCell>
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
                          : <Button size="small" onClick={() =>
                              setDeadlineDlg({ open:true, task:t, val:'' })}>Set</Button>}
                      </TableCell>
                      <TableCell>
                        <Button size="small" variant="contained"
                                onClick={()=>setCompleteDlg({ open:true, task:t, hours:'' })}>
                          Done
                        </Button>
                      </TableCell>
                    </TableRow>

                    {/* expanded row */}
                    {expanded===t.tareaId && (
                      <TableRow>
                        <TableCell colSpan={6} sx={{ bgcolor:'#f5f5f5', p: 3 }}>
                          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                            {/* Description Section */}
                            <Box>
                              <Typography variant="subtitle1" fontWeight="bold" color="primary">
                                Description
                              </Typography>
                              <Typography sx={{ whiteSpace:'pre-wrap', mt: 1 }}>
                                {t.descripcion || '—'}
                              </Typography>
                            </Box>

                            {/* Time Information Section */}
                            <Box sx={{ display: 'flex', gap: 4, mt: 2 }}>
                              <Box sx={{ minWidth: 200 }}>
                                <Typography variant="subtitle1" fontWeight="bold" color="primary">
                                  Time Information
                                </Typography>
                                <Box sx={{ mt: 1 }}>
                                  <Typography><strong>Estimated Hours:</strong> {t.horasEstimadas || '—'}</Typography>
                                  <Typography><strong>Real Hours:</strong> {t.horasReales || '—'}</Typography>
                                  <Typography><strong>Status:</strong> {t.estado || '—'}</Typography>
                                </Box>
                              </Box>

                              <Box sx={{ minWidth: 200 }}>
                                <Typography variant="subtitle1" fontWeight="bold" color="primary">
                                  Dates
                                </Typography>
                                <Box sx={{ mt: 1 }}>
                                  <Typography>
                                    <strong>Created:</strong>{' '}
                                    {t.fechaCreacion ? (
                                      <Moment format="DD/MM/YYYY HH:mm" utc>{t.fechaCreacion}</Moment>
                                    ) : '—'}
                                  </Typography>
                                  <Typography>
                                    <strong>Deadline:</strong>{' '}
                                    {t.deadline ? (
                                      <Moment format="DD/MM/YYYY HH:mm" utc>{t.deadline}</Moment>
                                    ) : '—'}
                                  </Typography>
                                </Box>
                              </Box>
                            </Box>

                            {/* Subtasks Section */}
                            <Box sx={{ mt: 2 }}>
                              <Typography variant="subtitle1" fontWeight="bold" color="primary">
                                Sub-tasks
                              </Typography>
                              {t.subTareas?.length ? (
                                <Box component="ul" sx={{ mt: 1, pl: 2 }}>
                                  {t.subTareas.map(s => (
                                    <li key={s.subTareaId}>
                                      <Typography>
                                        {s.titulo} — {s.horasEstimadas}h ({s.estado})
                                      </Typography>
                                    </li>
                                  ))}
                                </Box>
                              ) : (
                                <Typography sx={{ mt: 1 }}>No subtasks</Typography>
                              )}

                              {/* Add subtask form */}
                              <Box component="form"
                                   sx={{ display: 'flex', gap: 1, mt: 2, maxWidth: 400 }}
                                   onSubmit={e => {
                                     e.preventDefault();
                                     addSub(t.tareaId, newSubTitle, newSubHours);
                                     setNewSubTitle('');
                                     setNewSubHours('');
                                   }}>
                                <TextField size="small" label="Title" value={newSubTitle}
                                         onChange={e => setNewSubTitle(e.target.value)} />
                                <TextField size="small" label="Hours" type="number"
                                         value={newSubHours}
                                         onChange={e => setNewSubHours(e.target.value)} />
                                <Button type="submit" variant="contained">Add</Button>
                              </Box>
                            </Box>
                          </Box>
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

      {/* ---------------- Completed ---------------- */}
      {completed.length>0 && (
        <>
          <Typography variant="h6" sx={{ mt:4 }}>Completed</Typography>
          <TableContainer component={Paper} sx={{ mt:1 }}>
            <Table size="small">
              <TableHead sx={{ bgcolor:'#C74634' }}>
                <TableRow>
                  {['#','Title','Assignee','Status','Deadline','Actions']
                    .map(h=>(
                      <TableCell key={h} sx={{ color:'white', fontWeight:'bold' }}>
                        {h}
                      </TableCell>
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
                      <Button startIcon={<DeleteIcon />} color="error"
                              onClick={()=>deleteTask(t.tareaId)}>Delete</Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      )}

      {/* ------------- New Task dialog ------------- */}
      <Dialog open={newDlg} onClose={()=>setNewDlg(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Nueva Tarea</DialogTitle>
        <DialogContent>
          <NewItem addItem={addItem} isInserting={inserting} users={users}/>
        </DialogContent>
      </Dialog>

      {/* ------------- Deadline dialog ------------- */}
      <Dialog open={deadlineDlg.open}
              onClose={()=>setDeadlineDlg({ open:false, task:null, val:'' })}>
        <DialogTitle>Set Deadline</DialogTitle>
        <DialogContent>
          <TextField type="datetime-local" fullWidth
                     value={deadlineDlg.val}
                     onChange={e=>setDeadlineDlg({...deadlineDlg,val:e.target.value})}/>
        </DialogContent>
        <DialogActions>
          <Button onClick={()=>setDeadlineDlg({open:false,task:null,val:''})}>Cancel</Button>
          <Button onClick={()=>{
            const iso=new Date(deadlineDlg.val).toISOString();
            setDeadline(deadlineDlg.task.tareaId, iso);
            setDeadlineDlg({open:false,task:null,val:''});
          }}>Save</Button>
        </DialogActions>
      </Dialog>

      {/* ------------- Complete dialog ------------- */}
      <Dialog open={completeDlg.open}
              onClose={()=>setCompleteDlg({open:false,task:null,hours:''})}>
        <DialogTitle>Complete Task</DialogTitle>
        <DialogContent>
          <TextField label="Real hours" type="number" fullWidth
                     value={completeDlg.hours}
                     onChange={e=>setCompleteDlg({...completeDlg,hours:e.target.value})}/>
        </DialogContent>
        <DialogActions>
          <Button onClick={()=>setCompleteDlg({open:false,task:null,hours:''})}>Cancel</Button>
          <Button onClick={()=>{
            markDone(completeDlg.task.tareaId, completeDlg.hours);
            setCompleteDlg({open:false,task:null,hours:''});
          }}>Confirm</Button>
        </DialogActions>
      </Dialog>

      {/* ------------- Split-into-subtasks prompt ------------- */}
      {pendingSplit && (
        <Dialog open onClose={()=>setPendingSplit(null)}>
          <DialogTitle>Divide task into subtasks</DialogTitle>
          <DialogContent>
            <Typography>
              You still have {pendingSplit.remainingHours} h to assign to subtasks.
              (Implementación futura)
            </Typography>
          </DialogContent>
          <DialogActions>
            <Button onClick={()=>setPendingSplit(null)}>OK</Button>
          </DialogActions>
        </Dialog>
      )}
    </div>
  );
}
