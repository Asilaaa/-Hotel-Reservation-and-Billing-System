import React, { useState, useEffect } from 'react';
import { 
  Box, Typography, Alert, CircularProgress, Tabs, Tab, Chip, Card, CardContent 
} from '@mui/material';

// Components
import HotelSelector from '../components/Occupancy Statistics/HotelSelector';
import SummaryCards from '../components/Occupancy Statistics/SummaryCards';
import YearlyChart from '../components/Occupancy Statistics/YearlyChart';
import MonthlyChart from '../components/Occupancy Statistics/MonthlyChart';
import DebugInfo from '../components/DebugInfo';

const Analytics = () => {
  const [selectedHotel, setSelectedHotel] = useState('all');
  const [tabValue, setTabValue] = useState(1);
  const [occupancyData, setOccupancyData] = useState([]);
  const [monthlyData, setMonthlyData] = useState([]);
  const [selectedYear, setSelectedYear] = useState(null);
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchHotels();
  }, []);

  useEffect(() => {
    if (hotels.length > 0) {
      if (selectedYear) {
        fetchMonthlyData(selectedYear);
      } else {
        fetchYearlyData();
      }
    }
  }, [selectedHotel, selectedYear]);

  const fetchHotels = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/hotels');
      if (response.ok) {
        const hotelsData = await response.json();
        setHotels(hotelsData);
      } else {
        setError('Failed to load hotels');
      }
    } catch (error) {
      console.error('Error fetching hotels:', error);
      setError('Failed to connect to server');
    }
  };

  const fetchYearlyData = async () => {
    setLoading(true);
    setError(null);
    try {
      const hotelId = selectedHotel === 'all' ? -1 : selectedHotel;
      const url = `http://localhost:8080/api/statistics/occupancy/yearly/${hotelId}`;
      
      const response = await fetch(url);
      if (response.ok) {
        const data = await response.json();
        setOccupancyData(data);
      } else {
        setError('Failed to load yearly occupancy data');
        setOccupancyData([]);
      }
    } catch (error) {
      console.error('Error fetching yearly data:', error);
      setError('Failed to connect to server');
      setOccupancyData([]);
    } finally {
      setLoading(false);
    }
  };

  const fetchMonthlyData = async (year) => {
    setLoading(true);
    setError(null);
    try {
      const hotelId = selectedHotel === 'all' ? -1 : selectedHotel;
      const url = `http://localhost:8080/api/statistics/occupancy/monthly/${hotelId}?year=${year}`;
      
      const response = await fetch(url);
      if (response.ok) {
        const data = await response.json();
        setMonthlyData(data);
      } else {
        setError(`Failed to load monthly data for ${year}`);
        setMonthlyData([]);
      }
    } catch (error) {
      console.error('Error fetching monthly data:', error);
      setError('Failed to connect to server');
      setMonthlyData([]);
    } finally {
      setLoading(false);
    }
  };

  const handleHotelChange = (e) => {
    setSelectedHotel(e.target.value);
    setSelectedYear(null);
    setMonthlyData([]);
  };

  const handleYearClick = (year) => {
    setSelectedYear(year);
  };

  const handleBackToYearly = () => {
    setSelectedYear(null);
    setMonthlyData([]);
  };

  // Data formatting functions
  const formatYearlyData = () => {
    if (!occupancyData || occupancyData.length === 0) return [];
    return occupancyData.map(stat => ({
      year: stat.periodDate ? stat.periodDate.substring(0, 4) : 'Unknown',
      periodDate: stat.periodDate,
      occupancyRate: stat.occupancyRate,
      occupiedRooms: stat.occupiedRooms,
      totalRooms: stat.totalRooms,
      hotelName: stat.hotelName
    }));
  };

  const formatMonthlyData = () => {
    if (!monthlyData || monthlyData.length === 0) return [];
    const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return monthlyData.map(stat => {
      const monthIndex = stat.periodDate ? parseInt(stat.periodDate.substring(5, 7)) - 1 : 0;
      return {
        month: monthNames[monthIndex] || 'Unknown',
        periodDate: stat.periodDate,
        occupancyRate: stat.occupancyRate,
        occupiedRooms: stat.occupiedRooms,
        totalRooms: stat.totalRooms,
        hotelName: stat.hotelName
      };
    });
  };

  const selectedHotelName = selectedHotel === 'all' 
    ? 'All Hotels' 
    : hotels.find(h => h.hotelId && h.hotelId.toString() === selectedHotel.toString())?.name || 'Selected Hotel';

  const yearlyChartData = formatYearlyData();
  const monthlyChartData = formatMonthlyData();

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h3" gutterBottom fontWeight="bold">
        Hotel Analytics Dashboard
      </Typography>

      <HotelSelector 
        selectedHotel={selectedHotel}
        hotels={hotels}
        onHotelChange={handleHotelChange}
      />

      <Card sx={{ mb: 3 }}>
        <Tabs value={tabValue} onChange={(e, v) => setTabValue(v)} centered>
          <Tab label="Revenue Analytics" />
          <Tab label="Occupancy Statistics" />
          <Tab label="Service Analytics" />
        </Tabs>
      </Card>

      {error && (
        <Alert severity="error" sx={{ mb: 2 }} onClose={() => setError(null)}>
          {error}
        </Alert>
      )}

      {tabValue === 1 && (
        <Box>
          <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <Typography variant="h5">
              {selectedYear ? `Monthly Occupancy for ${selectedYear} - ${selectedHotelName}` : `Yearly Occupancy Rate - ${selectedHotelName}`}
            </Typography>
            {selectedYear && (
              <Chip 
                label="← Back to Yearly View" 
                onClick={handleBackToYearly}
                color="primary"
                clickable
              />
            )}
          </Box>

          {loading ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
              <CircularProgress />
            </Box>
          ) : (
            <>
              <SummaryCards 
                data={selectedYear ? monthlyChartData : yearlyChartData}
                type={selectedYear ? 'monthly' : 'yearly'}
              />

              <Card>
                <CardContent>
                  {selectedYear ? (
                    monthlyChartData.length > 0 ? (
                      <MonthlyChart data={monthlyChartData} selectedYear={selectedYear} />
                    ) : (
                      <Box sx={{ textAlign: 'center', py: 4 }}>
                        <Typography color="text.secondary">
                          No monthly occupancy data available for {selectedYear}
                        </Typography>
                      </Box>
                    )
                  ) : (
                    yearlyChartData.length > 0 ? (
                      <YearlyChart data={yearlyChartData} onYearClick={handleYearClick} />
                    ) : (
                      <Box sx={{ textAlign: 'center', py: 4 }}>
                        <Typography color="text.secondary">
                          No yearly occupancy data available
                        </Typography>
                      </Box>
                    )
                  )}
                </CardContent>
              </Card>
            </>
          )}
        </Box>
      )}
    </Box>
  );
};

export default Analytics;