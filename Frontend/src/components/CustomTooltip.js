import React from 'react';
import { Box, Typography } from '@mui/material';
import { useTheme } from '@mui/material/styles';

const CustomTooltip = ({ active, payload, label, currencyFields = [] }) => {
  const theme = useTheme();

  if (active && payload && payload.length) {
    return (
      <Box
        sx={{
          backgroundColor: 'background.paper',
          p: 2,
          border: `1px solid ${theme.palette.divider}`,
          borderRadius: 1,
          boxShadow: theme.shadows[3]
        }}
      >
        <Typography variant="subtitle2" fontWeight="bold" gutterBottom>
          {label}
        </Typography>
        {payload.map((entry, index) => (
          <Typography 
            key={index} 
            variant="body2" 
            sx={{ 
              color: entry.color,
              display: 'flex',
              justifyContent: 'space-between',
              gap: 2
            }}
          >
            <span>{entry.name}:</span>
            <span>
              {currencyFields.includes(entry.dataKey) ? '$' : ''}
              {entry.value ? entry.value.toLocaleString('en-US', {
                minimumFractionDigits: currencyFields.includes(entry.dataKey) ? 2 : 0,
                maximumFractionDigits: currencyFields.includes(entry.dataKey) ? 2 : 0
              }) : '0'}
              {entry.dataKey.includes('Rate') || entry.dataKey.includes('Percentage') ? '%' : ''}
            </span>
          </Typography>
        ))}
      </Box>
    );
  }
  return null;
};

export default CustomTooltip;