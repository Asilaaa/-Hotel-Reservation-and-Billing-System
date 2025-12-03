import React, { useState, useEffect } from 'react';
import { 
  Box, Typography, Alert, CircularProgress, Tabs, Tab, Chip, Card, CardContent 
} from '@mui/material';

// Shared Components
import HotelSelector from '../components/HotelSelector';

// Occupancy Components
import SummaryCards from '../components/Occupancy Statistics/SummaryCards';
import YearlyChart from '../components/Occupancy Statistics/YearlyChart';
import MonthlyChart from '../components/Occupancy Statistics/MonthlyChart';

// Revenue Components
import RevenueSummaryCards from '../components/Revenue Analytics/RevenueSummaryCards';
import RevenueYearlyChart from '../components/Revenue Analytics/RevenueYearlyChart';
import RevenueMonthlyChart from '../components/Revenue Analytics/RevenueMonthlyChart';

const Analytics = () => {
  const [selectedHotel, setSelectedHotel] = useState('all');
  const [tabValue, setTabValue] = useState(0);
  
  // Occupancy states
  const [occupancyData, setOccupancyData] = useState([]);
  const [monthlyData, setMonthlyData] = useState([]);
  const [selectedYear, setSelectedYear] = useState(null);
  
  // Revenue states
  const [revenueYearlyData, setRevenueYearlyData] = useState([]);
  const [revenueMonthlyData, setRevenueMonthlyData] = useState([]);
  const [revenueSelectedYear, setRevenueSelectedYear] = useState(null);
  
  // Common states
  const [hotels, setHotels] = useState([]);
  const [loading, setLoading] = useState({ occupancy: false, revenue: false });
  const [error, setError] = useState(null);

  useEffect(() => {
    fetchHotels();
  }, []);

  // Fetch data based on active tab
  useEffect(() => {
    if (hotels.length === 0) return;

    if (tabValue === 1) { // Occupancy tab
      if (selectedYear) {
        fetchMonthlyData(selectedYear);
      } else {
        fetchYearlyData();
      }
    } else if (tabValue === 0) { // Revenue tab
      if (revenueSelectedYear) {
        fetchRevenueMonthlyData(revenueSelectedYear);
      } else {
        fetchRevenueYearlyData();
      }
    }
  }, [selectedHotel, selectedYear, revenueSelectedYear, tabValue, hotels.length]);

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

  // Occupancy API calls
  const fetchYearlyData = async () => {
    setLoading(prev => ({ ...prev, occupancy: true }));
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
      setLoading(prev => ({ ...prev, occupancy: false }));
    }
  };

  const fetchMonthlyData = async (year) => {
    setLoading(prev => ({ ...prev, occupancy: true }));
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
      setLoading(prev => ({ ...prev, occupancy: false }));
    }
  };

  // Revenue API calls
  const fetchRevenueYearlyData = async () => {
    setLoading(prev => ({ ...prev, revenue: true }));
    try {
      const hotelId = selectedHotel === 'all' ? -1 : selectedHotel;
      const url = `http://localhost:8080/api/statistics/revenue/yearly/${hotelId}`;
      
      const response = await fetch(url);
      if (response.ok) {
        const data = await response.json();
        setRevenueYearlyData(data);
      } else {
        setError('Failed to load yearly revenue data');
        setRevenueYearlyData([]);
      }
    } catch (error) {
      console.error('Error fetching revenue yearly data:', error);
      setError('Failed to connect to server');
      setRevenueYearlyData([]);
    } finally {
      setLoading(prev => ({ ...prev, revenue: false }));
    }
  };

  const fetchRevenueMonthlyData = async (year) => {
    setLoading(prev => ({ ...prev, revenue: true }));
    try {
      const hotelId = selectedHotel === 'all' ? -1 : selectedHotel;
      const url = `http://localhost:8080/api/statistics/revenue/monthly/${hotelId}?year=${year}`;
      
      const response = await fetch(url);
      if (response.ok) {
        const data = await response.json();
        setRevenueMonthlyData(data);
      } else {
        setError(`Failed to load monthly revenue data for ${year}`);
        setRevenueMonthlyData([]);
      }
    } catch (error) {
      console.error('Error fetching revenue monthly data:', error);
      setError('Failed to connect to server');
      setRevenueMonthlyData([]);
    } finally {
      setLoading(prev => ({ ...prev, revenue: false }));
    }
  };

  const handleHotelChange = (e) => {
    const newHotel = e.target.value;
    setSelectedHotel(newHotel);
    setSelectedYear(null);
    setMonthlyData([]);
    setRevenueSelectedYear(null);
    setRevenueMonthlyData([]);
  };

  // Occupancy handlers
  const handleYearClick = (year) => {
    setSelectedYear(year);
  };

  const handleBackToYearly = () => {
    setSelectedYear(null);
    setMonthlyData([]);
  };

  // Revenue handlers
  const handleRevenueYearClick = (year) => {
    setRevenueSelectedYear(year);
  };

  const handleBackToRevenueYearly = () => {
    setRevenueSelectedYear(null);
    setRevenueMonthlyData([]);
  };

  // Data formatting
  const formatRevenueYearlyData = () => {
    if (!revenueYearlyData || revenueYearlyData.length === 0) return [];
    return revenueYearlyData.map(stat => ({
      year: stat.periodDate ? stat.periodDate.substring(0, 4) : 'Unknown',
      periodDate: stat.periodDate,
      roomRevenue: stat.roomRevenue || 0,
      serviceRevenue: stat.serviceRevenue || 0,
      totalRevenue: stat.totalRevenue || 0,
      avgDailyRevenue: stat.avgDailyRevenue || 0,
      avgRoomRate: stat.avgRoomRate || 0,
      avgServicePerStay: stat.avgServicePerStay || 0,
      totalStays: stat.totalStays || 0,
      totalRoomsBooked: stat.totalRoomsBooked || 0,
      totalServices: stat.totalServices || 0,
      hotelName: stat.hotelName || 'All Hotels'
    }));
  };

  const formatRevenueMonthlyData = () => {
    if (!revenueMonthlyData || revenueMonthlyData.length === 0) return [];
    const monthNames = ['Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'];
    return revenueMonthlyData.map(stat => {
      const monthIndex = stat.periodDate ? parseInt(stat.periodDate.substring(5, 7)) - 1 : 0;
      return {
        month: monthNames[monthIndex] || 'Unknown',
        periodDate: stat.periodDate,
        roomRevenue: stat.roomRevenue || 0,
        serviceRevenue: stat.serviceRevenue || 0,
        totalRevenue: stat.totalRevenue || 0,
        avgDailyRevenue: stat.avgDailyRevenue || 0,
        avgRoomRate: stat.avgRoomRate || 0,
        avgServicePerStay: stat.avgServicePerStay || 0,
        totalStays: stat.totalStays || 0,
        totalRoomsBooked: stat.totalRoomsBooked || 0,
        totalServices: stat.totalServices || 0,
        hotelName: stat.hotelName || 'All Hotels'
      };
    });
  };

  const formatOccupancyYearlyData = () => {
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

  const formatOccupancyMonthlyData = () => {
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

  const revenueYearlyChartData = formatRevenueYearlyData();
  const revenueMonthlyChartData = formatRevenueMonthlyData();
  const occupancyYearlyChartData = formatOccupancyYearlyData();
  const occupancyMonthlyChartData = formatOccupancyMonthlyData();

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

      {/* Revenue Analytics Tab */}
      {tabValue === 0 && (
        <RevenueAnalyticsTab
          loading={loading.revenue}
          selectedYear={revenueSelectedYear}
          selectedHotelName={selectedHotelName}
          yearlyData={revenueYearlyChartData}
          monthlyData={revenueMonthlyChartData}
          onYearClick={handleRevenueYearClick}
          onBackClick={handleBackToRevenueYearly}
          selectedHotel={selectedHotel}
        />
      )}

      {/* Occupancy Statistics Tab */}
      {tabValue === 1 && (
        <OccupancyAnalyticsTab
          loading={loading.occupancy}
          selectedYear={selectedYear}
          selectedHotelName={selectedHotelName}
          yearlyData={occupancyYearlyChartData}
          monthlyData={occupancyMonthlyChartData}
          onYearClick={handleYearClick}
          onBackClick={handleBackToYearly}
          selectedHotel={selectedHotel}
        />
      )}
    </Box>
  );
};

