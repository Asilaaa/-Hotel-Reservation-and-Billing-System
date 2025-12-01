import React from 'react';
import { ResponsiveContainer, LineChart, Line, XAxis, YAxis, CartesianGrid, Legend, Tooltip } from 'recharts';
import { CustomizedDot, CustomizedActiveDot } from './ChartDots';
import CustomTooltip from './CustomTooltip';

const YearlyChart = ({ data, onYearClick }) => {
  if (!data || data.length === 0) return null;

  return (
    <ResponsiveContainer width="100%" height={400}>
      <LineChart data={data}>
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis dataKey="year" />
        <YAxis yAxisId="left" domain={[0, 100]} />
        <YAxis yAxisId="right" orientation="right" />
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