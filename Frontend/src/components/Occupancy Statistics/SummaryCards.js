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
  Hotel,
  Bed,
  People,
  TrendingUp,
  CalendarMonth,
  Star,
  Percent,
  InsertChart,
  EmojiEvents,
  Timeline
} from '@mui/icons-material';

const SummaryCards = ({ data, type, isAllHotels = false }) => {
  if (!data || data.length === 0) {
    return (
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12}>
          <Typography color="text.secondary" align="center">
            No occupancy data available
          </Typography>
        </Grid>
      </Grid>
    );
  }

  // Calculate summary metrics
  const calculateSummary = () => {
    if (type === 'yearly') {
      const averageOccupancy = data.reduce((sum, item) => sum + (item.occupancyRate || 0), 0) / data.length;
      const peakOccupancy = Math.max(...data.map(item => item.occupancyRate || 0));
      const lowestOccupancy = Math.min(...data.map(item => item.occupancyRate || 0));
      const totalOccupiedRooms = data.reduce((sum, item) => sum + (item.occupiedRooms || 0), 0);
      const totalRooms = data.reduce((sum, item) => sum + (item.totalRooms || 0), 0);
      const occupancyGrowth = data.length > 1 ? 
        ((data[data.length - 1].occupancyRate - data[0].occupancyRate) / data[0].occupancyRate * 100) : 0;
      
      // Find best year
      const bestYear = data.find(item => item.occupancyRate === peakOccupancy)?.year || 'N/A';
      
      return {
        averageOccupancy,
        peakOccupancy,
        lowestOccupancy,
        totalOccupiedRooms,
        totalRooms,
        occupancyGrowth,
        yearsTracked: data.length,
        bestYear
      };
    } else {
      const averageMonthlyOccupancy = data.reduce((sum, item) => sum + (item.occupancyRate || 0), 0) / data.length;
      const bestMonthOccupancy = Math.max(...data.map(item => item.occupancyRate || 0));
      const worstMonthOccupancy = Math.min(...data.map(item => item.occupancyRate || 0));
      const totalMonthlyOccupied = data.reduce((sum, item) => sum + (item.occupiedRooms || 0), 0);
      const totalMonthlyRooms = data.reduce((sum, item) => sum + (item.totalRooms || 0), 0);
      const peakOccupancyMonth = data.find(item => item.occupancyRate === bestMonthOccupancy)?.month || 'Unknown';
      const worstOccupancyMonth = data.find(item => item.occupancyRate === worstMonthOccupancy)?.month || 'Unknown';
      
      return {
        averageOccupancy: averageMonthlyOccupancy,
        peakOccupancy: bestMonthOccupancy,
        lowestOccupancy: worstMonthOccupancy,
        totalOccupiedRooms: totalMonthlyOccupied,
        totalRooms: totalMonthlyRooms,
        peakMonth: peakOccupancyMonth,
        worstMonth: worstOccupancyMonth,
        monthsTracked: data.length
      };
    }
  };

  const summary = calculateSummary();
  const overallOccupancyRate = summary.totalRooms > 0 ? 
    (summary.totalOccupiedRooms / summary.totalRooms * 100) : 0;

  const getPerformanceColor = (rate) => {
    if (rate >= 80) return 'success';
    if (rate >= 60) return 'info';
    if (rate >= 40) return 'warning';
    return 'error';
  };

  const getPerformanceLabel = (rate) => {
    if (rate >= 80) return 'Excellent';
    if (rate >= 60) return 'Good';
    if (rate >= 40) return 'Fair';
    return 'Poor';
  };

  const formatPercentage = (value) => {
    return `${value.toFixed(1)}%`;
  };

  const formatNumber = (value) => {
    if (isAllHotels && value >= 1000) {
      return `${(value / 1000).toFixed(1)}K`;
    }
    return value.toLocaleString();
  };

  const StatCard = ({ title, value, icon, color, subtitle, progress, progressLabel }) => (
    <Card sx={{ 
      height: '100%',
      transition: 'transform 0.3s ease-in-out',
      '&:hover': {
        transform: 'translateY(-4px)',
        boxShadow: 3
      }
    }}>
      <CardContent sx={{ height: '100%' }}>
        <Stack direction="row" alignItems="center" spacing={2} sx={{ mb: 2 }}>
          <Box
            sx={{
              backgroundColor: `${color}.light`,
              color: `${color}.main`,
              borderRadius: 1,
              p: 1.5,
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            {icon}
          </Box>
          <Box sx={{ flexGrow: 1 }}>
            <Typography variant="body2" color="text.secondary" gutterBottom>
              {title}
            </Typography>
            <Typography variant="h5" fontWeight="bold" color={color === 'error' ? 'error.main' : 'inherit'}>
              {value}
            </Typography>
          </Box>
        </Stack>
        
        {subtitle && (
          <Typography variant="caption" color="text.secondary" sx={{ display: 'block', mb: 1 }}>
            {subtitle}
          </Typography>
        )}
        
        {progress !== undefined && (
          <Box sx={{ mt: 2 }}>
            {progressLabel && (
              <Stack direction="row" justifyContent="space-between" sx={{ mb: 0.5 }}>
                <Typography variant="caption" color="text.secondary">
                  {progressLabel}
                </Typography>
                <Typography variant="caption" fontWeight="bold">
                  {formatPercentage(progress)}
                </Typography>
              </Stack>
            )}
            <LinearProgress
              variant="determinate"
              value={progress}
              sx={{
                height: 6,
                borderRadius: 3,
                backgroundColor: `${color}.light`,
                '& .MuiLinearProgress-bar': {
                  backgroundColor: `${color}.main`,
                  borderRadius: 3
                }
              }}
            />
          </Box>
        )}
      </CardContent>
    </Card>
  );

  const PerformanceCard = ({ rate, title, icon }) => {
    const performanceColor = getPerformanceColor(rate);
    const performanceLabel = getPerformanceLabel(rate);
    
    return (
      <Card sx={{ 
        height: '100%',
        background: `linear-gradient(135deg, ${
          performanceColor === 'success' ? '#43e97b, #38f9d7' :
          performanceColor === 'info' ? '#4facfe, #00f2fe' :
          performanceColor === 'warning' ? '#fa709a, #fee140' :
          '#ff6b6b, #ffa726'
        })`,
        color: 'white'
      }}>
        <CardContent sx={{ 
          height: '100%',
          display: 'flex',
          flexDirection: 'column',
          justifyContent: 'space-between'
        }}>
          <Box>
            <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
              <Box sx={{ 
                backgroundColor: 'rgba(255, 255, 255, 0.2)',
                borderRadius: 1,
                p: 1,
                mr: 1.5
              }}>
                {icon}
              </Box>
              <Typography variant="body2" sx={{ opacity: 0.9 }}>
                {title}
              </Typography>
            </Box>
            
            <Typography variant="h3" fontWeight="bold" sx={{ mb: 1 }}>
              {formatPercentage(rate)}
            </Typography>
            
            <Typography variant="h6" fontWeight="medium">
              {performanceLabel}
            </Typography>
          </Box>
          
          <LinearProgress
            variant="determinate"
            value={rate}
            sx={{
              height: 8,
              borderRadius: 4,
              backgroundColor: 'rgba(255, 255, 255, 0.3)',
              '& .MuiLinearProgress-bar': {
                backgroundColor: 'white',
                borderRadius: 4
              }
            }}
          />
        </CardContent>
      </Card>
    );
  };

  if (type === 'yearly') {
    return (
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} md={6} lg={3}>
          <PerformanceCard
            rate={summary.averageOccupancy}
            title="Average Occupancy"
            icon={<InsertChart />}
          />
        </Grid>
        
        <Grid item xs={12} md={6} lg={3}>
          <StatCard
            title="Peak Performance"
            value={formatPercentage(summary.peakOccupancy)}
            icon={<EmojiEvents />}
            color="warning"
            subtitle={`Best year: ${summary.bestYear}`}
            progress={summary.peakOccupancy}
            progressLabel="Peak rate"
          />
        </Grid>
        
        <Grid item xs={12} md={6} lg={3}>
          <StatCard
            title="Growth Trend"
            value={`${summary.occupancyGrowth > 0 ? '+' : ''}${formatPercentage(summary.occupancyGrowth)}`}
            icon={<TrendingUp />}
            color={summary.occupancyGrowth >= 0 ? "success" : "error"}
            subtitle="Year-over-year change"
            progress={Math.min(Math.abs(summary.occupancyGrowth), 100)}
            progressLabel={summary.occupancyGrowth >= 0 ? "Growth" : "Decline"}
          />
        </Grid>
        
        <Grid item xs={12} md={6} lg={3}>
          <StatCard
            title="Room Utilization"
            value={formatNumber(summary.totalOccupiedRooms)}
            icon={<Bed />}
            color="info"
            subtitle={`${formatNumber(summary.totalRooms)} total rooms`}
            progress={overallOccupancyRate}
            progressLabel="Utilization rate"
          />
        </Grid>
      </Grid>
    );
  } else {
    return (
      <Grid container spacing={3} sx={{ mb: 3 }}>
        <Grid item xs={12} md={6} lg={3}>
          <PerformanceCard
            rate={summary.averageOccupancy}
            title="Monthly Average"
            icon={<CalendarMonth />}
          />
        </Grid>
        
        <Grid item xs={12} md={6} lg={3}>
          <StatCard
            title="Best Month"
            value={formatPercentage(summary.peakOccupancy)}
            icon={<Star />}
            color="success"
            subtitle={summary.peakMonth}
            progress={summary.peakOccupancy}
            progressLabel="Peak rate"
          />
        </Grid>
        
        <Grid item xs={12} md={6} lg={3}>
          <StatCard
            title="Lowest Month"
            value={formatPercentage(summary.lowestOccupancy)}
            icon={<Timeline />}
            color="warning"
            subtitle={summary.worstMonth}
            progress={summary.lowestOccupancy}
            progressLabel="Lowest rate"
          />
        </Grid>
        
        <Grid item xs={12} md={6} lg={3}>
          <StatCard
            title="Capacity Usage"
            value={formatNumber(summary.totalOccupiedRooms)}
            icon={<Hotel />}
            color="info"
            subtitle={`${summary.monthsTracked} months tracked`}
            progress={overallOccupancyRate}
            progressLabel="Monthly utilization"
          />
        </Grid>
      </Grid>
    );
  }
};

export default SummaryCards;