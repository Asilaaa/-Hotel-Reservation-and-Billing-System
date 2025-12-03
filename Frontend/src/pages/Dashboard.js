import React, { useState, useEffect } from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  LinearProgress,
  Chip,
  IconButton,
  Button,
} from '@mui/material';
import {
  TrendingUp as TrendingUpIcon,
  People as PeopleIcon,
  Hotel as HotelIcon, // Changed from ShoppingCartIcon
  AttachMoney as MoneyIcon,
  Favorite as FavoriteIcon,
  Star as StarIcon,
  CalendarToday as CalendarIcon,
  Room as RoomIcon,
} from '@mui/icons-material';
import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

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
  const [hotelStats, setHotelStats] = useState({
    totalReservations: 0,
    activeReservations: 0,
    availableRooms: 0,
    occupancyRate: '0%',
    totalRevenue: '$0'
  });

  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchHotelStats();
  }, []);

  const fetchHotelStats = async () => {
    try {
      setLoading(true);
      
      // Fetch reservations
      const reservationsRes = await axios.get(`${API_BASE_URL}/reservations/guest/1`);
      const reservations = reservationsRes.data;
      
      // Fetch room status
      const roomsRes = await axios.get(`${API_BASE_URL}/reservations/rooms/status`);
      const rooms = roomsRes.data;
      
      const totalRooms = rooms.length;
      const availableRooms = rooms.filter(r => r.status === 'AVAILABLE').length;
      const occupiedRooms = rooms.filter(r => r.status === 'OCCUPIED').length;
      const occupancyRate = totalRooms > 0 ? Math.round((occupiedRooms / totalRooms) * 100) : 0;
      
      const totalReservations = reservations.length;
      const activeReservations = reservations.filter(r => 
        r.status === 'BOOKED' || r.status === 'CHECKED_IN'
      ).length;
      
      // Calculate revenue (mock calculation)
      let totalRevenue = 0;
      reservations.forEach(res => {
        if (res.status !== 'CANCELLED') {
          const days = Math.ceil(
            (new Date(res.checkOutDate) - new Date(res.checkInDate)) / (1000 * 60 * 60 * 24)
          );
          res.rooms.forEach(room => {
            totalRevenue += days * (room.ratePerNight || room.roomType?.baseRate || 100);
          });
        }
      });
      
      setHotelStats({
        totalReservations,
        activeReservations,
        availableRooms,
        occupancyRate: `${occupancyRate}%`,
        totalRevenue: `$${totalRevenue.toLocaleString()}`
      });
      
    } catch (error) {
      console.error('Error fetching hotel stats:', error);
      // Use mock data if API fails
      setHotelStats({
        totalReservations: 15,
        activeReservations: 8,
        availableRooms: 12,
        occupancyRate: '65%',
        totalRevenue: '$24,850'
      });
    } finally {
      setLoading(false);
    }
  };

  const stats = [
    {
      title: 'TOTAL REVENUE',
      value: hotelStats.totalRevenue,
      change: '+12.5%',
      icon: <MoneyIcon sx={{ color: '#a5b4fc', fontSize: 32 }} />,
      color: '#a5b4fc',
      isFavorite: true
    },
    {
      title: 'ACTIVE RESERVATIONS',
      value: hotelStats.activeReservations,
      change: '+8.2%',
      icon: <CalendarIcon sx={{ color: '#d8b4fe', fontSize: 32 }} />,
      color: '#d8b4fe'
    },
    {
      title: 'AVAILABLE ROOMS',
      value: hotelStats.availableRooms,
      change: hotelStats.occupancyRate,
      icon: <RoomIcon sx={{ color: '#f9a8d4', fontSize: 32 }} />,
      color: '#f9a8d4'
    },
    {
      title: 'OCCUPANCY RATE',
      value: hotelStats.occupancyRate,
      change: '+5.7%',
      icon: <TrendingUpIcon sx={{ color: '#c7d2fe', fontSize: 32 }} />,
      color: '#c7d2fe'
    },
  ];

  const quickActions = [
    { 
      text: '🏨 Create Reservation', 
      icon: '📅',
      link: '/reservations',
      action: () => window.location.href = '/reservations'
    },
    { 
      text: '📊 View Room Status', 
      icon: '🛏️',
      link: '/reservations#room-status'
    },
    { 
      text: '👥 Guest Management', 
      icon: '👑',
      link: '/reservations#guests'
    },
    { 
      text: '📈 Hotel Analytics', 
      icon: '💫',
      link: '/analytics'
    },
  ];

  const recentBookings = [
    { guest: 'John Doe', room: 'Deluxe Suite', checkIn: 'Today', status: 'Checked In' },
    { guest: 'Jane Smith', room: 'Standard Room', checkIn: 'Tomorrow', status: 'Booked' },
    { guest: 'Robert Johnson', room: 'Executive Suite', checkIn: 'Dec 5', status: 'Confirmed' },
    { guest: 'Sarah Williams', room: 'Family Room', checkIn: 'Dec 7', status: 'Pending' },
  ];

  return (
    <Box>
      <Box sx={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', mb: 3 }}>
        <Box sx={{ display: 'flex', alignItems: 'center' }}>
          <Typography variant="h3" gutterBottom fontWeight="bold" className="gradient-text">
            Hotel Dashboard 👑
          </Typography>
          <IconButton className="heartbeat" sx={{ ml: 2, color: '#f9a8d4' }}>
            <FavoriteIcon />
          </IconButton>
        </Box>
        <Button
          variant="contained"
          startIcon={<HotelIcon />}
          href="/reservations"
          sx={{
            background: 'linear-gradient(135deg, #d8b4fe 0%, #a5b4fc 100%)',
            '&:hover': {
              background: 'linear-gradient(135deg, #c084fc 0%, #818cf8 100%)',
            }
          }}
        >
          Manage Reservations
        </Button>
      </Box>
      
      <Grid container spacing={3} sx={{ mt: 2 }}>
        {stats.map((stat, index) => (
          <Grid item xs={12} sm={6} md={3} key={index}>
            <StatCard {...stat} />
          </Grid>
        ))}

        {/* Recent Bookings */}
        <Grid item xs={12} md={8}>
          <Card className="glass" sx={{ height: 400, position: 'relative' }}>
            <Box className="shimmer"></Box>
            <CardContent>
              <Typography variant="h6" gutterBottom sx={{ display: 'flex', alignItems: 'center' }}>
                ✨ Recent Bookings
                <StarIcon sx={{ color: '#f9a8d4', ml: 1, fontSize: 20 }} />
              </Typography>
              <Box sx={{ mt: 3 }}>
                {recentBookings.map((booking, index) => (
                  <Box key={booking.guest} sx={{ mb: 3 }}>
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 1 }}>
                      <Box>
                        <Typography variant="body1" fontWeight="bold">{booking.guest}</Typography>
                        <Typography variant="body2" color="text.secondary">{booking.room} • Check-in: {booking.checkIn}</Typography>
                      </Box>
                      <Chip
                        label={booking.status}
                        size="small"
                        sx={{
                          background: booking.status === 'Checked In' 
                            ? 'linear-gradient(135deg, rgba(165, 180, 252, 0.3), rgba(165, 180, 252, 0.1))'
                            : booking.status === 'Booked'
                            ? 'linear-gradient(135deg, rgba(216, 180, 254, 0.3), rgba(216, 180, 254, 0.1))'
                            : 'linear-gradient(135deg, rgba(249, 168, 212, 0.3), rgba(249, 168, 212, 0.1))',
                          color: booking.status === 'Checked In' ? '#a5b4fc' : 
                                 booking.status === 'Booked' ? '#d8b4fe' : '#f9a8d4',
                          fontWeight: 'bold',
                        }}
                      />
                    </Box>
                    <LinearProgress 
                      variant="determinate" 
                      value={[100, 75, 50, 25][index]} 
                      sx={{ 
                        height: 6, 
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
                    component={action.link ? 'a' : 'div'}
                    href={action.link}
                    onClick={action.action}
                    sx={{
                      p: 2,
                      mb: 1,
                      background: 'rgba(255, 255, 255, 0.05)',
                      borderRadius: 3,
                      cursor: 'pointer',
                      transition: 'all 0.3s ease',
                      display: 'flex',
                      alignItems: 'center',
                      textDecoration: 'none',
                      color: 'inherit',
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
              
              {/* Hotel Welcome Message */}
              <Box sx={{ mt: 3, p: 2, borderRadius: 3, background: 'rgba(216, 180, 254, 0.1)' }}>
                <Typography variant="body2" sx={{ display: 'flex', alignItems: 'center' }}>
                  <HotelIcon sx={{ mr: 1, fontSize: 20, color: '#d8b4fe' }} />
                  Welcome to Hotel Management System
                </Typography>
                <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mt: 1 }}>
                  Manage reservations, rooms, and guests with ease
                </Typography>
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