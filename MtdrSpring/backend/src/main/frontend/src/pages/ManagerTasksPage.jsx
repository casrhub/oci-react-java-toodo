import React, { useState } from 'react';
import {
  Table,
  TableHead,
  TableBody,
  TableRow,
  TableCell,
  TableContainer,
  Paper,
  Button,
  Menu,
  MenuItem,
  Toolbar,
  Typography
} from '@mui/material';
import FilterListIcon from '@mui/icons-material/FilterList';
import AddIcon from '@mui/icons-material/Add';

function ManagerTasksPage() {
  // Hardcoded tasks for managers
  const tasks = [
    {
      id: 1,
      title: 'Review Budget',
      status: 'Pending',
      deadline: '15/04/2025',
      member: 'Alice Johnson'
    },
    {
      id: 2,
      title: 'Team Meeting',
      status: 'Completed',
      deadline: '13/04/2025',
      member: 'Bob Smith'
    },
    {
      id: 3,
      title: 'Project Update',
      status: 'In Progress',
      deadline: '16/04/2025',
      member: 'Charlie Davis'
    }
  ];

  // State for Filter dropdown
  const [anchorEl, setAnchorEl] = useState(null);
  const open = Boolean(anchorEl);

  const handleOpenMenu = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleCloseMenu = () => {
    setAnchorEl(null);
  };

  // Placeholder for add task action
  const handleAddTask = () => {
    console.log('Add task clicked');
  };

  return (
    <div style={{ padding: '1rem' }}>
      {/* Header Toolbar */}
      <Toolbar disableGutters sx={{ justifyContent: 'space-between' }}>
        <Typography variant="h5" component="div" sx={{ fontWeight: 'bold' }}>
          Manager Tasks
        </Typography>
        <div>
          {/* Filter Button with Dropdown */}
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
            {/* Future filter options */}
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

      {/* Task Table */}
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

export default ManagerTasksPage;
