import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';
import { ClerkProvider } from '@clerk/clerk-react';

const clerkPubKey = "pk_test_Z2FtZS13YWxydXMtMC5jbGVyay5hY2NvdW50cy5kZXYk";

ReactDOM.render(
  <React.StrictMode>
    <ClerkProvider publishableKey={clerkPubKey}>
      <App />
    </ClerkProvider>
  </React.StrictMode>,
  document.getElementById('root')
);
