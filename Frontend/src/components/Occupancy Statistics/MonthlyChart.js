import React from 'react';
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, CartesianGrid, Legend, Tooltip } from 'recharts';
import CustomTooltip from './CustomTooltip';

const MonthlyChart = ({ data, selectedYear }) => {
  if (!data || data.length === 0) return null;

  // Calculate dynamic Y-axis domain based on data
  const occupancyRates = data.map(item => item.occupancyRate || 0);
  const minRate = Math.min(...occupancyRates);
  const maxRate = Math.max(...occupancyRates);
  
  // Add some padding to the Y-axis (10% padding on top and bottom)
  const padding = (maxRate - minRate) * 0.1;
  const domainMin = Math.max(0, Math.floor(minRate - padding));
  const domainMax = Math.min(100, Math.ceil(maxRate + padding));

  return (
    <ResponsiveContainer width="100%" height={400}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="month" />
        <YAxis 
          domain={[domainMin, domainMax]} 
          tickCount={6}
          allowDataOverflow={false}
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