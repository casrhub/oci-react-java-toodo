import React, { useState, useEffect } from 'react';
import {
  Table, TableHead, TableBody, TableRow, TableCell,
  TableContainer, Paper, Button, Menu, MenuItem, Toolbar, Typography, Dialog,
  DialogTitle, DialogContent, DialogActions, TextField, CircularProgress
} from '@mui/material';
import FilterListIcon from '@mui/icons-material/FilterList';
import AddIcon from '@mui/icons-material/Add';
import DeleteIcon from '@mui/icons-material/Delete';
import Moment from 'react-moment';

import { API_TAREAS, API_SUBTAREAS } from '../api'; 
import NewItem from '../components/tasks/NewItem';  


function DevTasksPage() {
  // ───────── STATES ─────────────────────────────
  const [tasks, setTasks] = useState([]);
  const [isLoading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  // Adding a task
  const [isInserting, setInserting] = useState(false);
  const [newTaskDialogOpen, setNewTaskDialogOpen] = useState(false);
  const [pendingSubtaskPrompt, setPendingSubtaskPrompt] = useState(null);

  // Subtasks expansion
  const [expandedTaskId, setExpandedTaskId] = useState(null);

  // Deadline dialog
  const [selectedTask, setSelectedTask] = useState(null);
  const [deadline, setDeadline] = useState('');

  // Complete-task dialog
  const [completeDialogOpen, setCompleteDialogOpen] = useState(false);
  const [selectedCompleteTask, setSelectedCompleteTask] = useState(null);
  const [realHours, setRealHours] = useState('');

  // Filter Menu
  const [anchorEl, setAnchorEl] = useState(null);
  const filterMenuOpen = Boolean(anchorEl);

  // Subtask creation
  const [newSubTitulo, setNewSubTitulo] = useState('');
  const [newSubHoras, setNewSubHoras] = useState('');

  // ───────── EFFECTS ─────────────────────────────
  useEffect(() => {
    reloadAllTasks();
  }, []);

  // ───────── API / BACKEND LOGIC ─────────────────

  /** Fetch main tasks */
  function reloadAllTasks() {
    setLoading(true);
    fetch(API_TAREAS)
      .then((response) => (response.ok ? response.json() : Promise.reject('Error fetching tasks')))
      .then((data) => {
        setTasks(data);
        setLoading(false);
      })
      .catch((err) => {
        setError(err);
        setLoading(false);
      });
  }

  /** Fetch subtasks for a specific task */
  function fetchSubTareas(tareaId) {
    return fetch(`${API_SUBTAREAS}?tareaId=${tareaId}`)
      .then((res) => (res.ok ? res.json() : Promise.reject('Error fetching subtareas')));
  }

  /** Create a new subtask */
  function createSubTarea(tareaId, titulo, horasEstimadas) {
    fetch(API_SUBTAREAS, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        tareaId,
        titulo,
        descripcion: 'Subtask manual',
        estado: 'pendiente',
        horasEstimadas,
        horasReales: 0,
        fechaCreacion: new Date().toISOString(),
        deadline: null
      })
    })
      .then((res) => {
        if (res.ok) {
          reloadOneTask(tareaId);
        } else {
          console.error('Failed to add subtask');
        }
      })
      .catch((err) => console.error(err));
  }

  /** Create a new main task */
  function addItem(titulo, descripcion, usuarioId, equipoId, proyectoId, horasEstimadas) {
    setInserting(true);

    const baseTask = {
      titulo,
      descripcion,
      usuarioId,
      equipoId,
      proyectoId,
      horasEstimadas: Math.min(horasEstimadas, 4),
      estado: 'pendiente',
      fechaCreacion: new Date().toISOString()
    };

    fetch(API_TAREAS, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(baseTask)
    })
      .then((response) => (response.ok ? response.json() : Promise.reject('Failed to create task')))
      .then((createdTask) => {
        // If user asked for more than 4 hours, show the subtask-splitting prompt
        if (horasEstimadas > 4) {
          const remaining = horasEstimadas - 4;
          setPendingSubtaskPrompt({ tareaId: createdTask.tareaId, remainingHours: remaining });
        }
        return createdTask.tareaId;
      })
      .then(() => {
        // Wait briefly so the prompt can open before reloading tasks
        setTimeout(reloadAllTasks, 300);
      })
      .catch((err) => {
        setError(err);
      })
      .finally(() => {
        setInserting(false);
        setNewTaskDialogOpen(false); // Close the "Add Task" dialog
      });
  }

  /** Reload a single task after changes */
  function reloadOneTask(tareaId) {
    fetch(`${API_TAREAS}/${tareaId}`)
      .then((response) => (response.ok ? response.json() : Promise.reject('Error reloading task')))
      .then((result) => {
        setTasks((prev) =>
          prev.map((t) => (t.tareaId === tareaId ? { ...t, ...result } : t))
        );
      })
      .catch((err) => setError(err));
  }

  /** Delete a task */
  function deleteItem(tareaId) {
    fetch(`${API_TAREAS}/${tareaId}`, { method: 'DELETE' })
      .then((res) => {
        if (res.ok) {
          setTasks((prev) => prev.filter((t) => t.tareaId !== tareaId));
        } else {
          throw new Error('Error deleting task');
        }
      })
      .catch((err) => setError(err));
  }

  /** Mark task complete with real hours */
  function markTaskComplete(tareaId, horasReales) {
    fetch(`${API_TAREAS}/${tareaId}/complete`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ estado: 'completado', horasReales })
    })
      .then((res) => {
        if (!res.ok) throw new Error('Failed to mark task complete');
        reloadOneTask(tareaId);
      })
      .catch((err) => setError(err));
  }

  /** Update a task's deadline */
  function updateDeadline(tareaId, newDeadline) {
    fetch(`${API_TAREAS}/${tareaId}/deadline`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ deadline: newDeadline })
    })
      .then((res) => {
        if (!res.ok) throw new Error('Failed to update deadline');
        // Reload this single task
        reloadOneTask(tareaId);
      })
      .catch((err) => console.error(err));
  }

  // ───────── HANDLERS (Dialogs, Subtasks, Etc.) ─────────────────

  /** Toggle row expand/collapse to show subtasks */
  function toggleSubtaskRow(tareaId) {
    if (expandedTaskId === tareaId) {
      // Close if already expanded
      setExpandedTaskId(null);
    } else {
      // Fetch subtasks, then expand
      fetchSubTareas(tareaId)
        .then((subs) => {
          setTasks((prev) =>
            prev.map((t) =>
              t.tareaId === tareaId ? { ...t, subTareas: subs } : t
            )
          );
          setExpandedTaskId(tareaId);
        })
        .catch((err) => console.error(err));
    }
  }

  /** Open "Set Deadline" dialog */
  function openDeadlineDialog(task) {
    setSelectedTask(task);
    setDeadline(task.deadline || '');
  }

  /** Close "Set Deadline" dialog */
  function closeDeadlineDialog() {
    setSelectedTask(null);
    setDeadline('');
  }

  /** Save deadline (user picks date/time) */
  function saveDeadline() {
    if (!selectedTask) return;
    const localDate = new Date(deadline);
    const fullISODate = new Date(localDate.getTime() - localDate.getTimezoneOffset() * 60000).toISOString();

    updateDeadline(selectedTask.tareaId, fullISODate);
    closeDeadlineDialog();
  }

  /** Open "Mark Complete" dialog */
  function openCompleteDialog(task) {
    setSelectedCompleteTask(task);
    setCompleteDialogOpen(true);
  }

  /** Close "Mark Complete" dialog */
  function closeCompleteDialog() {
    setCompleteDialogOpen(false);
    setRealHours('');
    setSelectedCompleteTask(null);
  }

  /** Confirm "Done" with real hours */
  function confirmCompleteTask() {
    if (selectedCompleteTask && realHours) {
      markTaskComplete(selectedCompleteTask.tareaId, realHours);
      closeCompleteDialog();
    }
  }

  /** Add subtask to expanded parent */
  function handleAddSubtask(parentTaskId, e) {
    e.preventDefault();
    createSubTarea(parentTaskId, newSubTitulo, newSubHoras);
    setNewSubTitulo('');
    setNewSubHoras('');
  }

  // ───────── RENDER ─────────────────────────────

  return (
    <div style={{ padding: '1rem' }}>
      {/* Header Toolbar */}
      <Toolbar disableGutters sx={{ justifyContent: 'space-between' }}>
        <Typography variant="h5" component="div" sx={{ fontWeight: 'bold' }}>
          My Tasks
        </Typography>

        <div>
          {/* Filter Button + Menu */}
          <Button
            variant="outlined"
            startIcon={<FilterListIcon />}
            onClick={(event) => setAnchorEl(event.currentTarget)}
            sx={{ marginRight: '1rem' }}
          >
            Filter
          </Button>
          <Menu
            anchorEl={anchorEl}
            open={filterMenuOpen}
            onClose={() => setAnchorEl(null)}
            PaperProps={{ style: { maxHeight: 200 } }}
          >
            {/* Future filter options */}
            <MenuItem onClick={() => setAnchorEl(null)}>No filters yet</MenuItem>
          </Menu>

          {/* Add Task Button (opens modal with NewItem) */}
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={() => setNewTaskDialogOpen(true)}
            sx={{
              backgroundColor: '#C74634',
              '&:hover': {
                backgroundColor: '#b63f2e'
              }
            }}
          >
            Add Task
          </Button>
        </div>
      </Toolbar>

      {/* Error / Loading */}
      {error && <p style={{ color: 'red' }}>Error: {error.toString()}</p>}
      {isLoading && <CircularProgress style={{ marginTop: '1rem' }} />}

      {/* Pending subtask prompt (if user enters >4 hours) */}
      {pendingSubtaskPrompt && (
        <Dialog open={true} onClose={() => setPendingSubtaskPrompt(null)}>
          <DialogTitle>Break task into subtasks</DialogTitle>
          <DialogContent>
            <p>
              You have {pendingSubtaskPrompt.remainingHours} hours left to split
              into subtasks. (Implementation placeholder)
            </p>
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setPendingSubtaskPrompt(null)}>Done</Button>
          </DialogActions>
        </Dialog>
      )}

      {/* TABLE */}
      {!isLoading && tasks.length > 0 && (
        <TableContainer component={Paper} sx={{ marginTop: '1rem' }}>
          <Table>
            <TableHead sx={{ backgroundColor: '#C74634' }}>
              <TableRow>
                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>#</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Title</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Status</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Deadline</TableCell>
                <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Actions</TableCell>
              </TableRow>
            </TableHead>
            <TableBody>
              {tasks.map((t, index) => {
                // Convert backend "estado" => front-end "status"
                let statusLabel = 'To Do';
                if (t.estado === 'completado') statusLabel = 'Done';
                else if (t.estado === 'pendiente') statusLabel = 'To Do';
                else if (t.estado === 'en progreso') statusLabel = 'In Progress';

                const isDone = t.estado === 'completado';

                return (
                  <React.Fragment key={t.tareaId}>
                    {/* Main Row */}
                    <TableRow>
                      <TableCell>{index + 1}</TableCell>

                      {/* Title => Click to expand subtasks */}
                      <TableCell>
                        <Button onClick={() => toggleSubtaskRow(t.tareaId)}>
                          {t.titulo}
                        </Button>
                      </TableCell>

                      <TableCell>{statusLabel}</TableCell>

                      <TableCell>
                        {t.deadline ? (
                          <Moment format="MMM Do YYYY, hh:mm A" utc>
                            {t.deadline}
                          </Moment>
                        ) : (
                          <Button size="small" onClick={() => openDeadlineDialog(t)}>
                            Set Deadline
                          </Button>
                        )}
                      </TableCell>

                      <TableCell>
                        {!isDone && (
                          <Button
                            variant="contained"
                            sx={{ marginRight: '0.5rem' }}
                            onClick={() => openCompleteDialog(t)}
                          >
                            Done
                          </Button>
                        )}
                        {isDone && (
                          <Button
                            variant="contained"
                            startIcon={<DeleteIcon />}
                            color="error"
                            onClick={() => deleteItem(t.tareaId)}
                          >
                            Delete
                          </Button>
                        )}
                      </TableCell>
                    </TableRow>

                    {/* Expanded Row for Subtasks */}
                    {expandedTaskId === t.tareaId && (
                      <TableRow>
                        <TableCell colSpan={5} sx={{ backgroundColor: '#f9f9f9' }}>
                          {/* Subtask List */}
                          {t.subTareas && t.subTareas.length > 0 ? (
                            <ul>
                              {t.subTareas.map((sub) => (
                                <li key={sub.subTareaId}>
                                  {sub.titulo} — {sub.estado} — {sub.horasEstimadas}h
                                </li>
                              ))}
                            </ul>
                          ) : (
                            <p>No subtasks yet</p>
                          )}

                          {/* Subtask creation form */}
                          <form
                            onSubmit={(e) => handleAddSubtask(t.tareaId, e)}
                            style={{ display: 'flex', gap: '0.5rem', marginTop: '1rem' }}
                          >
                            <TextField
                              label="Subtask title"
                              size="small"
                              value={newSubTitulo}
                              onChange={(e) => setNewSubTitulo(e.target.value)}
                            />
                            <TextField
                              label="Hours"
                              size="small"
                              type="number"
                              value={newSubHoras}
                              onChange={(e) => setNewSubHoras(e.target.value)}
                            />
                            <Button variant="contained" type="submit">
                              Add Subtask
                            </Button>
                          </form>
                        </TableCell>
                      </TableRow>
                    )}
                  </React.Fragment>
                );
              })}
            </TableBody>
          </Table>
        </TableContainer>
      )}

      {/* ───────── DIALOGS ───────────────── */}

      {/* Add Task Dialog + “NewItem” component inside */}
      <Dialog open={newTaskDialogOpen} onClose={() => setNewTaskDialogOpen(false)}>
        <DialogTitle>Add New Task</DialogTitle>
        <DialogContent>
          <NewItem addItem={addItem} isInserting={isInserting} />
        </DialogContent>
      </Dialog>

      {/* Set Deadline Dialog */}
      <Dialog open={!!selectedTask} onClose={closeDeadlineDialog}>
        <DialogTitle>Set Deadline</DialogTitle>
        <DialogContent>
          <TextField
            type="datetime-local"
            value={deadline}
            onChange={(e) => setDeadline(e.target.value)}
            fullWidth
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={closeDeadlineDialog} color="secondary">
            Cancel
          </Button>
          <Button onClick={saveDeadline} color="primary">
            Save
          </Button>
        </DialogActions>
      </Dialog>

      {/* Complete Task Dialog */}
      <Dialog open={completeDialogOpen} onClose={closeCompleteDialog}>
        <DialogTitle>Complete Task</DialogTitle>
        <DialogContent>
          <TextField
            autoFocus
            label="Real Hours Worked"
            type="number"
            fullWidth
            value={realHours}
            onChange={(e) => setRealHours(e.target.value)}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={closeCompleteDialog} color="secondary">
            Cancel
          </Button>
          <Button onClick={confirmCompleteTask} color="primary">
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default DevTasksPage;
