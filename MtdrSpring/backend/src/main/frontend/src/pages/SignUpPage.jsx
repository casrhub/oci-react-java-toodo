import React, { useState } from 'react';
import { Box, Button, MenuItem, Select, TextField, Typography, Paper } from '@mui/material';
import { useNavigate } from 'react-router-dom';
import { auth } from '../firebase';
import { createUserWithEmailAndPassword } from 'firebase/auth';
import { saveUserRole } from '../context/AuthContext';

export default function SignUpPage() {
  const nav = useNavigate();
  const [email, setEmail] = useState('');
  const [pass, setPass] = useState('');
  const [role, setRole] = useState('developer');
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      const { user } = await createUserWithEmailAndPassword(auth, email, pass);
      await saveUserRole(user.uid, role);
      nav(role === 'manager' ? '/manager/tasks' : '/dev/tasks', { replace: true });
    } catch (err) {
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ mt: 6, display: 'flex', justifyContent: 'center' }}>
      <Paper sx={{ p: 4, minWidth: 320 }}>
        <Typography variant="h5" gutterBottom>
          Create account
        </Typography>

        <Box
          component="form"
          onSubmit={handleSubmit}
          sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}
        >
          <TextField
            label="Email"
            type="email"
            required
            fullWidth
            value={email}
            onChange={(e) => setEmail(e.target.value)}
          />
          <TextField
            label="Password"
            type="password"
            required
            fullWidth
            value={pass}
            onChange={(e) => setPass(e.target.value)}
          />
          <Select value={role} onChange={(e) => setRole(e.target.value)} fullWidth>
            <MenuItem value="developer">Developer</MenuItem>
            <MenuItem value="manager">Manager</MenuItem>
          </Select>
          {error && <Typography color="error">{error}</Typography>}
          <Button type="submit" variant="contained" disabled={loading} sx={{ bgcolor: '#C74634' }}>
            {loading ? 'Creating…' : 'Sign Up'}
          </Button>
          <Button onClick={() => nav('/sign-in')} disabled={loading}>
            Already have an account?
          </Button>
        </Box>
      </Paper>
    </Box>
  );
}
