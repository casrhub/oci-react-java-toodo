// File: MtdrSpring/backend/src/main/frontend/src/pages/DevTasksPage.jsx
import React, { useState, useEffect, useRef } from 'react';
import {
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  TableContainer,
  Paper,
  Button,
  Toolbar,
  Typography,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  TextField,
  CircularProgress,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Box,
} from '@mui/material';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import Moment from 'react-moment';
import { useAuth } from '../context/AuthContext';
import { useAuthFetch } from '../utils/authFetch';
import { API_TAREAS, API_SUBTAREAS, API_USUARIOS } from '../api';
import NewItem from '../components/tasks/NewItem';
import AppNavbar from '../components/AppNavbar';

export default function DevTasksPage() {
  const { role, developerId } = useAuth();
  const [tasks, setTasks] = useState([]);
  const [users, setUsers] = useState([]);
  const [error, setError] = useState(null);
  const [newDlg, setNewDlg] = useState(false);
  const [inserting, setInserting] = useState(false);
  const [deadlineDlg, setDeadlineDlg] = useState({ open: false, task: null, val: '' });
  const [completeDlg, setCompleteDlg] = useState({ open: false, task: null, hours: '' });
  const [pendingSplit, setPendingSplit] = useState(null);
  const [expanded, setExpanded] = useState(null);
  const [newSubTitle, setNewSubTitle] = useState('');
  const [newSubHours, setNewSubHours] = useState('');
  const [pageLoading, setPageLoading] = useState(true);
  const failSafe = useRef(null);
  const authFetch = useAuthFetch();

  const startLoading = () => {
    setPageLoading(true);
    if (failSafe.current) clearTimeout(failSafe.current);
    failSafe.current = setTimeout(() => setPageLoading(false), 30000);
  };

  const fetchTasks = () =>
    authFetch(API_TAREAS)
      .then((r) => (r.ok ? r.json() : Promise.reject('Error loading tasks')))
      .then(setTasks)
      .catch(setError);

  const fetchUsers = () =>
    authFetch(API_USUARIOS)
      .then((r) => (r.ok ? r.json() : Promise.reject('Error loading users')))
      .then((arr) =>
        setUsers(
          arr.map((u) => ({
            id: u.usuario_id ?? u.usuarioId ?? u.id,
            nombre: u.nombre,
          }))
        )
      )
      .catch(setError);

  useEffect(() => {
    startLoading();
    Promise.all([fetchTasks(), fetchUsers()]).finally(() => setPageLoading(false));
  }, []);

  const reloadOne = (id) =>
    authFetch(`${API_TAREAS}/${id}`)
      .then((r) => (r.ok ? r.json() : Promise.reject()))
      .then((t) => setTasks((p) => p.map((x) => (x.tareaId === id ? { ...x, ...t } : x))))
      .catch(setError);

  const fetchSubs = (tid) =>
    authFetch(`${API_SUBTAREAS}?tareaId=${tid}`).then((r) => (r.ok ? r.json() : Promise.reject()));

  const addSub = (tid, title, hrs) =>
    authFetch(API_SUBTAREAS, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        tareaId: tid,
        titulo: title,
        descripcion: 'Subtask',
        estado: 'pendiente',
        horasEstimadas: hrs,
        horasReales: 0,
        fechaCreacion: new Date().toISOString(),
        deadline: null,
      }),
    })
      .then(() => reloadOne(tid))
      .catch(setError);

  const addItem = (tit, desc, uid, eq, pid, hrs) => {
    setInserting(true);
    authFetch(API_TAREAS, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        titulo: tit,
        descripcion: desc,
        usuarioId: uid,
        equipoId: eq ? Number(eq) : null,
        proyectoId: pid ? Number(pid) : null,
        horasEstimadas: Math.min(hrs, 4),
        estado: 'pendiente',
        fechaCreacion: new Date().toISOString(),
      }),
    })
      .then((r) => (r.ok ? r.json() : Promise.reject()))
      .then((created) => {
        if (hrs > 4) setPendingSplit({ tareaId: created.tareaId, remainingHours: hrs - 4 });
        fetchTasks();
      })
      .catch(setError)
      .finally(() => {
        setInserting(false);
        setNewDlg(false);
      });
  };

  const updateAssignee = (tid, uid) =>
    authFetch(`${API_TAREAS}/${tid}/assignee`, {
      method: 'PATCH',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ usuarioId: uid }),
    })
      .then(() => reloadOne(tid))
      .catch(setError);

  const setDeadline = (tid, iso) =>
    authFetch(`${API_TAREAS}/${tid}/deadline`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ deadline: iso }),
    })
      .then(() => reloadOne(tid))
      .catch(setError);

  const markDone = (tid, hrs) =>
    authFetch(`${API_TAREAS}/${tid}/complete`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ estado: 'completado', horasReales: hrs }),
    })
      .then(() => reloadOne(tid))
      .catch(setError);

  const deleteTask = (tid) =>
    authFetch(`${API_TAREAS}/${tid}`, { method: 'DELETE' }).then(fetchTasks).catch(setError);

  const renderAssignee = (t) => (
    <FormControl size="small" fullWidth>
      <InputLabel id={`ass-${t.tareaId}`}>Asignado</InputLabel>
      <Select
        labelId={`ass-${t.tareaId}`}
        value={t.usuarioId != null ? String(t.usuarioId) : ''}
        label="Asignado"
        onChange={(e) =>
          updateAssignee(t.tareaId, e.target.value === '' ? null : Number(e.target.value))
        }
      >
        <MenuItem value="">
          <em>No asignado</em>
        </MenuItem>
        {users.map((u) => (
          <MenuItem key={u.id} value={String(u.id)}>
            {u.nombre}
          </MenuItem>
        ))}
      </Select>
    </FormControl>
  );

  const toggleExpand = (id) => {
    if (expanded === id) {
      setExpanded(null);
      return;
    }
    fetchSubs(id)
      .then((subs) => {
        setTasks((p) => p.map((t) => (t.tareaId === id ? { ...t, subTareas: subs } : t)));
        setExpanded(id);
      })
      .catch(setError);
  };

  const pending = tasks.filter(
    (t) => t.estado !== 'completado' && (role !== 'developer' || t.usuarioId === developerId)
  );
  const completed = tasks.filter(
    (t) => t.estado === 'completado' && (role !== 'developer' || t.usuarioId === developerId)
  );

  return (
    <>
      {pageLoading && (
        <Box
          sx={{
            position: 'fixed',
            inset: 0,
            bgcolor: 'white',
            zIndex: 9999,
            display: 'flex',
            flexDirection: 'column',
            alignItems: 'center',
            justifyContent: 'center',
          }}
        >
          <CircularProgress size={80} />
          <Typography variant="h6" sx={{ mt: 2 }}>
            Cargando tareas, por favor espera…
          </Typography>
        </Box>
      )}
      <AppNavbar />
      <div style={{ padding: 16, visibility: pageLoading ? 'hidden' : 'visible' }}>
        <Toolbar
          sx={{
            justifyContent: role !== 'developer' ? 'space-between' : 'flex-start',
            mb: 2,
            px: 0,
          }}
        >
          <Typography variant="h5" fontWeight="bold">
            Mis tareas
          </Typography>
          {role !== 'developer' && (
            <Button
              startIcon={<AddIcon />}
              variant="contained"
              onClick={() => setNewDlg(true)}
              sx={{ bgcolor: '#C74634', '&:hover': { bgcolor: '#b63f2e' } }}
            >
              Agregar tarea
            </Button>
          )}
        </Toolbar>
        {error && (
          <Typography color="error" sx={{ mt: 2 }}>
            {String(error)}
          </Typography>
        )}
        {pending.length > 0 && (
          <>
            <Typography variant="h6" sx={{ mt: 3, fontWeight: 'bold' }}>
              Pendientes
            </Typography>
            <TableContainer component={Paper} sx={{ mt: 1 }}>
              <Table size="small">
                <TableHead sx={{ bgcolor: '#C74634' }}>
                  <TableRow>
                    {['#', 'Título', 'Asignado', 'Estado', 'Fecha límite', 'Acciones'].map((h) => (
                      <TableCell key={h} sx={{ color: 'white', fontWeight: 'bold' }}>
                        {h}
                      </TableCell>
                    ))}
                  </TableRow>
                </TableHead>
                <TableBody>
                  {pending.map((t, i) => (
                    <React.Fragment key={t.tareaId}>
                      <TableRow>
                        <TableCell>{i + 1}</TableCell>
                        <TableCell>
                          <Button onClick={() => toggleExpand(t.tareaId)}>{t.titulo}</Button>
                        </TableCell>
                        <TableCell>{renderAssignee(t)}</TableCell>
                        <TableCell>
                          {t.estado === 'en progreso' ? 'En progreso' : 'Por hacer'}
                        </TableCell>
                        <TableCell>
                          {t.deadline ? (
                            <Moment format="DD/MM/YYYY HH:mm" utc>
                              {t.deadline}
                            </Moment>
                          ) : (
                            role !== 'developer' && (
                              <Button
                                size="small"
                                onClick={() => setDeadlineDlg({ open: true, task: t, val: '' })}
                              >
                                Establecer
                              </Button>
                            )
                          )}
                        </TableCell>
                        <TableCell>
                          <Button
                            size="small"
                            variant="contained"
                            onClick={() => setCompleteDlg({ open: true, task: t, hours: '' })}
                          >
                            Hecho
                          </Button>
                        </TableCell>
                      </TableRow>
                      {expanded === t.tareaId && (
                        <TableRow>
                          <TableCell colSpan={6} sx={{ bgcolor: '#fafafa' }}>
                            <Typography variant="subtitle2">Descripción</Typography>
                            <Typography sx={{ whiteSpace: 'pre-wrap' }}>
                              {t.descripcion || '—'}
                            </Typography>
                            <Typography mt={2} variant="subtitle2">
                              Subtareas
                            </Typography>
                            {t.subTareas?.length ? (
                              <ul>
                                {t.subTareas.map((s) => (
                                  <li key={s.subTareaId}>
                                    {s.titulo} — {s.horasEstimadas}h ({s.estado})
                                  </li>
                                ))}
                              </ul>
                            ) : (
                              <Typography>Sin subtareas</Typography>
                            )}
                            <Box
                              component="form"
                              sx={{ display: 'flex', gap: 1, mt: 1, maxWidth: 400 }}
                              onSubmit={(e) => {
                                e.preventDefault();
                                addSub(t.tareaId, newSubTitle, newSubHours);
                                setNewSubTitle('');
                                setNewSubHours('');
                              }}
                            >
                              <TextField
                                size="small"
                                label="Título"
                                value={newSubTitle}
                                onChange={(e) => setNewSubTitle(e.target.value)}
                              />
                              <TextField
                                size="small"
                                label="Horas"
                                type="number"
                                value={newSubHours}
                                onChange={(e) => setNewSubHours(e.target.value)}
                              />
                              <Button type="submit" variant="contained">
                                Agregar
                              </Button>
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
        {completed.length > 0 && (
          <>
            <Typography variant="h6" sx={{ mt: 4, fontWeight: 'bold' }}>
              Completadas
            </Typography>
            <TableContainer component={Paper} sx={{ mt: 1 }}>
              <Table size="small">
                <TableHead sx={{ bgcolor: '#C74634' }}>
                  <TableRow>
                    {['#', 'Título', 'Asignado', 'Estado', 'Fecha límite'].map((h) => (
                      <TableCell key={h} sx={{ color: 'white', fontWeight: 'bold' }}>
                        {h}
                      </TableCell>
                    ))}
                    {role !== 'developer' && (
                      <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Acciones</TableCell>
                    )}
                  </TableRow>
                </TableHead>
                <TableBody>
                  {completed.map((t, i) => (
                    <TableRow key={t.tareaId}>
                      <TableCell>{i + 1}</TableCell>
                      <TableCell>{t.titulo}</TableCell>
                      <TableCell>{renderAssignee(t)}</TableCell>
                      <TableCell>Completado</TableCell>
                      <TableCell>
                        <Moment format="DD/MM/YYYY HH:mm" utc>
                          {t.deadline}
                        </Moment>
                      </TableCell>
                      {role !== 'developer' && (
                        <TableCell>
                          <Button
                            startIcon={<DeleteIcon />}
                            color="error"
                            onClick={() => deleteTask(t.tareaId)}
                          >
                            Eliminar
                          </Button>
                        </TableCell>
                      )}
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </TableContainer>
          </>
        )}
        <Dialog open={newDlg} onClose={() => setNewDlg(false)} maxWidth="sm" fullWidth>
          <DialogTitle>Nueva Tarea</DialogTitle>
          <DialogContent>
            <NewItem addItem={addItem} isInserting={inserting} users={users} />
          </DialogContent>
        </Dialog>
        <Dialog
          open={deadlineDlg.open}
          onClose={() => setDeadlineDlg({ open: false, task: null, val: '' })}
        >
          <DialogTitle>Establecer fecha límite</DialogTitle>
          <DialogContent>
            <TextField
              type="datetime-local"
              fullWidth
              value={deadlineDlg.val}
              onChange={(e) => setDeadlineDlg({ ...deadlineDlg, val: e.target.value })}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setDeadlineDlg({ open: false, task: null, val: '' })}>
              Cancelar
            </Button>
            <Button
              onClick={() => {
                const iso = new Date(deadlineDlg.val).toISOString();
                setDeadline(deadlineDlg.task.tareaId, iso);
                setDeadlineDlg({ open: false, task: null, val: '' });
              }}
            >
              Guardar
            </Button>
          </DialogActions>
        </Dialog>
        <Dialog
          open={completeDlg.open}
          onClose={() => setCompleteDlg({ open: false, task: null, hours: '' })}
        >
          <DialogTitle>Completar tarea</DialogTitle>
          <DialogContent>
            <TextField
              label="Horas reales"
              type="number"
              fullWidth
              value={completeDlg.hours}
              onChange={(e) => setCompleteDlg({ ...completeDlg, hours: e.target.value })}
            />
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setCompleteDlg({ open: false, task: null, hours: '' })}>
              Cancelar
            </Button>
            <Button
              onClick={() => {
                markDone(completeDlg.task.tareaId, completeDlg.hours);
                setCompleteDlg({ open: false, task: null, hours: '' });
              }}
            >
              Confirmar
            </Button>
          </DialogActions>
        </Dialog>
        {pendingSplit && (
          <Dialog open onClose={() => setPendingSplit(null)}>
            <DialogTitle>Dividir tarea en subtareas</DialogTitle>
            <DialogContent>
              <Typography>
                Aún tienes {pendingSplit.remainingHours} h para asignar a subtareas.
              </Typography>
            </DialogContent>
            <DialogActions>
              <Button onClick={() => setPendingSplit(null)}>Aceptar</Button>
            </DialogActions>
          </Dialog>
        )}
      </div>
    </>
  );
}