// Revenue Analytics Tab Component
const RevenueAnalyticsTab = ({ 
  loading, 
  selectedYear, 
  selectedHotelName, 
  yearlyData, 
  monthlyData, 
  onYearClick, 
  onBackClick,
  selectedHotel
}) => {
  const isAllHotels = selectedHotel === 'all';

  return (
    <Box>
      <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h5">
          {selectedYear 
            ? `Monthly Revenue for ${selectedYear} - ${selectedHotelName}` 
            : `Yearly Revenue Analytics - ${selectedHotelName}`}
        </Typography>
        {selectedYear && (
          <Chip 
            label="← Back to Yearly View" 
            onClick={onBackClick}
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
          <RevenueSummaryCards 
            data={selectedYear ? monthlyData : yearlyData}
            type={selectedYear ? 'monthly' : 'yearly'}
            isAllHotels={isAllHotels}
          />
          
          <Card>
            <CardContent>
              {selectedYear ? (
                monthlyData.length > 0 ? (
                  <RevenueMonthlyChart 
                    data={monthlyData} 
                    selectedYear={selectedYear} 
                    isAllHotels={isAllHotels}
                  />
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography color="text.secondary">
                      No monthly revenue data available for {selectedYear}
                    </Typography>
                  </Box>
                )
              ) : (
                yearlyData.length > 0 ? (
                  <RevenueYearlyChart 
                    data={yearlyData} 
                    onYearClick={onYearClick} 
                    isAllHotels={isAllHotels}
                  />
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography color="text.secondary">
                      No yearly revenue data available
                    </Typography>
                  </Box>
                )
              )}
            </CardContent>
          </Card>
        </>
      )}
    </Box>
  );
};

