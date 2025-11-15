import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import {
  Drawer,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
  Typography,
  Box,
  IconButton,
} from '@mui/material';
import {
  Dashboard as DashboardIcon,
  Analytics as AnalyticsIcon,
  People as PeopleIcon,
  Inventory as InventoryIcon,
  Settings as SettingsIcon,
  ChevronLeft as ChevronLeftIcon,
  ChevronRight as ChevronRightIcon,
  Favorite as FavoriteIcon,
} from '@mui/icons-material';

const menuItems = [
  { text: 'Dashboard', icon: <DashboardIcon />, path: '/', emoji: '🏠' },
  { text: 'Analytics', icon: <AnalyticsIcon />, path: '/analytics', emoji: '📊' },
  { text: 'Users', icon: <PeopleIcon />, path: '/users', emoji: '👥' },
  { text: 'Products', icon: <InventoryIcon />, path: '/products', emoji: '🛍️' },
  { text: 'Settings', icon: <SettingsIcon />, path: '/settings', emoji: '⚙️' },
];

const Sidebar = ({ open, onToggle }) => {
  const location = useLocation();

  return (
    <Drawer
      variant="permanent"
      sx={{
        width: open ? 180 : 80,
        flexShrink: 0,
        '& .MuiDrawer-paper': {
          width: open ? 280 : 80,
          boxSizing: 'border-box',
          background: 'linear-gradient(180deg, #2a2438 0%, #1e1b2e 100%)',
          borderRight: '1px solid rgba(249, 168, 212, 0.2)',
          transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
          overflowX: 'hidden',
          position: 'relative',
        },
      }}
    >
      {/* Sidebar Glitter Effect */}
      <Box
        sx={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'radial-gradient(circle at 30% 70%, rgba(249, 168, 212, 0.1) 0%, transparent 50%)',
          pointerEvents: 'none',
        }}
      />
      
      <Box sx={{ p: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between', position: 'relative' }}>
        {open && (
          <Box sx={{ display: 'flex', alignItems: 'center' }}>
            <Typography variant="h5" className="gradient-text" fontWeight="bold" sx={{ mr: 1 }}>
              HOTEL MANAGEMENT
            </Typography>
            <Box className="heartbeat">👑</Box>
          </Box>
        )}
        <IconButton
          onClick={() => onToggle(!open)}
          sx={{
            color: '#f9a8d4',
            background: 'linear-gradient(135deg, rgba(249, 168, 212, 0.2), rgba(216, 180, 254, 0.2))',
            border: '1px solid rgba(249, 168, 212, 0.3)',
            '&:hover': { 
              background: 'linear-gradient(135deg, rgba(249, 168, 212, 0.3), rgba(216, 180, 254, 0.3))',
              transform: 'scale(1.1)',
            },
            transition: 'all 0.3s ease',
          }}
        >
          {open ? <ChevronLeftIcon /> : <ChevronRightIcon />}
        </IconButton>
      </Box>

      <List sx={{ mt: 2, position: 'relative' }}>
        {menuItems.map((item) => (
          <ListItem
            key={item.text}
            component={Link}
            to={item.path}
            sx={{
              margin: '0.5rem 1rem',
              borderRadius: 3,
              background: location.pathname === item.path 
                ? 'linear-gradient(135deg, #f9a8d4 0%, #d8b4fe 50%, #a5b4fc 100%)'
                : 'transparent',
              '&:hover': {
                background: location.pathname === item.path 
                  ? 'linear-gradient(135deg, #f9a8d4 0%, #d8b4fe 50%, #a5b4fc 100%)'
                  : 'linear-gradient(135deg, rgba(249, 168, 212, 0.1), rgba(216, 180, 254, 0.1))',
                transform: 'translateX(5px)',
                border: location.pathname === item.path 
                  ? 'none'
                  : '1px solid rgba(249, 168, 212, 0.2)',
              },
              transition: 'all 0.3s ease',
              textDecoration: 'none',
              color: 'white',
              position: 'relative',
              overflow: 'hidden',
            }}
          >
            {location.pathname === item.path && (
              <Box
                className="float"
                sx={{
                  position: 'absolute',
                  right: 8,
                  fontSize: '1.2rem',
                }}
              >
                {item.emoji}
              </Box>
            )}
            <ListItemIcon 
              sx={{ 
                color: 'inherit', 
                minWidth: 40,
                transition: 'all 0.3s ease',
              }}
            >
              {item.icon}
            </ListItemIcon>
            {open && (
              <ListItemText 
                primary={item.text} 
                sx={{
                  '& .MuiTypography-root': {
                    fontWeight: location.pathname === item.path ? 'bold' : 'normal',
                    transition: 'all 0.3s ease',
                  }
                }}
              />
            )}
          </ListItem>
        ))}
      </List>

      {/* Favorite Star at bottom */}
      <Box sx={{ p: 2, mt: 'auto', textAlign: 'center' }}>
        <IconButton className="heartbeat" sx={{ color: '#f9a8d4' }}>
          <FavoriteIcon />
        </IconButton>
        {open && (
          <Typography variant="caption" sx={{ color: '#d8b4fe', display: 'block', mt: 1 }}>
            Made by Group *  
            💖
          </Typography>
        )}
      </Box>
    </Drawer>
  );
};

export default Sidebar;