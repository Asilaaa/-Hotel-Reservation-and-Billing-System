import React, { useState } from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider, createTheme } from '@mui/material/styles';
import CssBaseline from '@mui/material/CssBaseline';
import './App.css';

import Sidebar from './components/Sidebar';
import TopBar from './components/TopBar';
import Dashboard from './pages/Dashboard';
import Analytics from './pages/Analytics';
import Users from './pages/Users';
import Products from './pages/Products';
import Settings from './pages/Settings';

const theme = createTheme({
  palette: {
    mode: 'dark',
    primary: {
      main: '#d8b4fe',
      light: '#e9d5ff',
      dark: '#c084fc',
    },
    secondary: {
      main: '#f9a8d4',
      light: '#fbcfe8',
      dark: '#f472b6',
    },
    background: {
      default: '#1e1b2e',
      paper: '#2a2438',
    },
    success: {
      main: '#a5b4fc',
      light: '#c7d2fe',
      dark: '#818cf8',
    },
    info: {
      main: '#c7d2fe',
      light: '#e0e7ff',
      dark: '#a5b4fc',
    },
  },
  typography: {
    fontFamily: '"Lobster", "Pacifico", "Caveat", cursive',
    h3: {
      fontWeight: 'normal',
      background: 'linear-gradient(135deg, #f9a8d4 0%, #d8b4fe 50%, #a5b4fc 100%)',
      WebkitBackgroundClip: 'text',
      WebkitTextFillColor: 'transparent',
      backgroundClip: 'text',
    },
    h4: {
      fontFamily: '"Lobster", "Pacifico", "Caveat", cursive',
      fontWeight: 'normal',
    },
    h6: {
      fontFamily: '"Lobster", "Pacifico", "Caveat", cursive',
      fontWeight: 'normal',
    },
    body1: {
      fontFamily: '"Pacifico", "Caveat", "Lobster", cursive',
    },
    body2: {
      fontFamily: '"Pacifico", "Caveat", "Lobster", cursive',
    },
  },
  shape: {
    borderRadius: 16,
  },
  components: {
    MuiCard: {
      styleOverrides: {
        root: {
          background: 'rgba(255, 255, 255, 0.05)',
          backdropFilter: 'blur(10px)',
          border: '1px solid rgba(255, 255, 255, 0.1)',
        },
      },
    },
    MuiTypography: {
      styleOverrides: {
        root: {
          fontFamily: '"Pacifico", "Caveat", "Lobster", cursive',
        },
      },
    },
  },
});

function App() {
  const [sidebarOpen, setSidebarOpen] = useState(true);

  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      {/* Glitter Background Effect */}
      <div className="glitter-bg"></div>
      <Router>
        <div className="app">
          <Sidebar open={sidebarOpen} onToggle={setSidebarOpen} />
          <div className={`main-content ${sidebarOpen ? 'sidebar-open' : 'sidebar-closed'}`}>
            <TopBar onMenuToggle={() => setSidebarOpen(!sidebarOpen)} />
            <div className="content-area">
              <Routes>
                <Route path="/" element={<Dashboard />} />
                <Route path="/analytics" element={<Analytics />} />
                <Route path="/users" element={<Users />} />
                <Route path="/products" element={<Products />} />
                <Route path="/settings" element={<Settings />} />
              </Routes>
            </div>
          </div>
        </div>
      </Router>
    </ThemeProvider>
  );
}

export default App;