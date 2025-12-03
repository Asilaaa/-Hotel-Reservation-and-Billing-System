import React from 'react';
import {
  ComposedChart,
  Bar,
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

const RevenueMonthlyChart = ({ data, selectedYear, isAllHotels = false }) => {
  const theme = useTheme();

  if (!data || data.length === 0) {
    return (
      <Box sx={{ textAlign: 'center', py: 4 }}>
        <Typography color="text.secondary">
          No monthly revenue data available for {selectedYear}
        </Typography>
      </Box>
    );
  }

  // Sort data by month order
  const monthOrder = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
  const sortedData = [...data].sort((a, b) => 
    monthOrder.indexOf(a.month) - monthOrder.indexOf(b.month)
  );

  // Calculate dynamic Y-axis domain
  const calculateYAxisDomain = () => {
    if (!sortedData || sortedData.length === 0) return [0, 100];
    
    const maxRevenue = Math.max(...sortedData.map(item => item.totalRevenue || 0));
    
    // For all hotels (aggregated), use larger scale
    if (isAllHotels) {
      const padding = maxRevenue * 0.15; // 15% padding for aggregated data
      return [0, maxRevenue + padding];
    } 
    // For individual hotels, use tighter scale
    else {
      const minRevenue = Math.min(...sortedData.map(item => item.totalRevenue || 0));
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

  return (
    <Box sx={{ width: '100%', height: 400 }}>
      <Typography variant="h6" align="center" gutterBottom>
        Monthly Revenue Breakdown - {selectedYear}
      </Typography>
      
      <ResponsiveContainer width="100%" height="90%">
        <ComposedChart
          data={sortedData}
          margin={{ top: 20, right: 30, left: 20, bottom: 20 }}
        >
          <CartesianGrid strokeDasharray="3 3" stroke={theme.palette.divider} />
          <XAxis 
            dataKey="month" 
            tick={{ fill: theme.palette.text.primary }}
            axisLine={{ stroke: theme.palette.divider }}
          />
          <YAxis 
            yAxisId="left"
            domain={yAxisDomain}
            tickFormatter={formatYAxisTick}
            tick={{ fill: theme.palette.text.primary }}
            axisLine={{ stroke: theme.palette.divider }}
          />
          <Tooltip 
            content={<CustomTooltip currencyFields={['roomRevenue', 'serviceRevenue', 'totalRevenue', 'avgDailyRevenue', 'avgRoomRate', 'avgServicePerStay']} />}
          />
          <Legend />
          
          {/* Total Revenue - Line */}
          <Line
            yAxisId="left"
            type="monotone"
            dataKey="totalRevenue"
            name="Total Revenue"
            stroke={theme.palette.primary.main}
            strokeWidth={3}
            dot={{ r: 4 }}
            activeDot={{ r: 6 }}
          />
          
          {/* Room Revenue - Bar */}
          <Bar
            yAxisId="left"
            dataKey="roomRevenue"
            name="Room Revenue"
            fill={theme.palette.success.main}
            fillOpacity={0.8}
          />
          
          {/* Service Revenue - Bar */}
          <Bar
            yAxisId="left"
            dataKey="serviceRevenue"
            name="Service Revenue"
            fill={theme.palette.warning.main}
            fillOpacity={0.8}
          />
        </ComposedChart>
      </ResponsiveContainer>
    </Box>
  );
};

export default RevenueMonthlyChart;