import React from 'react';
import { Card, CardContent, Typography, Box } from '@mui/material';

const Settings = () => {
  return (
    <Box>
      <Typography variant="h3" gutterBottom fontWeight="bold" className="gradient-text">
        Settings
      </Typography>
      <Card className="glass">
        <CardContent>
          <Typography variant="h6">Settings Panel Coming Soon</Typography>
          <Typography>Application settings and configuration will be here.</Typography>
        </CardContent>
      </Card>
    </Box>
  );
};

export default Settings;