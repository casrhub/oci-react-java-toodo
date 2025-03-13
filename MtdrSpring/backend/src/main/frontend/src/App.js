import React, { useState, useEffect } from 'react';
import NewItem from './NewItem';
import API_LIST from './API';
import DeleteIcon from '@mui/icons-material/Delete';
import { Button, TableBody, CircularProgress } from '@mui/material';
import Moment from 'react-moment';

function App() {
    const [isLoading, setLoading] = useState(false);
    const [isInserting, setInserting] = useState(false);
    const [items, setItems] = useState([]);
    const [error, setError] = useState();

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
        modifyItem(id, description, done).then(() => reloadOneItem(id), (error) => setError(error));
    }

    function reloadOneItem(id) {
        fetch(API_LIST + '/' + id)
        .then(response => response.ok ? response.json() : Promise.reject(new Error('Something went wrong ...')))
        .then(result => {
            setItems(items.map(x => (x.id === id ? { ...x, description: result.description, done: result.done, deadline: result.deadline } : x)));
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

//deadline put fetch
    function updateDeadline(id, newDeadline) {
      fetch(`${API_LIST}/${id}/deadline`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(newDeadline) // Send the deadline as a string
      })
      .then(response => {
        if (response.ok) {
          reloadOneItem(id);
        } else {
          throw new Error('Failed to update deadline');
        }
      })
      .catch(error => console.error(error));
    }
    

    return (
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
                        <Moment format="MMM Do YYYY, hh:mm A" utc>{item.deadline}</Moment>
                      ) : (
                        "No Deadline"
                      )}
                    </td>
                    <td className="date">
                      <Moment format="MMM Do YYYY, hh:mm A">{item.creation_ts}</Moment>
                    </td>
                    <td>
                      <Button variant="contained" className="DoneButton" onClick={(event) => toggleDone(event, item.id, item.description, !item.done)} size="small">
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
                        <Moment format="MMM Do YYYY, hh:mm A" utc>{item.deadline}</Moment>
                      ) : (
                        "No Deadline"
                      )}
                    </td>
                    <td className="date">
                      <Moment format="MMM Do YYYY, hh:mm A">{item.creation_ts}</Moment>
                    </td>
                    <td className="actions">
                      <Button variant="contained" className="DoneButton">Undo</Button>
                      <Button startIcon={<DeleteIcon />} variant="contained" className="DeleteButton">Delete</Button>
                    </td>

                  </tr>
                ))}
              </TableBody>
            </table>
          </div>
        )}
      </div>
    );
}

export default App;
