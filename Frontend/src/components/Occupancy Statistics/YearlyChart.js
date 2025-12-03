import React from 'react';
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, CartesianGrid, Legend, Tooltip } from 'recharts';
import { CustomizedDot, CustomizedActiveDot } from '../ChartDots';
import CustomTooltip from '../CustomTooltip';
import { useTheme } from '@mui/material/styles';

const YearlyChart = ({ data, onYearClick, isAllHotels = false }) => {
  const theme = useTheme();
  
  if (!data || data.length === 0) return null;

  // Calculate dynamic Y-axis domain based on data
  const calculateYAxisDomain = () => {
    if (!data || data.length === 0) return [0, 100];
    
    const occupancyRates = data.map(item => item.occupancyRate || 0);
    const maxRate = Math.max(...occupancyRates);
    const minRate = Math.min(...occupancyRates);
    
    // For aggregated data (All Hotels), we might have higher occupancy
    const padding = (maxRate - minRate) * 0.1;
    const domainMin = Math.max(0, Math.floor(minRate - padding));
    
    // Cap at 100% or slightly above for better visualization
    const domainMax = Math.min(isAllHotels ? 120 : 100, Math.ceil(maxRate + padding));
    
    return [domainMin, domainMax];
  };

  const yAxisDomain = calculateYAxisDomain();

  return (
    <ResponsiveContainer width="100%" height={400}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" stroke={theme.palette.divider} />
        <XAxis 
          dataKey="year" 
          tick={{ fill: theme.palette.text.primary }}
          axisLine={{ stroke: theme.palette.divider }}
        />
        <YAxis 
          yAxisId="left" 
          domain={yAxisDomain} 
          tick={{ fill: theme.palette.text.primary }}
          axisLine={{ stroke: theme.palette.divider }}
        />
        <YAxis 
          yAxisId="right" 
          orientation="right" 
          tick={{ fill: theme.palette.text.primary }}
          axisLine={{ stroke: theme.palette.divider }}
        />
        <Tooltip content={<CustomTooltip />} />
        <Legend />
        <Line 
          yAxisId="left"
          type="monotone" 
          dataKey="occupancyRate" 
          stroke="#8884d8" 
          strokeWidth={3}
          name="Occupancy Rate"
          dot={<CustomizedDot onDotClick={onYearClick} />}
          activeDot={<CustomizedActiveDot onDotClick={onYearClick} />}
        />
        <Line 
          yAxisId="right"
          type="monotone" 
          dataKey="occupiedRooms" 
          stroke="#82ca9d" 
          strokeWidth={2}
          strokeDasharray="5 5"
          name="Occupied Rooms"
        />
      </LineChart>
    </ResponsiveContainer>
  );
};

export default YearlyChart;