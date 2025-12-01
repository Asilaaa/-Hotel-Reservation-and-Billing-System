import React from 'react';
import { Paper, Typography } from '@mui/material';

const CustomTooltip = ({ active, payload, label, selectedYear }) => {
  if (active && payload && payload.length) {
    const data = payload[0].payload;
    return (
      <Paper sx={{ p: 2, background: 'rgba(255,255,255,0.95)' }}>
        <Typography variant="body2" fontWeight="bold">
          {selectedYear ? `Month: ${label}` : `Year: ${label}`}
        </Typography>
        <Typography variant="body2" color="#8884d8">
          Occupancy Rate: {data.occupancyRate?.toFixed(1)}%
        </Typography>
        <Typography variant="body2" color="#82ca9d">
          Occupied Rooms: {data.occupiedRooms}
        </Typography>
        <Typography variant="body2" color="#ffc658">
          Total Rooms: {data.totalRooms}
        </Typography>
        {!selectedYear && (
          <Typography variant="body2" color="text.secondary" sx={{ mt: 1, fontStyle: 'italic' }}>
            Click on data points to view monthly data
          </Typography>
        )}
      </Paper>
    );
  }
  return null;
};

export default CustomTooltip;