// Occupancy Analytics Tab Component
const OccupancyAnalyticsTab = ({ 
  loading, 
  selectedYear, 
  selectedHotelName, 
  yearlyData, 
  monthlyData, 
  onYearClick, 
  onBackClick,
  selectedHotel
}) => {
  const isAllHotels = selectedHotel === 'all';

  return (
    <Box>
      <Box sx={{ mb: 3, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <Typography variant="h5">
          {selectedYear 
            ? `Monthly Occupancy for ${selectedYear} - ${selectedHotelName}` 
            : `Yearly Occupancy Rate - ${selectedHotelName}`}
        </Typography>
        {selectedYear && (
          <Chip 
            label="← Back to Yearly View" 
            onClick={onBackClick}
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
            data={selectedYear ? monthlyData : yearlyData}
            type={selectedYear ? 'monthly' : 'yearly'}
            isAllHotels={selectedHotel === 'all'}
          />
          
          <Card>
            <CardContent>
              {selectedYear ? (
                monthlyData.length > 0 ? (
                  <MonthlyChart 
                    data={monthlyData} 
                    selectedYear={selectedYear} 
                    isAllHotels={isAllHotels}
                  />
                ) : (
                  <Box sx={{ textAlign: 'center', py: 4 }}>
                    <Typography color="text.secondary">
                      No monthly occupancy data available for {selectedYear}
                    </Typography>
                  </Box>
                )
              ) : (
                yearlyData.length > 0 ? (
                  <YearlyChart 
                    data={yearlyData} 
                    onYearClick={onYearClick} 
                    isAllHotels={isAllHotels}
                  />
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
  );
};

export default Analytics;