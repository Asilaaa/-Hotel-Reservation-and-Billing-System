import React from 'react';
import { Card, CardContent, Grid, FormControl, InputLabel, Select, MenuItem } from '@mui/material';

const HotelSelector = ({ selectedHotel, hotels, onHotelChange }) => {
  return (
    <Card sx={{ mb: 3 }}>
      <CardContent>
        <Grid container spacing={3} alignItems="center">
          <Grid item xs={12} md={6}>
            <FormControl fullWidth>
              <InputLabel>Select Hotel</InputLabel>
              <Select
                value={selectedHotel}
                label="Select Hotel"
                onChange={onHotelChange}
              >
                <MenuItem value="all">All Hotels (Overall Statistics)</MenuItem>
                {hotels.map(hotel => (
                  <MenuItem key={hotel.hotelId} value={hotel.hotelId.toString()}>
                    {hotel.name}
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
          </Grid>
        </Grid>
      </CardContent>
    </Card>
  );
};

export default HotelSelector;