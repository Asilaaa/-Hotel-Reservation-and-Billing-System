import React from 'react';
import { Grid, Card, CardContent, Typography } from '@mui/material';

const SummaryCards = ({ data, type = 'yearly' }) => {
  if (!data || data.length === 0) return null;

  const cardsData = type === 'yearly' ? getYearlySummary(data) : getMonthlySummary(data);

  return (
    <Grid container spacing={3} sx={{ mb: 3 }}>
      {cardsData.map((card, index) => (
        <Grid item xs={12} md={3} key={index}>
          <Card sx={{ background: card.gradient }}>
            <CardContent sx={{ textAlign: 'center', color: 'white' }}>
              <Typography variant="h4" fontWeight="bold" color={card.color || 'inherit'}>
                {card.value}
              </Typography>
              <Typography variant="body1">{card.label}</Typography>
            </CardContent>
          </Card>
        </Grid>
      ))}
    </Grid>
  );
};

const getYearlySummary = (yearlyData) => {
  const averageOccupancy = (yearlyData.reduce((sum, item) => sum + item.occupancyRate, 0) / yearlyData.length).toFixed(1);
  const peakOccupancy = Math.max(...yearlyData.map(item => item.occupancyRate)).toFixed(1);
  const growth = (yearlyData[yearlyData.length - 1].occupancyRate - yearlyData[0].occupancyRate).toFixed(1);
  
  return [
    {
      value: `${averageOccupancy}%`,
      label: 'Average Occupancy',
      gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    },
    {
      value: `${peakOccupancy}%`,
      label: 'Peak Year',
      gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
    },
    {
      value: `${growth}%`,
      label: 'Growth',
      gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
      color: growth >= 0 ? 'inherit' : 'error'
    },
    {
      value: yearlyData.length,
      label: 'Years Tracked',
      gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
    }
  ];
};

const getMonthlySummary = (monthlyData) => {
  const averageMonthlyOccupancy = (monthlyData.reduce((sum, item) => sum + item.occupancyRate, 0) / monthlyData.length).toFixed(1);
  const bestMonthOccupancy = Math.max(...monthlyData.map(item => item.occupancyRate)).toFixed(1);
  const peakMonthlyOccupancy = Math.max(...monthlyData.map(item => item.occupiedRooms));

  return [
    {
      value: `${averageMonthlyOccupancy}%`,
      label: 'Avg Monthly Rate',
      gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
    },
    {
      value: `${bestMonthOccupancy}%`,
      label: 'Best Month',
      gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
    },
    {
      value: peakMonthlyOccupancy,
      label: 'Peak Occupancy',
      gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
    },
    {
      value: monthlyData.length,
      label: 'Months Tracked',
      gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
    }
  ];
};

export default SummaryCards;