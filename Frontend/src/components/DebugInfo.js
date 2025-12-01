import React from 'react';
import { Card, CardContent, Typography } from '@mui/material';

const DebugInfo = ({ selectedYear, yearlyDataPoints, monthlyDataPoints, selectedHotelName, yearlyDataSample }) => {
  return (
    <Card sx={{ mb: 2, bgcolor: 'warning.light' }}>
      <CardContent>
        <Typography variant="h6">Debug Info</Typography>
        <Typography>Selected Year: {selectedYear || 'None'}</Typography>
        <Typography>Yearly Data Points: {yearlyDataPoints}</Typography>
        <Typography>Monthly Data Points: {monthlyDataPoints}</Typography>
        <Typography>Selected Hotel: {selectedHotelName}</Typography>
        <Typography>Yearly Data Sample: {yearlyDataPoints > 0 ? JSON.stringify(yearlyDataSample) : 'No data'}</Typography>
      </CardContent>
    </Card>
  );
};

export default DebugInfo;