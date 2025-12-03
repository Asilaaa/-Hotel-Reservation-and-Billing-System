import React from 'react';
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, CartesianGrid, Legend, Tooltip } from 'recharts';
import CustomTooltip from '../CustomTooltip';
import { useTheme } from '@mui/material/styles';

const MonthlyChart = ({ data, selectedYear, isAllHotels = false }) => {
  const theme = useTheme();
  
  if (!data || data.length === 0) return null;

  // Calculate dynamic Y-axis domain based on data
  const occupancyRates = data.map(item => item.occupancyRate || 0);
  const maxRate = Math.max(...occupancyRates);
  const minRate = Math.min(...occupancyRates);
  
  // For aggregated data (All Hotels), adjust scaling
  const padding = (maxRate - minRate) * 0.1;
  const domainMin = Math.max(0, Math.floor(minRate - padding));
  const domainMax = Math.min(isAllHotels ? 120 : 100, Math.ceil(maxRate + padding));

  return (
    <ResponsiveContainer width="100%" height={400}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" stroke={theme.palette.divider} />
        <XAxis 
          dataKey="month" 
          tick={{ fill: theme.palette.text.primary }}
          axisLine={{ stroke: theme.palette.divider }}
        />
        <YAxis 
          domain={[domainMin, domainMax]} 
          tickCount={6}
          allowDataOverflow={false}
          tick={{ fill: theme.palette.text.primary }}
          axisLine={{ stroke: theme.palette.divider }}
        />
        <Tooltip content={<CustomTooltip selectedYear={selectedYear} />} />
        <Legend />
        <Line 
          type="monotone" 
          dataKey="occupancyRate" 
          stroke="#8884d8" 
          strokeWidth={3}
          name="Occupancy Rate"
          dot={{ r: 6 }}
        />
      </LineChart>
    </ResponsiveContainer>
  );
};

export default MonthlyChart;