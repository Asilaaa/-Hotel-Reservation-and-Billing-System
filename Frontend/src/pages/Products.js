import React from 'react';
import { Card, CardContent, Typography, Box } from '@mui/material';

const Products = () => {
  return (
    <Box>
      <Typography variant="h3" gutterBottom fontWeight="bold" className="gradient-text">
        Products
      </Typography>
      <Card className="glass">
        <CardContent>
          <Typography variant="h6">Product Management Coming Soon</Typography>
          <Typography>Product catalog and inventory will be here.</Typography>
        </CardContent>
      </Card>
    </Box>
  );
};

export default Products;