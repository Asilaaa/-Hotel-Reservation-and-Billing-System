import React from 'react';
import { Card, CardContent, Typography, Box } from '@mui/material';

const Users = () => {
  return (
    <Box>
      <Typography variant="h3" gutterBottom fontWeight="bold" className="gradient-text">
        Users Management
      </Typography>
      <Card className="glass">
        <CardContent>
          <Typography variant="h6">User Management Coming Soon</Typography>
          <Typography>User lists and management tools will be here.</Typography>
        </CardContent>
      </Card>
    </Box>
  );
};

export default Users;