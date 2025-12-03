import React from 'react';

export const CustomizedDot = ({ cx, cy, payload, onDotClick, color = "#8884d8", size = 6 }) => {
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
      r={size} 
      fill={color}
      stroke="#fff" 
      strokeWidth={2}
      onClick={handleClick}
      style={{ cursor: 'pointer' }}
    />
  );
};

export const CustomizedActiveDot = ({ cx, cy, payload, onDotClick, color = "#ff7300", size = 8 }) => {
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
      r={size} 
      fill={color}
      stroke="#fff" 
      strokeWidth={2}
      onClick={handleClick}
      style={{ cursor: 'pointer' }}
    />
  );
};