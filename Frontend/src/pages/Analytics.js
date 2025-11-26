import React, { useState, useEffect } from 'react';
import { 
  Card, 
  CardContent, 
  Typography, 
  Box, 
  Grid,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Paper,
  Alert,
  CircularProgress,
  Tabs,
  Tab,
  Chip,
  Button // Make sure this line is present and correct
} from '@mui/material';
import { 
  LineChart, 
  Line, 
  XAxis, 
  YAxis, 
  CartesianGrid, 
  Tooltip, 
  Legend, 
  ResponsiveContainer,
  BarChart,
  Bar,
  PieChart,
  Pie,
  Cell,
  AreaChart,
  Area
} from 'recharts';

const Analytics = () => {
  const [period, setPeriod] = useState('monthly');
  const [selectedHotel, setSelectedHotel] = useState('all');
  const [tabValue, setTabValue] = useState(0);
  const [revenueData, setRevenueData] = useState([]);
  const [roomTypeData, setRoomTypeData] = useState([]);
  const [serviceRevenue, setServiceRevenue] = useState([]);
  const [hotelRevenue, setHotelRevenue] = useState([]);
  const [keyMetrics, setKeyMetrics] = useState({});
  const [occupancyData, setOccupancyData] = useState({});
  const [reservationStats, setReservationStats] = useState({});
  const [guestStats, setGuestStats] = useState({});
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  // Colors for charts
  const COLORS = ['#0088FE', '#00C49F', '#FFBB28', '#FF8042', '#8884D8', '#FF6B6B', '#82CA9D', '#FFC658'];

  useEffect(() => {
    fetchAllAnalyticsData();
  }, [period, selectedHotel]);

  const fetchAllAnalyticsData = async () => {
    try {
      setLoading(true);
      setError(null);
      
      // Fetch hotels first, then other data
      const hotelsResponse = await fetch('http://localhost:8080/api/hotels');
      if (hotelsResponse.ok) {
        const hotelsData = await hotelsResponse.json();
        setHotels(hotelsData);
      } else {
        throw new Error('Failed to fetch hotels');
      }

      // Fetch all analytics data in parallel
      await Promise.all([
        fetchRevenueTrend(),
        fetchRoomTypeDistribution(),
        fetchServiceRevenue(),
        fetchHotelRevenue(),
        fetchKeyMetrics(),
        fetchOccupancyRate(),
        fetchReservationStats(),
        fetchGuestStats()
      ]);
      
    } catch (error) {
      console.error('Error fetching analytics data:', error);
      setError('Failed to connect to backend server. Make sure the server is running on port 8080.');
    } finally {
      setLoading(false);
    }
  };

  // 1. Revenue Trend - Fixed date range to include all years
  const fetchRevenueTrend = async () => {
    try {
      const url = selectedHotel === 'all' 
        ? `http://localhost:8080/api/revenue/trend?startDate=2022-01-01&endDate=2025-12-31&period=${period}`
        : `http://localhost:8080/api/revenue/trend?startDate=2022-01-01&endDate=2025-12-31&period=${period}&hotelId=${selectedHotel}`;
      
      const response = await fetch(url);
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const data = await response.json();
      setRevenueData(data);
    } catch (error) {
      console.error('Revenue API error:', error);
      setRevenueData([]);
    }
  };

  // 2. Room Type Distribution
  const fetchRoomTypeDistribution = async () => {
    try {
      const url = selectedHotel === 'all'
        ? 'http://localhost:8080/api/analytics/room-types'
        : `http://localhost:8080/api/analytics/room-types?hotelId=${selectedHotel}`;
      
      const response = await fetch(url);
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const data = await response.json();
      setRoomTypeData(data);
    } catch (error) {
      console.error('Room Types API error:', error);
      setRoomTypeData([]);
    }
  };

  // 3. Revenue by Service Type
  const fetchServiceRevenue = async () => {
    try {
      const url = selectedHotel === 'all'
        ? 'http://localhost:8080/api/analytics/service-revenue'
        : `http://localhost:8080/api/analytics/service-revenue?hotelId=${selectedHotel}`;
      
      const response = await fetch(url);
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const data = await response.json();
      setServiceRevenue(data);
    } catch (error) {
      console.error('Service Revenue API error:', error);
      setServiceRevenue([]);
    }
  };

  // 4. Revenue by Hotel
  const fetchHotelRevenue = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/analytics/hotel-revenue');
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const data = await response.json();
      setHotelRevenue(data);
    } catch (error) {
      console.error('Hotel Revenue API error:', error);
      setHotelRevenue([]);
    }
  };

  // 5. Key Metrics
  const fetchKeyMetrics = async () => {
    try {
      const url = selectedHotel === 'all'
        ? 'http://localhost:8080/api/analytics/key-metrics'
        : `http://localhost:8080/api/analytics/key-metrics?hotelId=${selectedHotel}`;
      
      const response = await fetch(url);
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const data = await response.json();
      setKeyMetrics(data);
    } catch (error) {
      console.error('Key Metrics API error:', error);
      setKeyMetrics({});
    }
  };

  // 6. Occupancy Rate
  const fetchOccupancyRate = async () => {
    try {
      const url = selectedHotel === 'all'
        ? 'http://localhost:8080/api/analytics/occupancy-rate'
        : `http://localhost:8080/api/analytics/occupancy-rate?hotelId=${selectedHotel}`;
      
      const response = await fetch(url);
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const data = await response.json();
      setOccupancyData(data);
    } catch (error) {
      console.error('Occupancy API error:', error);
      setOccupancyData({});
    }
  };

  // 7. Reservation Statistics
  const fetchReservationStats = async () => {
    try {
      const url = selectedHotel === 'all'
        ? 'http://localhost:8080/api/analytics/reservation-stats'
        : `http://localhost:8080/api/analytics/reservation-stats?hotelId=${selectedHotel}`;
      
      const response = await fetch(url);
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const data = await response.json();
      setReservationStats(data);
    } catch (error) {
      console.error('Reservation Stats API error:', error);
      setReservationStats({});
    }
  };

  // 8. Guest Statistics
  const fetchGuestStats = async () => {
    try {
      const url = selectedHotel === 'all'
        ? 'http://localhost:8080/api/analytics/guest-stats'
        : `http://localhost:8080/api/analytics/guest-stats?hotelId=${selectedHotel}`;
      
      const response = await fetch(url);
      if (!response.ok) throw new Error(`HTTP ${response.status}`);
      const data = await response.json();
      setGuestStats(data);
    } catch (error) {
      console.error('Guest Stats API error:', error);
      setGuestStats({});
    }
  };

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleHotelChange = (event) => {
    setSelectedHotel(event.target.value);
  };

  const handlePeriodChange = (event) => {
    setPeriod(event.target.value);
  };

  // Format currency
  const formatCurrency = (value) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value);
  };

  // Custom tooltip formatter
  const CustomTooltip = ({ active, payload, label }) => {
    if (active && payload && payload.length) {
      return (
        <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.95)' }}>
          <Typography variant="body2" fontWeight="bold">{label}</Typography>
          {payload.map((entry, index) => (
            <Typography key={index} variant="body2" style={{ color: entry.color }}>
              {entry.name}: {formatCurrency(entry.value)}
            </Typography>
          ))}
        </Paper>
      );
    }
    return null;
  };

  if (loading) {
    return (
      <Box sx={{ p: 3, textAlign: 'center' }}>
        <Typography variant="h3" gutterBottom fontWeight="bold" className="gradient-text">
          Hotel Analytics Dashboard
        </Typography>
        <CircularProgress sx={{ mt: 2 }} />
        <Typography sx={{ mt: 2 }}>Loading analytics data...</Typography>
      </Box>
    );
  }

  if (error) {
    return (
      <Box sx={{ p: 3 }}>
        <Typography variant="h3" gutterBottom fontWeight="bold" className="gradient-text">
          Hotel Analytics Dashboard
        </Typography>
        <Alert severity="error" sx={{ mb: 2 }}>
          {error}
        </Alert>
        <Button variant="contained" onClick={fetchAllAnalyticsData}>
          Retry
        </Button>
      </Box>
    );
  }

  const selectedHotelName = selectedHotel !== 'all' 
    ? hotels.find(h => h.hotelId == selectedHotel)?.name 
    : 'All Hotels';

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h3" gutterBottom fontWeight="bold" className="gradient-text">
        Hotel Analytics Dashboard
        {selectedHotel !== 'all' && ` - ${selectedHotelName}`}
      </Typography>

      {/* Hotel Selection Chip */}
      <Box sx={{ mb: 2 }}>
        <Chip 
          label={`Viewing: ${selectedHotelName}`}
          color="primary"
          variant="outlined"
          sx={{ mb: 2 }}
        />
      </Box>

      {/* Filters */}
      <Card className="glass" sx={{ mb: 3 }}>
        <CardContent>
          <Grid container spacing={3} alignItems="center">
            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel>Select Hotel</InputLabel>
                <Select
                  value={selectedHotel}
                  label="Select Hotel"
                  onChange={handleHotelChange}
                >
                  <MenuItem value="all">All Hotels</MenuItem>
                  {hotels.map(hotel => (
                    <MenuItem key={hotel.hotelId} value={hotel.hotelId}>
                      {hotel.name}
                    </MenuItem>
                  ))}
                </Select>
              </FormControl>
            </Grid>
            <Grid item xs={12} md={6}>
              <FormControl fullWidth>
                <InputLabel>View Period</InputLabel>
                <Select
                  value={period}
                  label="View Period"
                  onChange={handlePeriodChange}
                >
                  <MenuItem value="daily">Daily</MenuItem>
                  <MenuItem value="monthly">Monthly</MenuItem>
                  <MenuItem value="yearly">Yearly</MenuItem>
                </Select>
              </FormControl>
            </Grid>
          </Grid>
        </CardContent>
      </Card>

      {/* Tabs for different analytics views */}
      <Card className="glass" sx={{ mb: 3 }}>
        <Tabs value={tabValue} onChange={handleTabChange} centered>
          <Tab label="Overview" />
          <Tab label="Revenue Analytics" />
          <Tab label="Occupancy & Rooms" />
          <Tab label="Guest Analytics" />
        </Tabs>
      </Card>

      {/* Tab 1: Overview */}
      {tabValue === 0 && (
        <Grid container spacing={3}>
          {/* Key Metrics */}
          <Grid item xs={12}>
            <Card className="glass">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Key Performance Indicators - {selectedHotelName}
                </Typography>
                <Grid container spacing={2}>
                  <Grid item xs={12} sm={6} md={3}>
                    <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.1)', textAlign: 'center' }}>
                      <Typography variant="h4" color="#8884d8" fontWeight="bold">
                        {formatCurrency(keyMetrics.totalRevenue || 0)}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">Total Revenue</Typography>
                    </Paper>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.1)', textAlign: 'center' }}>
                      <Typography variant="h4" color="#82ca9d" fontWeight="bold">
                        {keyMetrics.totalGuests || 0}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">Total Guests</Typography>
                    </Paper>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.1)', textAlign: 'center' }}>
                      <Typography variant="h4" color="#ffc658" fontWeight="bold">
                        {keyMetrics.occupancyRate || 0}%
                      </Typography>
                      <Typography variant="body2" color="text.secondary">Occupancy Rate</Typography>
                    </Paper>
                  </Grid>
                  <Grid item xs={12} sm={6} md={3}>
                    <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.1)', textAlign: 'center' }}>
                      <Typography variant="h4" color="#ff8042" fontWeight="bold">
                        {formatCurrency(keyMetrics.averageDailyRate || 0)}
                      </Typography>
                      <Typography variant="body2" color="text.secondary">Average Daily Rate</Typography>
                    </Paper>
                  </Grid>
                </Grid>
              </CardContent>
            </Card>
          </Grid>

          {/* Revenue Trend & Room Distribution */}
          <Grid item xs={12} md={8}>
            <Card className="glass">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Revenue Trend ({period}) - {selectedHotelName}
                </Typography>
                {revenueData.length > 0 ? (
                  <ResponsiveContainer width="100%" height={300}>
                    <LineChart data={revenueData}>
                      <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.2)" />
                      <XAxis 
                        dataKey="label" 
                        stroke="rgba(255,255,255,0.7)"
                        fontSize={12}
                      />
                      <YAxis 
                        stroke="rgba(255,255,255,0.7)"
                        fontSize={12}
                        tickFormatter={(value) => `$${value}`}
                      />
                      <Tooltip content={<CustomTooltip />} />
                      <Legend />
                      <Line 
                        type="monotone" 
                        dataKey="amount" 
                        stroke="#8884d8" 
                        strokeWidth={3}
                        dot={{ fill: '#8884d8', strokeWidth: 2, r: 4 }}
                        activeDot={{ r: 6, stroke: '#8884d8', strokeWidth: 2 }}
                      />
                    </LineChart>
                  </ResponsiveContainer>
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography color="text.secondary">No revenue data available for the selected period</Typography>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={4}>
            <Card className="glass">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Room Type Distribution - {selectedHotelName}
                </Typography>
                {roomTypeData.length > 0 ? (
                  <ResponsiveContainer width="100%" height={300}>
                    <PieChart>
                      <Pie
                        data={roomTypeData}
                        cx="50%"
                        cy="50%"
                        labelLine={false}
                        label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                        outerRadius={80}
                        fill="#8884d8"
                        dataKey="value"
                      >
                        {roomTypeData.map((entry, index) => (
                          <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
                        ))}
                      </Pie>
                      <Tooltip formatter={(value) => [value, 'Rooms']} />
                    </PieChart>
                  </ResponsiveContainer>
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography color="text.secondary">No room data available</Typography>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* Tab 2: Revenue Analytics */}
      {tabValue === 1 && (
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Card className="glass">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Revenue by Service Type - {selectedHotelName}
                </Typography>
                {serviceRevenue.length > 0 ? (
                  <ResponsiveContainer width="100%" height={300}>
                    <BarChart data={serviceRevenue}>
                      <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.2)" />
                      <XAxis 
                        dataKey="service" 
                        stroke="rgba(255,255,255,0.7)"
                        fontSize={12}
                      />
                      <YAxis 
                        stroke="rgba(255,255,255,0.7)"
                        fontSize={12}
                        tickFormatter={(value) => `$${value}`}
                      />
                      <Tooltip formatter={(value) => [formatCurrency(value), 'Revenue']} />
                      <Bar dataKey="revenue" fill="#82ca9d" radius={[4, 4, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography color="text.secondary">No service revenue data available</Typography>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={6}>
            <Card className="glass">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Revenue by Hotel
                </Typography>
                {hotelRevenue.length > 0 ? (
                  <ResponsiveContainer width="100%" height={300}>
                    <BarChart data={hotelRevenue}>
                      <CartesianGrid strokeDasharray="3 3" stroke="rgba(255,255,255,0.2)" />
                      <XAxis 
                        dataKey="hotel" 
                        stroke="rgba(255,255,255,0.7)"
                        fontSize={12}
                        angle={-45}
                        textAnchor="end"
                        height={80}
                      />
                      <YAxis 
                        stroke="rgba(255,255,255,0.7)"
                        fontSize={12}
                        tickFormatter={(value) => `$${value}`}
                      />
                      <Tooltip formatter={(value) => [formatCurrency(value), 'Revenue']} />
                      <Bar dataKey="revenue" fill="#ffc658" radius={[4, 4, 0, 0]} />
                    </BarChart>
                  </ResponsiveContainer>
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography color="text.secondary">No hotel revenue data available</Typography>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* Tab 3: Occupancy & Rooms */}
      {tabValue === 2 && (
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Card className="glass">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Current Occupancy - {selectedHotelName}
                </Typography>
                <Box sx={{ textAlign: 'center', py: 4 }}>
                  <Typography variant="h2" color="#8884d8" fontWeight="bold">
                    {occupancyData.currentOccupancy || 0}%
                  </Typography>
                  <Typography variant="body1" color="text.secondary">
                    Current Occupancy Rate
                  </Typography>
                  <Box sx={{ mt: 3 }}>
                    <Grid container spacing={2}>
                      <Grid item xs={6}>
                        <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.1)' }}>
                          <Typography variant="h6" color="#00C49F">
                            {occupancyData.availableRooms || 0}
                          </Typography>
                          <Typography variant="body2">Available Rooms</Typography>
                        </Paper>
                      </Grid>
                      <Grid item xs={6}>
                        <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.1)' }}>
                          <Typography variant="h6" color="#FF8042">
                            {occupancyData.occupiedRooms || 0}
                          </Typography>
                          <Typography variant="body2">Occupied Rooms</Typography>
                        </Paper>
                      </Grid>
                    </Grid>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={6}>
            <Card className="glass">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Room Status Distribution - {selectedHotelName}
                </Typography>
                {keyMetrics.totalRooms > 0 ? (
                  <ResponsiveContainer width="100%" height={300}>
                    <PieChart>
                      <Pie
                        data={[
                          { name: 'Available', value: keyMetrics.availableRooms || 0, color: '#00C49F' },
                          { name: 'Occupied', value: keyMetrics.occupiedRooms || 0, color: '#FF8042' },
                          { name: 'Maintenance', value: keyMetrics.maintenanceRooms || 0, color: '#FFBB28' }
                        ]}
                        cx="50%"
                        cy="50%"
                        labelLine={false}
                        label={({ name, percent }) => `${name}: ${(percent * 100).toFixed(0)}%`}
                        outerRadius={80}
                        dataKey="value"
                      >
                        <Cell fill="#00C49F" />
                        <Cell fill="#FF8042" />
                        <Cell fill="#FFBB28" />
                      </Pie>
                      <Tooltip />
                      <Legend />
                    </PieChart>
                  </ResponsiveContainer>
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography color="text.secondary">No room status data available</Typography>
                  </Box>
                )}
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}

      {/* Tab 4: Guest Analytics */}
      {tabValue === 3 && (
        <Grid container spacing={3}>
          <Grid item xs={12} md={6}>
            <Card className="glass">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Reservation Statistics - {selectedHotelName}
                </Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                  <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.1)', textAlign: 'center' }}>
                    <Typography variant="h4" fontWeight="bold">
                      {reservationStats.totalReservations || 0}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">Total Reservations</Typography>
                  </Paper>
                  <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.1)', textAlign: 'center' }}>
                    <Typography variant="h4" color="#00C49F" fontWeight="bold">
                      {reservationStats.completedStays || 0}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">Completed Stays</Typography>
                  </Paper>
                  <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.1)', textAlign: 'center' }}>
                    <Typography variant="h4" color="#FF8042" fontWeight="bold">
                      {reservationStats.cancellations || 0}
                    </Typography>
                    <Typography variant="body2" color="text.secondary">Cancellations</Typography>
                  </Paper>
                  <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.1)', textAlign: 'center' }}>
                    <Typography variant="h4" color="#FFBB28" fontWeight="bold">
                      {reservationStats.cancellationRate || 0}%
                    </Typography>
                    <Typography variant="body2" color="text.secondary">Cancellation Rate</Typography>
                  </Paper>
                </Box>
              </CardContent>
            </Card>
          </Grid>

          <Grid item xs={12} md={6}>
            <Card className="glass">
              <CardContent>
                <Typography variant="h6" gutterBottom>
                  Guest Analytics - {selectedHotelName}
                </Typography>
                <Box sx={{ textAlign: 'center', py: 4 }}>
                  <Typography variant="h3" color="#8884d8" fontWeight="bold" gutterBottom>
                    {guestStats.averageLoyaltyPoints || keyMetrics.averageLoyaltyPoints || 0}
                  </Typography>
                  <Typography variant="body2" color="text.secondary" gutterBottom>
                    Average Loyalty Points
                  </Typography>
                  
                  <Box sx={{ mt: 4 }}>
                    <Typography variant="h5" color="#00C49F" fontWeight="bold">
                      {guestStats.vipGuests || keyMetrics.vipGuests || 0} VIP Guests
                    </Typography>
                    <Typography variant="body2" color="text.secondary">
                      Out of {guestStats.totalGuests || keyMetrics.totalGuests || 0} total guests
                    </Typography>
                  </Box>

                  <Box sx={{ mt: 3, p: 2, background: 'rgba(255,255,255,0.1)', borderRadius: 2 }}>
                    <Typography variant="body1" fontWeight="bold">
                      Guest Distribution
                    </Typography>
                    <Box sx={{ mt: 1 }}>
                      <Typography variant="body2">
                        Regular Guests: {(guestStats.totalGuests || keyMetrics.totalGuests || 0) - (guestStats.vipGuests || keyMetrics.vipGuests || 0)}
                      </Typography>
                      <Typography variant="body2" color="#00C49F">
                        VIP Guests: {guestStats.vipGuests || keyMetrics.vipGuests || 0}
                      </Typography>
                    </Box>
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        </Grid>
      )}
    </Box>
  );
};

export default Analytics;