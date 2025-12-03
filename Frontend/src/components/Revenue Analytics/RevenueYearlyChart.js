import React from 'react';
import {
  ComposedChart,
  Line,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  ResponsiveContainer
} from 'recharts';
import { Box, Typography, useTheme } from '@mui/material';
import CustomTooltip from '../CustomTooltip';

const RevenueYearlyChart = ({ data, onYearClick, isAllHotels = false }) => {
  const theme = useTheme();

  if (!data || data.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Typography color="text.secondary">
          No revenue data available
        </Typography>
      </Box>
    );
  }

  // Calculate dynamic Y-axis domain based on data
  const calculateYAxisDomain = () => {
    if (!data || data.length === 0) return [0, 100];
    
    const maxRevenue = Math.max(...data.map(item => item.totalRevenue || 0));
    const minRevenue = Math.min(...data.map(item => item.totalRevenue || 0));
    
    // For all hotels (aggregated), use larger scale
    if (isAllHotels) {
      const padding = maxRevenue * 0.1; // 10% padding
      return [0, maxRevenue + padding];
    } 
    // For individual hotels, use tighter scale
    else {
      const range = maxRevenue - minRevenue;
      const padding = range * 0.1; // 10% padding
      return [Math.max(0, minRevenue - padding), maxRevenue + padding];
    }
  };

  const yAxisDomain = calculateYAxisDomain();

  // Format Y-axis ticks based on scale
  const formatYAxisTick = (value) => {
    if (isAllHotels && value >= 1000000) {
      return `$${(value / 1000000).toFixed(1)}M`;
    } else if (isAllHotels && value >= 1000) {
      return `$${(value / 1000).toFixed(0)}K`;
    } else {
      return `$${value.toLocaleString()}`;
    }
  };

  const CustomDot = ({ cx, cy, payload, onDotClick }) => {
    const handleClick = () => {
      if (payload && payload.periodDate && onDotClick) {
        const year = payload.periodDate.substring(0, 4);
        onDotClick(year);
      }
    };

    return (
      <circle 
        cx={cx} 
        cy={cy} 
        r={6} 
        fill={theme.palette.primary.main}
        stroke="#fff" 
        strokeWidth={2}
        onClick={handleClick}
        style={{ cursor: 'pointer' }}
      />
    );
  };

  const CustomActiveDot = ({ cx, cy, payload, onDotClick }) => {
    const handleClick = () => {
      if (payload && payload.periodDate && onDotClick) {
        const year = payload.periodDate.substring(0, 4);
        onDotClick(year);
      }
    };

    return (
      <circle 
        cx={cx} 
        cy={cy} 
        r={8} 
        fill={theme.palette.primary.dark}
        stroke="#fff" 
        strokeWidth={2}
        onClick={handleClick}
        style={{ cursor: 'pointer' }}
      />
    );
  };

  return (
    <Box sx={{ width: '100%', height: 400 }}>
      <ResponsiveContainer width="100%" height="100%">
        <ComposedChart
          data={data}
          margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke={theme.palette.divider} />
          <XAxis 
            dataKey="year" 
            tick={{ fill: theme.palette.text.primary }}
            axisLine={{ stroke: theme.palette.divider }}
          />
          <YAxis 
            domain={yAxisDomain}
            tickFormatter={formatYAxisTick}
            tick={{ fill: theme.palette.text.primary }}
            axisLine={{ stroke: theme.palette.divider }}
          />
          <Tooltip 
            content={<CustomTooltip currencyFields={['roomRevenue', 'serviceRevenue', 'totalRevenue', 'avgDailyRevenue', 'avgRoomRate', 'avgServicePerStay']} />}
          />
          <Legend />
          
          {/* Total Revenue Line */}
          <Line
            type="monotone"
            dataKey="totalRevenue"
            name="Total Revenue"
            stroke={theme.palette.primary.main}
            strokeWidth={3}
            dot={<CustomDot onDotClick={onYearClick} />}
            activeDot={<CustomActiveDot onDotClick={onYearClick} />}
          />
          
          {/* Room Revenue Line */}
          <Line
            type="monotone"
            dataKey="roomRevenue"
            name="Room Revenue"
            stroke={theme.palette.success.main}
            strokeWidth={2}
            dot={<CustomDot onDotClick={onYearClick} />}
            activeDot={<CustomActiveDot onDotClick={onYearClick} />}
          />
          
          {/* Service Revenue Line */}
          <Line
            type="monotone"
            dataKey="serviceRevenue"
            name="Service Revenue"
            stroke={theme.palette.warning.main}
            strokeWidth={2}
            dot={<CustomDot onDotClick={onYearClick} />}
            activeDot={<CustomActiveDot onDotClick={onYearClick} />}
          />
        </ComposedChart>
      </ResponsiveContainer>
      
      <Typography variant="caption" color="text.secondary" sx={{ mt: 1, display: 'block', textAlign: 'center' }}>
        Click on any data point to view monthly breakdown for that year
      </Typography>
    </Box>
  );
};

export default RevenueYearlyChart;