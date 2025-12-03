import React from 'react';
import {
  Grid,
  Card,
  CardContent,
  Typography,
  Box,
  Stack,
  LinearProgress
} from '@mui/material';
import {
  AttachMoney,
  Hotel,
  RoomService,
  TrendingUp,
  CalendarMonth,
  BarChart
} from '@mui/icons-material';

const RevenueSummaryCards = ({ data, type, isAllHotels = false }) => {
  if (!data || data.length === 0) {
    return (
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12}>
          <Typography color="text.secondary" align="center">
            No revenue data available
          </Typography>
        </Grid>
      </Grid>
    );
  }

  // Calculate summary metrics
  const calculateSummary = () => {
    if (type === 'yearly') {
      const totalRoomRevenue = data.reduce((sum, item) => sum + (item.roomRevenue || 0), 0);
      const totalServiceRevenue = data.reduce((sum, item) => sum + (item.serviceRevenue || 0), 0);
      const totalRevenue = data.reduce((sum, item) => sum + (item.totalRevenue || 0), 0);
      const avgRoomRate = data.reduce((sum, item) => sum + (item.avgRoomRate || 0), 0) / data.length;
      const avgDailyRevenue = data.reduce((sum, item) => sum + (item.avgDailyRevenue || 0), 0) / data.length;
      const totalStays = data.reduce((sum, item) => sum + (item.totalStays || 0), 0);
      
      return {
        totalRoomRevenue,
        totalServiceRevenue,
        totalRevenue,
        avgRoomRate,
        avgDailyRevenue,
        totalStays
      };
    } else {
      const currentData = data;
      const totalRoomRevenue = currentData.reduce((sum, item) => sum + (item.roomRevenue || 0), 0);
      const totalServiceRevenue = currentData.reduce((sum, item) => sum + (item.serviceRevenue || 0), 0);
      const totalRevenue = currentData.reduce((sum, item) => sum + (item.totalRevenue || 0), 0);
      const avgRoomRate = currentData.reduce((sum, item) => sum + (item.avgRoomRate || 0), 0) / currentData.length;
      const avgDailyRevenue = currentData.reduce((sum, item) => sum + (item.avgDailyRevenue || 0), 0) / currentData.length;
      const totalStays = currentData.reduce((sum, item) => sum + (item.totalStays || 0), 0);
      
      return {
        totalRoomRevenue,
        totalServiceRevenue,
        totalRevenue,
        avgRoomRate,
        avgDailyRevenue,
        totalStays
      };
    }
  };

  const summary = calculateSummary();
  const servicePercentage = summary.totalRevenue > 0 
    ? (summary.totalServiceRevenue / summary.totalRevenue * 100) 
    : 0;

  const formatCurrency = (value) => {
    if (isAllHotels && value >= 1000000) {
      return `$${(value / 1000000).toFixed(1)}M`;
    } else if (isAllHotels && value >= 1000) {
      return `$${(value / 1000).toFixed(0)}K`;
    } else {
      return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: 'USD',
        minimumFractionDigits: 0,
        maximumFractionDigits: 0
      }).format(value);
    }
  };

  const StatCard = ({ title, value, icon, color, subtitle }) => (
    <Card>
      <CardContent>
        <Stack direction="row" alignItems="center" spacing={2} sx={{ mb: 1 }}>
          <Box
            sx={{
              backgroundColor: `${color}.light`,
              color: `${color}.main`,
              borderRadius: 1,
              p: 1,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            {icon}
          </Box>
          <Box>
            <Typography variant="body2" color="text.secondary">
              {title}
            </Typography>
            <Typography variant="h5" fontWeight="bold">
              {formatCurrency(value)}
            </Typography>
            {subtitle && (
              <Typography variant="caption" color="text.secondary">
                {subtitle}
              </Typography>
            )}
          </Box>
        </Stack>
      </CardContent>
    </Card>
  );

  const formatSmallCurrency = (value) => {
    if (value >= 1000) {
      return `$${(value / 1000).toFixed(0)}K`;
    }
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0
    }).format(value);
  };

  return (
    <Grid container spacing={3} sx={{ mb: 3 }}>
      <Grid item xs={12} md={6} lg={3}>
        <StatCard
          title="Total Revenue"
          value={summary.totalRevenue}
          icon={<AttachMoney />}
          color="primary"
          subtitle={type === 'yearly' ? 'All Years' : 'This Year'}
        />
      </Grid>
      
      <Grid item xs={12} md={6} lg={3}>
        <StatCard
          title="Room Revenue"
          value={summary.totalRoomRevenue}
          icon={<Hotel />}
          color="success"
          subtitle={`${(summary.totalRoomRevenue / summary.totalRevenue * 100 || 0).toFixed(1)}% of total`}
        />
      </Grid>
      
      <Grid item xs={12} md={6} lg={3}>
        <StatCard
          title="Service Revenue"
          value={summary.totalServiceRevenue}
          icon={<RoomService />}
          color="warning"
          subtitle={`${servicePercentage.toFixed(1)}% of total`}
        />
      </Grid>
      
      <Grid item xs={12} md={6} lg={3}>
        <StatCard
          title="Avg Room Rate"
          value={summary.avgRoomRate}
          icon={<BarChart />}
          color="info"
          subtitle={`${summary.totalStays} stays`}
        />
      </Grid>
      
      <Grid item xs={12}>
        <Card>
          <CardContent>
            <Typography variant="subtitle2" gutterBottom>
              Revenue Composition
            </Typography>
            <Box sx={{ mt: 2 }}>
              <Stack direction="row" justifyContent="space-between" sx={{ mb: 1 }}>
                <Typography variant="caption" color="success.main">
                  Room Revenue: {formatSmallCurrency(summary.totalRoomRevenue)}
                </Typography>
                <Typography variant="caption" color="warning.main">
                  Service Revenue: {formatSmallCurrency(summary.totalServiceRevenue)}
                </Typography>
              </Stack>
              <LinearProgress
                variant="determinate"
                value={servicePercentage}
                sx={{
                  height: 8,
                  borderRadius: 4,
                  backgroundColor: 'success.light',
                  '& .MuiLinearProgress-bar': {
                    backgroundColor: 'warning.main',
                    borderRadius: 4
                  }
                }}
              />
              <Stack direction="row" justifyContent="space-between" sx={{ mt: 1 }}>
                <Typography variant="caption" color="text.secondary">
                  {(100 - servicePercentage).toFixed(1)}% Rooms
                </Typography>
                <Typography variant="caption" color="text.secondary">
                  {servicePercentage.toFixed(1)}% Services
                </Typography>
              </Stack>
            </Box>
          </CardContent>
        </Card>
      </Grid>
    </Grid>
  );
};

export default RevenueSummaryCards;