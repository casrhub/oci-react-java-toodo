import React, { useState, useEffect } from 'react';
import NewItem from '../components/tasks/NewItem';  // adjusted import paths
import API_LIST from '../api';                      // adjusted import paths

import DeleteIcon from '@mui/icons-material/Delete';
import { Button, TableBody, CircularProgress, Dialog, DialogActions, DialogContent, DialogTitle, TextField } from '@mui/material';
import Moment from 'react-moment';

function Home() {
  const [isLoading, setLoading] = useState(false);
  const [isInserting, setInserting] = useState(false);
  const [items, setItems] = useState([]);
  const [error, setError] = useState();
  const [selectedTask, setSelectedTask] = useState(null);
  const [deadline, setDeadline] = useState("");

  function deleteItem(deleteId) {
    fetch(API_LIST + '/' + deleteId, { method: 'DELETE' })
      .then(response => {
        if (response.ok) {
          setItems(items.filter(item => item.id !== deleteId));
        } else {
          throw new Error('Something went wrong ...');
        }
      })
      .catch(err => setError(err));
  }

  function toggleDone(event, id, description, done) {
    event.preventDefault();
    modifyItem(id, description, done).then(
      () => reloadOneItem(id),
      (error) => setError(error)
    );
  }

  function reloadOneItem(id) {
    fetch(API_LIST + '/' + id)
      .then(response => response.ok ? response.json() : Promise.reject(new Error('Something went wrong ...')))
      .then(result => {
        setItems(items.map(x => 
          (x.id === id 
            ? { ...x, description: result.description, done: result.done, deadline: result.deadline }
            : x
          )
        ));
      })
      .catch(error => setError(error));
  }

  function modifyItem(id, description, done) {
    return fetch(API_LIST + '/' + id, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ description, done })
    })
      .then(response => response.ok ? response : Promise.reject(new Error('Something went wrong ...')));
  }

  useEffect(() => {
    setLoading(true);
    fetch(API_LIST)
      .then(response => response.ok ? response.json() : Promise.reject(new Error('Something went wrong ...')))
      .then(result => {
        setLoading(false);
        setItems(result);
      })
      .catch(error => {
        setLoading(false);
        setError(error);
      });
  }, []);

  function addItem(text) {
    setInserting(true);
    fetch(API_LIST, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ description: text })
    })
      .then(response => response.ok ? response : Promise.reject(new Error('Something went wrong ...')))
      .then(result => {
        const id = result.headers.get('location');
        setItems([{ id, description: text }, ...items]);
        setInserting(false);
      })
      .catch(error => {
        setInserting(false);
        setError(error);
      });
  }

  // Function to handle deadline update
  function updateDeadline(id, newDeadline) {
    fetch(`${API_LIST}/${id}/deadline`, {
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

  // Open modal to edit deadline
  function handleOpen(task) {
    setSelectedTask(task);
    setDeadline(task.deadline || "");
  }

  // Close modal
  function handleClose() {
    setSelectedTask(null);
    setDeadline("");
  }

  // Handle deadline save
  function handleSave() {
    if (selectedTask) {
      // Convert user input (local time) to UTC before saving
      const localDate = new Date(deadline);
      const fullISODate = new Date(
        localDate.getTime() - localDate.getTimezoneOffset() * 60000
      ).toISOString();

      updateDeadline(selectedTask.id, fullISODate);

      // Update UI immediately with the corrected UTC time
      setItems(items.map(item => 
        item.id === selectedTask.id
          ? { ...item, deadline: fullISODate }
          : item
      ));
    }
  }

  return (
    <div className="themed-wrapper">
      <div className="App">
        <h1>MY TODO LIST</h1>
        <NewItem addItem={addItem} isInserting={isInserting} />
        {error && <p className="errorMessage">Error: {error.message}</p>}
        {isLoading && <CircularProgress />}
        {!isLoading && (
          <div id="maincontent">
            {/* ✅ Not Done Items Table */}
            <h2>Pending Tasks</h2>
            <table id="itemlistNotDone" className="itemlist">
              <thead>
                <tr>
                  <th>Description</th>
                  <th className="deadline">Deadline</th>
                  <th className="date">Created On</th>
                  <th>Action</th>
                </tr>
              </thead>
              <TableBody>
                {items.map(item => !item.done && (
                  <tr key={item.id}>
                    <td className="description">{item.description}</td>
                    <td className="deadline">
                      {item.deadline ? (
                        <Moment format="MMM Do YYYY, hh:mm A" utc>
                          {item.deadline}
                        </Moment>
                      ) : (
                        <Button size="small" onClick={() => handleOpen(item)}>
                          Set Deadline
                        </Button>
                      )}
                    </td>
                    <td className="date">
                      <Moment format="MMM Do YYYY, hh:mm A">
                        {item.creation_ts}
                      </Moment>
                    </td>
                    <td>
                      <Button
                        variant="contained"
                        className="DoneButton"
                        onClick={(event) =>
                          toggleDone(event, item.id, item.description, !item.done)
                        }
                        size="small"
                      >
                        Done
                      </Button>
                    </td>
                  </tr>
                ))}
              </TableBody>
            </table>

            {/* ✅ Done Items Table */}
            <h2 id="donelist">Completed Tasks</h2>
            <table id="itemlistDone" className="itemlist">
              <thead>
                <tr>
                  <th>Description</th>
                  <th className="deadline">Deadline</th>
                  <th className="date">Created On</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <TableBody>
                {items.map(item => item.done && (
                  <tr key={item.id}>
                    <td className="description">{item.description}</td>
                    <td className="deadline">
                      {item.deadline ? (
                        <Moment format="MMM Do YYYY, hh:mm A" utc>
                          {item.deadline}
                        </Moment>
                      ) : (
                        <Button size="small" onClick={() => handleOpen(item)}>
                          Set Deadline
                        </Button>
                      )}
                    </td>
                    <td className="date">
                      <Moment format="MMM Do YYYY, hh:mm A">
                        {item.creation_ts}
                      </Moment>
                    </td>
                    <td className="actions">
                      <Button variant="contained" className="DoneButton">
                        Undo
                      </Button>
                      <Button
                        startIcon={<DeleteIcon />}
                        variant="contained"
                        className="DeleteButton"
                      >
                        Delete
                      </Button>
                    </td>
                  </tr>
                ))}
              </TableBody>
            </table>
          </div>
        )}

        {/* 📅 Deadline Modal */}
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
            <Button onClick={handleClose} color="secondary">
              Cancel
            </Button>
            <Button onClick={handleSave} color="primary">
              Save
            </Button>
          </DialogActions>
        </Dialog>
      </div>
    </div>
  );
}

export default Home;
