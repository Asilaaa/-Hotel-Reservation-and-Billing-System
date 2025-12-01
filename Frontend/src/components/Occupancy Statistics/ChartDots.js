import React from 'react';

export const CustomizedDot = (props) => {
  const { cx, cy, payload, onDotClick } = props;

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
      r={6} 
      fill="#8884d8" 
      stroke="#fff" 
      strokeWidth={2}
      onClick={handleClick}
      style={{ cursor: 'pointer' }}
    />
  );
};

export const CustomizedActiveDot = (props) => {
  const { cx, cy, payload, onDotClick } = props;

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
      r={8} 
      fill="#ff7300" 
      stroke="#fff" 
      strokeWidth={2}
      onClick={handleClick}
      style={{ cursor: 'pointer' }}
    />
  );
};