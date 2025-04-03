import React, { useState } from 'react';
import {
  Table, TableHead, TableBody, TableRow, TableCell,
  TableContainer, Paper, Button, Menu, MenuItem, Toolbar, Typography
} from '@mui/material';
import FilterListIcon from '@mui/icons-material/FilterList';
import AddIcon from '@mui/icons-material/Add';

function DevTasksPage() {
  // Hardcoded tasks
  const tasks = [
    {
      id: 1,
      title: 'Cell Data',
      status: 'In Progress',
      deadline: '12/04/2025',
      member: 'Miguel Ángel Barrientos'
    },
    {
      id: 2,
      title: 'Cell Data',
      status: 'Done',
      deadline: '11/04/2025',
      member: 'Fernanda Díaz'
    },
    {
      id: 3,
      title: 'Cell Data',
      status: 'To Do',
      deadline: '10/04/2025',
      member: 'Not Assigned'
    },
    {
      id: 4,
      title: 'Cell Data',
      status: 'In Progress',
      deadline: '09/04/2025',
      member: 'César Alan Silva'
    },
    {
      id: 5,
      title: 'Cell Data',
      status: 'In Progress',
      deadline: '07/04/2025',
      member: 'Jose María Soto'
    },
    {
      id: 6,
      title: 'Cell Data',
      status: 'Done',
      deadline: '05/04/2025',
      member: 'Miguel Ángel Barrientos'
    },
    {
      id: 7,
      title: 'Cell Data',
      status: 'In Progress',
      deadline: '02/04/2025',
      member: 'Diego Iván Morales'
    }
  ];

  // For the Filter dropdown
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  const handleOpenMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleCloseMenu = () => {
    setAnchorEl(null);
  };

  // Add Task (currently no functionality)
  const handleAddTask = () => {
    console.log('Add task clicked');
  };

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
            onClick={handleOpenMenu}
            sx={{ marginRight: '1rem' }}
          >
            Filter
          </Button>
          <Menu
            anchorEl={anchorEl}
            open={open}
            onClose={handleCloseMenu}
            PaperProps={{ style: { maxHeight: 200 } }}
          >
            {/* Future filter options go here */}
            <MenuItem onClick={handleCloseMenu}>No filters yet</MenuItem>
          </Menu>

          {/* Add Task Button */}
          <Button
            variant="contained"
            startIcon={<AddIcon />}
            onClick={handleAddTask}
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

      {/* Table */}
      <TableContainer component={Paper} sx={{ marginTop: '1rem' }}>
        <Table>
          <TableHead sx={{ backgroundColor: '#C74634' }}>
            <TableRow>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>#</TableCell>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Title</TableCell>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Status</TableCell>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Deadline</TableCell>
              <TableCell sx={{ color: 'white', fontWeight: 'bold' }}>Member</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {tasks.map((task, index) => (
              <TableRow key={task.id}>
                <TableCell>{index + 1}</TableCell>
                <TableCell>{task.title}</TableCell>
                <TableCell>{task.status}</TableCell>
                <TableCell>{task.deadline}</TableCell>
                <TableCell>{task.member}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
    </div>
  );
}

export default DevTasksPage;
