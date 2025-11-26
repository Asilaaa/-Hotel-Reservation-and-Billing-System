import React from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  LinearProgress,
  Chip,
  IconButton,
} from '@mui/material';
import {
  TrendingUp as TrendingUpIcon,
  People as PeopleIcon,
  ShoppingCart as ShoppingCartIcon,
  AttachMoney as MoneyIcon,
  Favorite as FavoriteIcon,
  Star as StarIcon,
} from '@mui/icons-material';

const StatCard = ({ title, value, change, icon, color, isFavorite = false }) => (
  <Card className="glass pulse-glow" sx={{ 
    height: '100%',
    position: 'relative',
    transition: 'all 0.3s ease',
    '&:hover': {
      transform: 'translateY(-5px)',
    }
  }}>
    {isFavorite && (
      <Box className="crown"></Box>
    )}
    <CardContent>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
        <Box>
          <Typography color="textSecondary" gutterBottom variant="overline" sx={{ opacity: 0.8 }}>
            {title}
          </Typography>
          <Typography variant="h4" component="div" fontWeight="bold" sx={{ mb: 1 }}>
            {value}
          </Typography>
          <Chip 
            label={change} 
            size="small" 
            sx={{ 
              background: `linear-gradient(135deg, ${color}40, ${color}20)`,
              color: color,
              fontWeight: 'bold',
              border: `1px solid ${color}30`,
            }}
          />
        </Box>
        <Box
          className="heartbeat"
          sx={{
            background: `linear-gradient(135deg, ${color}30 0%, ${color}10 100%)`,
            borderRadius: 3,
            p: 1.5,
            border: `1px solid ${color}20`,
          }}
        >
          {icon}
        </Box>
      </Box>
    </CardContent>
  </Card>
);

const Dashboard = () => {
  const stats = [
    {
      title: 'TOTAL REVENUE',
      value: '$42,567',
      change: '+12.5%',
      icon: <MoneyIcon sx={{ color: '#a5b4fc', fontSize: 32 }} />,
      color: '#a5b4fc', // Lavender blue
      isFavorite: true
    },
    {
      title: 'ACTIVE USERS',
      value: '12,458',
      change: '+8.2%',
      icon: <PeopleIcon sx={{ color: '#d8b4fe', fontSize: 32 }} />, // Lavender
      color: '#d8b4fe'
    },
    {
      title: 'ORDERS',
      value: '1,245',
      change: '+15.3%',
      icon: <ShoppingCartIcon sx={{ color: '#f9a8d4', fontSize: 32 }} />, // Pink
      color: '#f9a8d4'
    },
    {
      title: 'GROWTH',
      value: '32.8%',
      change: '+5.7%',
      icon: <TrendingUpIcon sx={{ color: '#c7d2fe', fontSize: 32 }} />, // Light lavender
      color: '#c7d2fe'
    },
  ];

  const quickActions = [
    { text: '✨ Add New User', icon: '👥' },
    { text: '📊 Create Report', icon: '📈' },
    { text: '⚙️ Update Settings', icon: '🎀' },
    { text: '📱 View Analytics', icon: '💫' },
  ];

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', mb: 3 }}>
        <Typography variant="h3" gutterBottom fontWeight="bold" className="gradient-text">
          Dashboard 👑
        </Typography>
        <IconButton className="heartbeat" sx={{ ml: 2, color: '#f9a8d4' }}>
          <FavoriteIcon />
        </IconButton>
      </Box>
      
      <Grid container spacing={3} sx={{ mt: 2 }}>
        {stats.map((stat, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <StatCard {...stat} />
          </Grid>
        ))}

        {/* Recent Activity */}
        <Grid item xs={12} md={8}>
          <Card className="glass" sx={{ height: 400, position: 'relative' }}>
            <Box className="shimmer"></Box>
            <CardContent>
              <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
                ✨ Performance Metrics
                <StarIcon sx={{ color: '#f9a8d4', ml: 1, fontSize: 20 }} />
              </Typography>
              <Box sx={{ mt: 3 }}>
                {['Website Visits', 'Conversion Rate', 'Bounce Rate', 'Avg. Session'].map((metric, index) => (
                  <Box key={metric} sx={{ mb: 3 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 1 }}>
                      <Typography variant="body2" sx={{ opacity: 0.9 }}>{metric}</Typography>
                      <Typography variant="body2" fontWeight="bold" className="gradient-text">
                        {[12543, 3.2, 42.1, '00:03:45'][index]}
                      </Typography>
                    </Box>
                    <LinearProgress 
                      variant="determinate" 
                      value={[75, 32, 42, 60][index]} 
                      sx={{ 
                        height: 8, 
                        borderRadius: 4,
                        background: 'rgba(255, 255, 255, 0.1)',
                        '& .MuiLinearProgress-bar': {
                          background: `linear-gradient(90deg, #f9a8d4, #d8b4fe, #a5b4fc)`,
                          borderRadius: 4,
                        }
                      }}
                    />
                  </Box>
                ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>

        <Grid item xs={12} md={4}>
          <Card className="glass pulse-glow" sx={{ height: 400 }}>
            <CardContent>
              <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
                🎀 Quick Actions
                <Box className="bounce" sx={{ ml: 1 }}>💖</Box>
              </Typography>
              <Box sx={{ mt: 2 }}>
                {quickActions.map((action, index) => (
                  <Box
                    key={action.text}
                    className="glass"
                    sx={{
                      p: 2,
                      mb: 1,
                      background: 'rgba(255, 255, 255, 0.05)',
                      borderRadius: 3,
                      cursor: 'pointer',
                      transition: 'all 0.3s ease',
                      display: 'flex',
                      alignItems: 'center',
                      '&:hover': {
                        background: 'linear-gradient(135deg, rgba(249, 168, 212, 0.2), rgba(216, 180, 254, 0.2))',
                        transform: 'translateX(8px) scale(1.02)',
                        border: '1px solid rgba(249, 168, 212, 0.3)',
                      },
                    }}
                  >
                    <Typography variant="h6" sx={{ mr: 2 }}>{action.icon}</Typography>
                    <Typography sx={{ fontWeight: 500 }}>{action.text}</Typography>
                  </Box>
                ))}
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>

      {/* Floating Glitter Particles */}
      {[...Array(15)].map((_, i) => (
        <div
          key={i}
          className="glitter-particle"
          style={{
            left: `${Math.random() * 100}%`,
            top: `${Math.random() * 100}%`,
            animationDelay: `${Math.random() * 6}s`,
          }}
        />
      ))}
    </Box>
  );
};

export default Dashboard;