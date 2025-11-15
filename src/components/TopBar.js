import React from 'react';
import {
  AppBar,
  Toolbar,
  Typography,
  IconButton,
  Badge,
  Avatar,
  Box,
  InputBase,
} from '@mui/material';
import {
  Menu as MenuIcon,
  Notifications as NotificationsIcon,
  Search as SearchIcon,
  Brightness4 as DarkModeIcon,
  Brightness7 as LightModeIcon,
  Favorite as FavoriteIcon,
} from '@mui/icons-material';

const TopBar = ({ onMenuToggle }) => {
  return (
    <AppBar 
      position="static" 
      elevation={0}
      sx={{ 
        background: 'linear-gradient(135deg, rgba(42, 36, 56, 0.9) 0%, rgba(30, 27, 46, 0.9) 100%)',
        backdropFilter: 'blur(20px)',
        borderBottom: '1px solid rgba(249, 168, 212, 0.2)',
        position: 'relative',
        overflow: 'hidden',
      }}
    >
      {/* Animated background effect */}
      <Box
        sx={{
          position: 'absolute',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          background: 'linear-gradient(90deg, transparent, rgba(249, 168, 212, 0.05), transparent)',
          animation: 'shimmer 3s infinite',
        }}
      />
      
      <Toolbar sx={{ position: 'relative' }}>
        <IconButton
          color="inherit"
          aria-label="open drawer"
          onClick={onMenuToggle}
          edge="start"
          sx={{ 
            mr: 2,
            color: '#f9a8d4',
            background: 'linear-gradient(135deg, rgba(249, 168, 212, 0.1), rgba(216, 180, 254, 0.1))',
            '&:hover': {
              background: 'linear-gradient(135deg, rgba(249, 168, 212, 0.2), rgba(216, 180, 254, 0.2))',
              transform: 'scale(1.1)',
            },
            transition: 'all 0.3s ease',
          }}
        >
          <MenuIcon />
        </IconButton>

        <Box sx={{ flexGrow: 1 }} />

        {/* Search Bar */}
        <Box 
          className="glass"
          sx={{ 
            display: 'flex', 
            alignItems: 'center', 
            background: 'rgba(255, 255, 255, 0.05)',
            borderRadius: 3,
            px: 2,
            py: 1,
            mr: 2,
            border: '1px solid rgba(249, 168, 212, 0.1)',
            transition: 'all 0.3s ease',
            '&:focus-within': {
              border: '1px solid rgba(249, 168, 212, 0.3)',
              boxShadow: '0 0 20px rgba(249, 168, 212, 0.2)',
            }
          }}
        >
          <SearchIcon sx={{ color: '#d8b4fe', mr: 1 }} />
          <InputBase
            placeholder="Search something magical..."
            sx={{ color: 'white', '&::placeholder': { color: '#a5b4fc', opacity: 0.7 } }}
          />
        </Box>

        {/* Notifications */}
        <IconButton 
          sx={{ 
            mr: 1,
            color: '#f9a8d4',
            '&:hover': {
              transform: 'scale(1.1)',
            },
            transition: 'all 0.3s ease',
          }}
        >
          <Badge badgeContent={4} color="secondary">
            <NotificationsIcon />
          </Badge>
        </IconButton>

        {/* Theme Toggle */}
        <IconButton 
          className="heartbeat"
          sx={{ 
            mr: 2,
            color: '#d8b4fe',
          }}
        >
          <DarkModeIcon />
        </IconButton>

        {/* User Avatar */}
        <Avatar
          sx={{
            width: 42,
            height: 42,
            background: 'linear-gradient(135deg, #f9a8d4 0%, #d8b4fe 50%, #a5b4fc 100%)',
            border: '2px solid rgba(255, 255, 255, 0.2)',
            transition: 'all 0.3s ease',
            '&:hover': {
              transform: 'scale(1.1)',
              border: '2px solid rgba(249, 168, 212, 0.5)',
              boxShadow: '0 0 20px rgba(249, 168, 212, 0.4)',
            }
          }}
        >
          👑
        </Avatar>
      </Toolbar>
    </AppBar>
  );
};

export default TopBar;