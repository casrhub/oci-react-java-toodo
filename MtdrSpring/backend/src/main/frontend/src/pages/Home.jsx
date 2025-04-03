import React, { useState, useEffect } from 'react';
import { API_TAREAS, API_SUBTAREAS } from '../api/index';  // adjusted import paths
import DeleteIcon from '@mui/icons-material/Delete';
import { Button, TableBody, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@mui/material';
import Moment from 'react-moment';

import NewItem from '../components/tasks/NewItem';  // adjusted import paths


function App() {
  const [isLoading, setLoading] = useState(false);
  const [isInserting, setInserting] = useState(false);
  const [items, setItems] = useState([]);
  const [error, setError] = useState();
  const [selectedTask, setSelectedTask] = useState(null);
  const [deadline, setDeadline] = useState("");
  const [completeDialogOpen, setCompleteDialogOpen] = useState(false);
  const [selectedCompleteTask, setSelectedCompleteTask] = useState(null);
  const [realHours, setRealHours] = useState("");
  const [expandedTaskId, setExpandedTaskId] = useState(null);
  const [newSubTitulo, setNewSubTitulo] = useState('');
  const [newSubHoras, setNewSubHoras] = useState('');
  const [pendingSubtaskPrompt, setPendingSubtaskPrompt] = useState(null);

  function fetchSubTareas(tareaId) {
    return fetch(`${API_SUBTAREAS}?tareaId=${tareaId}`)
      .then(response => response.ok ? response.json() : Promise.reject('Failed to fetch subtareas'));
  }
  
  function createSubTarea(tareaId, titulo, horasEstimadas) {
    fetch(API_SUBTAREAS, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        tareaId, // 👈 send it flat, not nested!
        titulo,
        descripcion: 'Subtask manual',
        estado: 'pendiente',
        horasEstimadas,
        horasReales: 0,
        fechaCreacion: new Date().toISOString(),
        deadline: null
      })
    })
      .then(res => res.ok ? reloadOneItem(tareaId) : console.error("Failed to add subtask"))
      .catch(err => console.error(err));
  }
  
  


  function deleteItem(deleteId) {
    fetch(API_TAREAS + '/' + deleteId, { method: 'DELETE' })
      .then(response => {
        if (response.ok) {
          setItems(items.filter(item => item.tareaId !== deleteId));
        } else {
          throw new Error('Something went wrong ...');
        }
      })
      .catch(err => setError(err));
  }

  function markTaskComplete(id, horasReales) {
    fetch(`${API_TAREAS}/${id}/complete`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ estado: "completado", horasReales })
    })
      .then(response => {
        if (response.ok) {
          reloadOneItem(id);
        } else {
          throw new Error('Failed to mark task complete');
        }
      })
      .catch(error => setError(error));
  }

  function reloadOneItem(id) {
    fetch(API_TAREAS + '/' + id)
      .then(response => response.ok ? response.json() : Promise.reject(new Error('Something went wrong ...')))
      .then(result => {
        setItems(items.map(x => (x.tareaId === id ? { ...x, ...result } : x)));
      })
      .catch(error => setError(error));
  }

  useEffect(() => {
    reloadAllItems();
  }, []);

  function reloadAllItems() {
    setLoading(true);
    fetch(API_TAREAS)
      .then(response => response.ok ? response.json() : Promise.reject(new Error('Something went wrong ...')))
      .then(result => {
        setLoading(false);
        setItems(result);
      })
      .catch(error => {
        setLoading(false);
        setError(error);
      });
  }

  function addItem(titulo, descripcion, usuarioId, equipoId, proyectoId, horasEstimadas) {
    setInserting(true);
  
    const baseTask = {
      titulo,
      descripcion,
      usuarioId,
      equipoId,
      proyectoId,
      horasEstimadas: Math.min(horasEstimadas, 4),
      estado: "pendiente",
      fechaCreacion: new Date().toISOString()
    };
  
    fetch(API_TAREAS, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify(baseTask)
    })
      .then(response => response.ok ? response.json() : Promise.reject(new Error('Failed to create task')))
      .then(createdTask => {
        if (horasEstimadas > 4) {
          const remaining = horasEstimadas - 4;
          setPendingSubtaskPrompt({ tareaId: createdTask.tareaId, remainingHours: remaining });
        }
        return createdTask.tareaId;
      })
      .then(() => {
        // Only reload after the modal is shown
        setTimeout(reloadAllItems, 300); // Delay to let the dialog render cleanly
      })
      .catch(error => {
        setError(error);
      })
      .finally(() => {
        setInserting(false);
      });
  }
  
  function updateDeadline(id, newDeadline) {
    fetch(`${API_TAREAS}/${id}/deadline`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ deadline: newDeadline })
    })
      .then(response => {
        if (response.ok) {
          reloadOneItem(id);
          handleClose();
        } else {
          throw new Error('Failed to update deadline');
        }
      })
      .catch(error => console.error(error));
  }

  function handleOpen(task) {
    setSelectedTask(task);
    setDeadline(task.deadline || "");
  }

  function handleClose() {
    setSelectedTask(null);
    setDeadline("");
  }

  function handleSave() {
    if (selectedTask) {
      const localDate = new Date(deadline);
      const fullISODate = new Date(
        localDate.getTime() - localDate.getTimezoneOffset() * 60000
      ).toISOString();

      updateDeadline(selectedTask.tareaId, fullISODate);
      setItems(items.map(item => item.tareaId === selectedTask.tareaId ? { ...item, deadline: fullISODate } : item));
    }
  }

  function toggleSubtask(taskId) {
    if (expandedTaskId === taskId) {
      setExpandedTaskId(null);
    } else {
      fetchSubTareas(taskId)
        .then(subs => {
          setItems(items.map(item => 
            item.tareaId === taskId 
              ? { ...item, subTareas: subs }  // ✅ Only update the right task
              : item
          ));
          setExpandedTaskId(taskId);
        })
        .catch(err => console.error(err));
    }
  }
  

  return (
    <div className="App">
      <h1>MY TODO LIST</h1>
      <NewItem addItem={addItem} isInserting={isInserting} />
      {error && <p className="errorMessage">Error: {error.message}</p>}
      {isLoading && <CircularProgress />}
      {/* Pending Subtask Prompt */}
      {pendingSubtaskPrompt && (
        <Dialog open={true}>
          <DialogTitle>Break task into subtasks</DialogTitle>
          <DialogContent>
            <p>You have {pendingSubtaskPrompt.remainingHours} hours left to split into subtasks.</p>
            {/* You could add subtask UI fields here */}
          </DialogContent>
          <DialogActions>
            <Button onClick={() => setPendingSubtaskPrompt(null)}>Done</Button>
          </DialogActions>
        </Dialog>
      )}
      {!isLoading && (
        <div id="maincontent">
          <h2>Pending Tasks</h2>
          <table id="itemlistNotDone" className="itemlist">
            <thead>
              <tr>
                <th>Title</th>
                <th>Description</th>
                <th className="deadline">Deadline</th>
                <th className="date">Created On</th>
                <th>Action</th>
              </tr>
            </thead>
            <TableBody>
              {items.map(item => item.estado !== "completado" && (
                <React.Fragment key={item.tareaId}>
                  <tr>
                    <td>
                      <Button onClick={() => toggleSubtask(item.tareaId)}>{item.titulo}</Button>
                    </td>
                    <td>{item.descripcion}</td>
                    <td className="deadline">
                      {item.deadline ? (
                        <Moment format="MMM Do YYYY, hh:mm A" utc>{item.deadline}</Moment>
                      ) : (
                        <Button size="small" onClick={() => handleOpen(item)}>Set Deadline</Button>
                      )}
                    </td>
                    <td className="date">
                      <Moment format="MMM Do YYYY, hh:mm A">{item.fechaCreacion}</Moment>
                    </td>
                    <td>
                      <Button
                        variant="contained"
                        className="DoneButton"
                        onClick={() => {
                          setSelectedCompleteTask(item);
                          setCompleteDialogOpen(true);
                        }}
                        size="small"
                      >
                        Done
                      </Button>
                    </td>
                  </tr>
                  {expandedTaskId === item.tareaId && (
              <tr>
                <td colSpan="5">
                  <ul>
                    {item.subTareas && item.subTareas.map(sub => (
                      <li key={sub.subTareaId}>{sub.titulo} - {sub.estado} - {sub.horasEstimadas}h</li>
                    ))}
                  </ul>

                  {/* Subtask creation form */}
                  <form onSubmit={(e) => {
                    e.preventDefault();
                    createSubTarea(item.tareaId, newSubTitulo, newSubHoras);
                    setNewSubTitulo('');
                    setNewSubHoras('');
                  }}>
                    <input
                      placeholder="Subtask title"
                      value={newSubTitulo}
                      onChange={e => setNewSubTitulo(e.target.value)}
                    />
                    <input
                      type="number"
                      placeholder="Hours"
                      value={newSubHoras}
                      onChange={e => setNewSubHoras(e.target.value)}
                    />
                    <button type="submit">Add Subtask</button>
                  </form>
                </td>
              </tr>
            )}

                </React.Fragment>
              ))}
            </TableBody>
          </table>

          <h2 id="donelist">Completed Tasks</h2>
          <table id="itemlistDone" className="itemlist">
            <thead>
              <tr>
                <th>Title</th>
                <th>Description</th>
                <th className="deadline">Deadline</th>
                <th className="date">Created On</th>
                <th>Actions</th>
              </tr>
            </thead>
            <TableBody>
              {items.map(item => item.estado === "completado" && (
                <tr key={item.tareaId}>
                  <td>{item.titulo}</td>
                  <td>{item.descripcion}</td>
                  <td className="deadline">
                    <Moment format="MMM Do YYYY, hh:mm A" utc>{item.deadline}</Moment>
                  </td>
                  <td className="date">
                    <Moment format="MMM Do YYYY, hh:mm A">{item.fechaCreacion}</Moment>
                  </td>
                  <td className="actions">
                    <Button startIcon={<DeleteIcon />} variant="contained" className="DeleteButton" onClick={() => deleteItem(item.tareaId)}>Delete</Button>
                  </td>
                </tr>
              ))}
            </TableBody>
          </table>
        </div>
      )}

      <Dialog open={!!selectedTask} onClose={handleClose}>
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
          <Button onClick={handleClose} color="secondary">Cancel</Button>
          <Button onClick={handleSave} color="primary">Save</Button>
        </DialogActions>
      </Dialog>

      <Dialog open={completeDialogOpen} onClose={() => setCompleteDialogOpen(false)}>
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
          <Button onClick={() => setCompleteDialogOpen(false)} color="secondary">Cancel</Button>
          <Button
            onClick={() => {
              if (selectedCompleteTask && realHours) {
                markTaskComplete(selectedCompleteTask.tareaId, realHours);
                setCompleteDialogOpen(false);
                setRealHours("");
              }
            }}
            color="primary"
          >
            Confirm
          </Button>
        </DialogActions>
      </Dialog>
    </div>
  );
}

export default App;
