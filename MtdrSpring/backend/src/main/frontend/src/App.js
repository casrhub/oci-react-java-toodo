/*
## MyToDoReact version 1.0.
##
## Copyright (c) 2022 Oracle, Inc.
## Licensed under the Universal Permissive License v 1.0 as shown at https://oss.oracle.com/licenses/upl/
*/
/*
 * This is the application main React component. We're using "function"
 * components in this application. No "class" components should be used for
 * consistency.
 * @author  jean.de.lavarene@oracle.com
 */
import React, { useState, useEffect } from 'react';
import NewItem from './NewItem';
import API_LIST from './API';
import DeleteIcon from '@mui/icons-material/Delete';
import { Button, TableBody, CircularProgress } from '@mui/material';
import Moment from 'react-moment';

/* In this application we're using Function Components with the State Hooks
 * to manage the states. See the doc: https://reactjs.org/docs/hooks-state.html
 * This App component represents the entire app. It renders a NewItem component
 * and two tables: one that lists the todo items that are to be done and another
 * one with the items that are already done.
 */
function App() {
    // isLoading is true while waiting for the backend to return the list
    // of items. We use this state to display a spinning circle:
    const [isLoading, setLoading] = useState(false);
    // Similar to isLoading, isInserting is true while waiting for the backend
    // to insert a new item:
    const [isInserting, setInserting] = useState(false);
    // The list of todo items is stored in this state. It includes the "done"
    // "not-done" items:
    const [items, setItems] = useState([]);
    // In case of an error during the API call:
    const [error, setError] = useState();

    function deleteItem(deleteId) {
      fetch(API_LIST + '/' + deleteId, {
        method: 'DELETE',
      })
      .then(response => {
        if (response.ok) {
          const remainingItems = items.filter(item => item.id !== deleteId);
          setItems(remainingItems);
        } else {
          throw new Error('Something went wrong ...');
        }
      })
      .catch(err => {
        setError(err);
      });
    }

    function toggleDone(event, id, description, done) {
      event.preventDefault();
      modifyItem(id, description, done).then(
        () => { reloadOneItem(id); },
        (error) => { setError(error); }
      );
    }

    function reloadOneItem(id){
      fetch(API_LIST + '/' + id)
        .then(response => {
          if (response.ok) {
            return response.json();
          } else {
            throw new Error('Something went wrong ...');
          }
        })
        .then(
          (result) => {
            const updatedItems = items.map(x =>
              x.id === id
                ? { ...x, description: result.description, done: result.done }
                : x
            );
            setItems(updatedItems);
          },
          (error) => { setError(error); }
        );
    }

    function modifyItem(id, description, done) {
      const data = { description, done };
      return fetch(API_LIST + '/' + id, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
      })
      .then(response => {
        if (response.ok) {
          return response;
        } else {
          throw new Error('Something went wrong ...');
        }
      });
    }

    useEffect(() => {
      setLoading(true);
      fetch(API_LIST)
        .then(response => {
          if (response.ok) {
            return response.json();
          } else {
            throw new Error('Something went wrong ...');
          }
        })
        .then(
          (result) => {
            setLoading(false);
            setItems(result);
          },
          (error) => {
            setLoading(false);
            setError(error);
          }
        );
    }, []);

    function addItem(text){
      setInserting(true);
      const data = { description: text };
      fetch(API_LIST, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(data),
      })
      .then((response) => {
        if (response.ok) {
          return response;
        } else {
          throw new Error('Something went wrong ...');
        }
      })
      .then(
        (result) => {
          const id = result.headers.get('location');
          const newItem = { id, description: text };
          setItems([newItem, ...items]);
          setInserting(false);
        },
        (error) => {
          setInserting(false);
          setError(error);
        }
      );
    }

    return (
      <div className="App">
        <h1>MY TODO LIST</h1>
        <NewItem addItem={addItem} isInserting={isInserting} />
        { error && (
          <p className="errorMessage">Error: {error.message}</p>
        )}
        { isLoading && (
          <CircularProgress />
        )}
        { !isLoading && (
        <div id="maincontent">
          <table id="itemlistNotDone" className="itemlist">
            <TableBody>
              {items.map(item => (
                !item.done && (
                <tr key={item.id}>
                  <td className="description">{item.description}</td>
                  <td className="deadline">
                    {item.deadline ? (
                      <Moment format="MMM Do YYYY, hh:mm A">{item.deadline}</Moment>
                    ) : (
                      "No Deadline"
                    )}
                  </td>
                  <td className="date">
                    <Moment format="MMM Do hh:mm:ss">{item.createdAt}</Moment>
                  </td>
                  <td>
                    <Button
                      variant="contained"
                      className="DoneButton"
                      onClick={(event) => toggleDone(event, item.id, item.description, !item.done)}
                      size="small"
                    >
                      Done
                    </Button>
                  </td>
                </tr>
              )))}
            </TableBody>
          </table>

          <h2 id="donelist">Done items</h2>
          <table id="itemlistDone" className="itemlist">
            <TableBody>
              {items.map(item => (
                item.done && (
                <tr key={item.id}>
                  <td className="description">{item.description}</td>
                  <td className="date">
                    <Moment format="MMM Do hh:mm:ss">{item.createdAt}</Moment>
                  </td>
                  <td className="deadline">
                    {item.deadline ? (
                      <Moment format="MMM Do YYYY, hh:mm A">{item.deadline}</Moment>
                    ) : (
                      "No Deadline"
                    )}
                  </td>
                  <td>
                    <Button
                      variant="contained"
                      className="DoneButton"
                      onClick={(event) => toggleDone(event, item.id, item.description, !item.done)}
                      size="small"
                    >
                      Undo
                    </Button>
                  </td>
                  <td>
                    <Button
                      startIcon={<DeleteIcon />}
                      variant="contained"
                      className="DeleteButton"
                      onClick={() => deleteItem(item.id)}
                      size="small"
                    >
                      Delete
                    </Button>
                  </td>
                </tr>
              )))}
            </TableBody>
          </table>
        </div>
        )}
      </div>
    );
}

export default App;
