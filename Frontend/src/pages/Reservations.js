import React, { useState, useEffect } from 'react';
import {
  Container,
  Typography,
  Box,
  Grid,
  Paper,
  Tabs,
  Tab,
  Button,
  Chip,
  IconButton,
  TextField,
  MenuItem,
  Select,
  FormControl,
  InputLabel,
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Alert,
  Snackbar,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Card,
  CardContent,
  Pagination,
  FormControlLabel,
  Switch,
  InputAdornment,
  Tooltip,
  CircularProgress,
} from '@mui/material';
import {
  Add as AddIcon,
  Delete as DeleteIcon,
  Login as CheckInIcon,
  Logout as CheckOutIcon,
  Hotel as HotelIcon,
  Person as PersonIcon,
  Bed as RoomIcon,
  CalendarToday as CalendarIcon,
  Search as SearchIcon,
  Refresh as RefreshIcon,
  Edit as EditIcon,
} from '@mui/icons-material';
import axios from 'axios';

// API base URL - adjust this to match your backend
const API_BASE_URL = 'http://localhost:8080/api';

// Helper function to format date for backend
const formatDateTimeForBackend = (dateTimeString) => {
  if (!dateTimeString) return null;
  
  try {
    const date = new Date(dateTimeString);
    // Format to: YYYY-MM-DDTHH:mm:ss
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    
    return `${year}-${month}-${day}T${hours}:${minutes}:00`;
  } catch (error) {
    console.error('Error formatting date:', error);
    // Return current time as fallback
    const now = new Date();
    return now.toISOString().split('.')[0]; // Remove milliseconds
  }
};

// Helper to get current time in correct format
const getCurrentDateTime = () => {
  const now = new Date();
  return now.toISOString().slice(0, 16); // YYYY-MM-DDTHH:mm format
};

function Reservations() {
  const [tabValue, setTabValue] = useState(0);
  const [allReservations, setAllReservations] = useState([]);
  const [displayedReservations, setDisplayedReservations] = useState([]);
  const [allRooms, setAllRooms] = useState([]);
  const [displayedRooms, setDisplayedRooms] = useState([]);
  const [allGuests, setAllGuests] = useState([]);
  const [displayedGuests, setDisplayedGuests] = useState([]);
  const [loading, setLoading] = useState(true);
  const [roomLoading, setRoomLoading] = useState(false);
  const [guestLoading, setGuestLoading] = useState(false);
  const [error, setError] = useState(null);
  const [success, setSuccess] = useState(null);

  // Pagination and display states
  const [page, setPage] = useState(0);
  const [guestPage, setGuestPage] = useState(0);
  const [itemsToShow, setItemsToShow] = useState(20);
  const [guestItemsToShow, setGuestItemsToShow] = useState(20);
  const [totalReservations, setTotalReservations] = useState(0);
  const [totalGuests, setTotalGuests] = useState(0);
  const [showAll, setShowAll] = useState(false);
  const [guestShowAll, setGuestShowAll] = useState(false);
  
  // Reservation filter and search states
  const [searchTerm, setSearchTerm] = useState('');
  const [statusFilter, setStatusFilter] = useState('ALL');
  const [dateFilter, setDateFilter] = useState('ALL');
  const [sortBy, setSortBy] = useState('newest');

  // Room filter states
  const [roomSearchTerm, setRoomSearchTerm] = useState('');
  const [roomStatusFilter, setRoomStatusFilter] = useState('ALL');
  const [roomTypeFilter, setRoomTypeFilter] = useState('ALL');
  const [hotelFilter, setHotelFilter] = useState('ALL');

  // Guest filter states
  const [guestSearchTerm, setGuestSearchTerm] = useState('');
  const [guestSortBy, setGuestSortBy] = useState('name');
  const [guestLoyaltyFilter, setGuestLoyaltyFilter] = useState('ALL');

  // Dialog states
  const [createDialogOpen, setCreateDialogOpen] = useState(false);
  const [checkInDialogOpen, setCheckInDialogOpen] = useState(false);
  const [checkOutDialogOpen, setCheckOutDialogOpen] = useState(false);
  const [guestDialogOpen, setGuestDialogOpen] = useState(false);
  const [editGuestDialogOpen, setEditGuestDialogOpen] = useState(false);
  
  // Form states
  const [newReservation, setNewReservation] = useState({
    guestId: '',
    roomIds: [],
    checkInDate: '',
    checkOutDate: '',
    specialRequests: ''
  });
  
  const [checkInData, setCheckInData] = useState({
    reservationId: '',
    actualCheckIn: getCurrentDateTime()
  });
  
  const [checkOutData, setCheckOutData] = useState({
    reservationId: '',
    actualCheckOut: getCurrentDateTime()
  });

  const [newGuest, setNewGuest] = useState({
    name: '',
    email: '',
    phone: '',
    idNumber: '',
    loyaltyPoints: 0
  });

  const [editingGuest, setEditingGuest] = useState(null);

  // Fetch data on component mount
  useEffect(() => {
    fetchAllReservations();
    fetchAllRooms();
    fetchAllGuests();
  }, []);

  // Update displayed reservations when filters or pagination changes
  useEffect(() => {
    if (allReservations.length > 0) {
      applyReservationFiltersAndPagination();
    }
  }, [allReservations, searchTerm, statusFilter, dateFilter, sortBy, itemsToShow, showAll, page]);

  // Update displayed rooms when room filters change
  useEffect(() => {
    if (allRooms.length > 0) {
      applyRoomFilters();
    }
  }, [allRooms, roomSearchTerm, roomStatusFilter, roomTypeFilter, hotelFilter]);

  // Update displayed guests when guest filters change
  useEffect(() => {
    if (allGuests.length > 0) {
      applyGuestFilters();
    }
  }, [allGuests, guestSearchTerm, guestSortBy, guestLoyaltyFilter, guestItemsToShow, guestShowAll, guestPage]);

  const fetchAllReservations = async () => {
    try {
      setLoading(true);
      const response = await axios.get(`${API_BASE_URL}/reservations/all`);
      setAllReservations(response.data);
      setTotalReservations(response.data.length);
      setError(null);
    } catch (err) {
      console.error('Error fetching reservations:', err);
      // Try different endpoints as fallback
      try {
        const response = await axios.get(`${API_BASE_URL}/reservations`);
        setAllReservations(response.data);
        setTotalReservations(response.data.length);
      } catch (fallbackErr) {
        console.error('Fallback also failed:', fallbackErr);
        setError('Failed to fetch reservations. Please check your API endpoints.');
      }
    } finally {
      setLoading(false);
    }
  };

  const fetchAllRooms = async () => {
    try {
      setRoomLoading(true);
      
      // Use the correct endpoint from your backend - gets ALL room statuses
      console.log('Fetching rooms from:', `${API_BASE_URL}/reservations/rooms/status`);
      const response = await axios.get(`${API_BASE_URL}/reservations/rooms/status`);
      const roomsData = response.data;
      
      console.log('Rooms data received:', roomsData);
      
      // Based on your database structure, the Room entity should have:
      // - roomId, roomNumber, status, ratePerNight
      // - hotel (with hotelName)
      // - roomType (with name, base_rate, capacity)
      const transformedRooms = roomsData.map(room => ({
        roomId: room.roomId || room.id,
        roomNumber: room.roomNumber || room.number,
        roomType: {
          name: room.roomType?.name || room.roomTypeName || 'Standard',
          capacity: room.roomType?.capacity || room.capacity || 2,
          baseRate: room.roomType?.baseRate || room.roomType?.base_rate || 100
        },
        ratePerNight: room.ratePerNight || room.rate || (room.roomType?.baseRate || 100),
        status: room.status || 'AVAILABLE',
        hotelName: room.hotel?.name || room.hotelName || 'Grand Plaza Hotel',
        bookedUntil: room.bookedUntil || null,
        hotelRating: room.hotel?.rating || 0
      }));
      
      console.log('Transformed rooms:', transformedRooms);
      setAllRooms(transformedRooms);
    } catch (err) {
      console.error('Error fetching rooms from /rooms/status:', err);
      console.error('Error details:', err.response?.data || err.message);
      
      // Try to get available rooms as fallback
      try {
        console.log('Trying fallback endpoint:', `${API_BASE_URL}/reservations/rooms/status/AVAILABLE`);
        const response = await axios.get(`${API_BASE_URL}/reservations/rooms/status/AVAILABLE`);
        const roomsData = response.data;
        console.log('Available rooms data:', roomsData);
        
        const transformedRooms = roomsData.map(room => ({
          roomId: room.roomId || room.id,
          roomNumber: room.roomNumber || room.number,
          roomType: {
            name: room.roomType?.name || room.roomTypeName || 'Standard',
            capacity: room.roomType?.capacity || room.capacity || 2,
            baseRate: room.roomType?.baseRate || room.roomType?.base_rate || 100
          },
          ratePerNight: room.ratePerNight || room.rate || (room.roomType?.baseRate || 100),
          status: 'AVAILABLE', // Since we fetched available rooms
          hotelName: room.hotel?.name || room.hotelName || 'Grand Plaza Hotel',
          bookedUntil: null,
          hotelRating: room.hotel?.rating || 0
        }));
        
        setAllRooms(transformedRooms);
      } catch (fallbackErr) {
        console.error('Fallback also failed:', fallbackErr);
        setError(`Failed to fetch rooms: ${fallbackErr.message}. Please check if the backend is running.`);
        
        // Set empty array instead of showing error
        setAllRooms([]);
      }
    } finally {
      setRoomLoading(false);
    }
  };

  const fetchAllGuests = async () => {
    try {
      setGuestLoading(true);
      // Try to fetch guests from API if endpoint exists
      const response = await axios.get(`${API_BASE_URL}/guests`);
      setAllGuests(response.data);
      setTotalGuests(response.data.length);
      setError(null);
    } catch (err) {
      console.error('Error fetching guests:', err);
      // Try alternative endpoints
      try {
        const response = await axios.get(`${API_BASE_URL}/guests/all`);
        setAllGuests(response.data);
        setTotalGuests(response.data.length);
      } catch (fallbackErr) {
        console.error('Fallback also failed:', fallbackErr);
        setError('Failed to fetch guests. Please check if the guests endpoint exists.');
        setAllGuests([]);
      }
    } finally {
      setGuestLoading(false);
    }
  };

  const applyReservationFiltersAndPagination = () => {
    let filtered = [...allReservations];

    // Apply search filter
    if (searchTerm) {
      filtered = filtered.filter(res =>
        res.guest?.name?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        res.reservationId?.toString().includes(searchTerm) ||
        res.rooms?.some(room => 
          room.roomNumber?.toLowerCase().includes(searchTerm.toLowerCase()) ||
          room.roomType?.name?.toLowerCase().includes(searchTerm.toLowerCase())
        )
      );
    }

    // Apply status filter
    if (statusFilter !== 'ALL') {
      filtered = filtered.filter(res => res.status === statusFilter);
    }

    // Apply date filter
    if (dateFilter !== 'ALL') {
      const today = new Date();
      today.setHours(0, 0, 0, 0);
      
      switch (dateFilter) {
        case 'TODAY':
          filtered = filtered.filter(res => {
            const checkInDate = new Date(res.checkInDate);
            const checkOutDate = new Date(res.checkOutDate);
            checkInDate.setHours(0, 0, 0, 0);
            checkOutDate.setHours(0, 0, 0, 0);
            return checkInDate.getTime() === today.getTime() || 
                   checkOutDate.getTime() === today.getTime();
          });
          break;
        case 'UPCOMING':
          filtered = filtered.filter(res => {
            const checkInDate = new Date(res.checkInDate);
            return checkInDate > today;
          });
          break;
        case 'PAST':
          filtered = filtered.filter(res => {
            const checkOutDate = new Date(res.checkOutDate);
            return checkOutDate < today;
          });
          break;
        case 'ACTIVE':
          filtered = filtered.filter(res => {
            const checkInDate = new Date(res.checkInDate);
            const checkOutDate = new Date(res.checkOutDate);
            return checkInDate <= today && checkOutDate >= today;
          });
          break;
      }
    }

    // Apply sorting
    switch (sortBy) {
      case 'newest':
        filtered.sort((a, b) => new Date(b.createdAt || b.reservationDate) - new Date(a.createdAt || a.reservationDate));
        break;
      case 'oldest':
        filtered.sort((a, b) => new Date(a.createdAt || a.reservationDate) - new Date(b.createdAt || b.reservationDate));
        break;
      case 'checkIn':
        filtered.sort((a, b) => new Date(a.checkInDate) - new Date(b.checkInDate));
        break;
      case 'checkOut':
        filtered.sort((a, b) => new Date(a.checkOutDate) - new Date(b.checkOutDate));
        break;
    }

    // Calculate pagination
    const startIndex = showAll ? 0 : page * itemsToShow;
    const endIndex = showAll ? filtered.length : startIndex + itemsToShow;
    const paginated = filtered.slice(startIndex, endIndex);

    setDisplayedReservations(paginated);
    setTotalReservations(filtered.length);
  };

  const applyRoomFilters = () => {
    let filtered = [...allRooms];

    // Apply room search filter
    if (roomSearchTerm) {
      filtered = filtered.filter(room =>
        (room.roomNumber?.toString() || '').toLowerCase().includes(roomSearchTerm.toLowerCase()) ||
        (room.roomType?.name?.toLowerCase() || '').includes(roomSearchTerm.toLowerCase()) ||
        (room.hotelName?.toLowerCase() || '').includes(roomSearchTerm.toLowerCase())
      );
    }

    // Apply room status filter
    if (roomStatusFilter !== 'ALL') {
      filtered = filtered.filter(room => room.status === roomStatusFilter);
    }

    // Apply room type filter
    if (roomTypeFilter !== 'ALL') {
      filtered = filtered.filter(room => room.roomType?.name === roomTypeFilter);
    }

    // Apply hotel filter
    if (hotelFilter !== 'ALL') {
      filtered = filtered.filter(room => room.hotelName === hotelFilter);
    }

    setDisplayedRooms(filtered);
  };

  const applyGuestFilters = () => {
    let filtered = [...allGuests];

    // Apply guest search filter
    if (guestSearchTerm) {
      filtered = filtered.filter(guest =>
        guest.name?.toLowerCase().includes(guestSearchTerm.toLowerCase()) ||
        guest.email?.toLowerCase().includes(guestSearchTerm.toLowerCase()) ||
        guest.phone?.includes(guestSearchTerm) ||
        guest.idNumber?.includes(guestSearchTerm)
      );
    }

    // Apply loyalty points filter
    if (guestLoyaltyFilter !== 'ALL') {
      switch (guestLoyaltyFilter) {
        case 'NONE':
          filtered = filtered.filter(guest => (!guest.loyaltyPoints && guest.loyaltyPoints !== 0) || guest.loyaltyPoints === 0);
          break;
        case 'LOW':
          filtered = filtered.filter(guest => guest.loyaltyPoints > 0 && guest.loyaltyPoints <= 100);
          break;
        case 'MEDIUM':
          filtered = filtered.filter(guest => guest.loyaltyPoints > 100 && guest.loyaltyPoints <= 500);
          break;
        case 'HIGH':
          filtered = filtered.filter(guest => guest.loyaltyPoints > 500);
          break;
      }
    }

    // Apply sorting
    switch (guestSortBy) {
      case 'name':
        filtered.sort((a, b) => a.name?.localeCompare(b.name));
        break;
      case 'name_desc':
        filtered.sort((a, b) => b.name?.localeCompare(a.name));
        break;
      case 'points':
        filtered.sort((a, b) => (b.loyaltyPoints || 0) - (a.loyaltyPoints || 0));
        break;
      case 'points_asc':
        filtered.sort((a, b) => (a.loyaltyPoints || 0) - (b.loyaltyPoints || 0));
        break;
      case 'recent':
        // Assuming guests have a createdAt field
        filtered.sort((a, b) => new Date(b.createdAt || 0) - new Date(a.createdAt || 0));
        break;
    }

    // Calculate pagination
    const startIndex = guestShowAll ? 0 : guestPage * guestItemsToShow;
    const endIndex = guestShowAll ? filtered.length : startIndex + guestItemsToShow;
    const paginated = filtered.slice(startIndex, endIndex);

    setDisplayedGuests(paginated);
    setTotalGuests(filtered.length);
  };

  const handleTabChange = (event, newValue) => {
    setTabValue(newValue);
  };

  const handleCreateReservation = async () => {
    try {
      const response = await axios.post(`${API_BASE_URL}/reservations`, newReservation);
      setSuccess('Reservation created successfully!');
      setCreateDialogOpen(false);
      fetchAllReservations();
      fetchAllRooms();
      // Reset form
      setNewReservation({
        guestId: '',
        roomIds: [],
        checkInDate: '',
        checkOutDate: '',
        specialRequests: ''
      });
    } catch (err) {
      setError('Failed to create reservation: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleCheckIn = async () => {
    try {
      // Format the date to YYYY-MM-DDTHH:mm:ss
      const actualCheckIn = formatDateTimeForBackend(checkInData.actualCheckIn);
      
      const response = await axios.post(`${API_BASE_URL}/reservations/check-in`, {
        reservationId: parseInt(checkInData.reservationId),
        actualCheckIn: actualCheckIn
      });
      
      setSuccess('Check-in successful!');
      setCheckInDialogOpen(false);
      fetchAllReservations();
      fetchAllRooms();
      setCheckInData({
        reservationId: '',
        actualCheckIn: getCurrentDateTime()
      });
    } catch (err) {
      setError('Failed to check in: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleCheckOut = async () => {
    try {
      // Format the date to YYYY-MM-DDTHH:mm:ss
      const formattedCheckOut = formatDateTimeForBackend(checkOutData.actualCheckOut);
      
      const response = await axios.post(`${API_BASE_URL}/reservations/check-out`, {
        ...checkOutData,
        actualCheckOut: formattedCheckOut
      });
      
      setSuccess('Check-out successful!');
      setCheckOutDialogOpen(false);
      fetchAllReservations();
      fetchAllRooms();
      setCheckOutData({
        reservationId: '',
        actualCheckOut: getCurrentDateTime()
      });
    } catch (err) {
      setError('Failed to check out: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleCancelReservation = async (reservationId) => {
    if (window.confirm('Are you sure you want to cancel this reservation?')) {
      try {
        await axios.delete(`${API_BASE_URL}/reservations/${reservationId}/cancel`);
        setSuccess('Reservation cancelled successfully!');
        fetchAllReservations();
        fetchAllRooms();
      } catch (err) {
        setError('Failed to cancel reservation: ' + (err.response?.data?.message || err.message));
      }
    }
  };

  const handleCreateGuest = async () => {
    try {
      // Validate required fields
      if (!newGuest.name || !newGuest.email || !newGuest.idNumber) {
        setError('Name, email, and ID number are required fields.');
        return;
      }

      // Validate email format
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(newGuest.email)) {
        setError('Please enter a valid email address.');
        return;
      }

      const response = await axios.post(`${API_BASE_URL}/guests`, newGuest);
      setSuccess('Guest created successfully!');
      setGuestDialogOpen(false);
      fetchAllGuests();
      // Reset form
      setNewGuest({
        name: '',
        email: '',
        phone: '',
        idNumber: '',
        loyaltyPoints: 0
      });
    } catch (err) {
      setError('Failed to create guest: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleUpdateGuest = async () => {
    try {
      if (!editingGuest) return;

      // Validate required fields
      if (!editingGuest.name || !editingGuest.email || !editingGuest.idNumber) {
        setError('Name, email, and ID number are required fields.');
        return;
      }

      // Validate email format
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(editingGuest.email)) {
        setError('Please enter a valid email address.');
        return;
      }

      const response = await axios.put(`${API_BASE_URL}/guests/${editingGuest.guestId}`, editingGuest);
      setSuccess('Guest updated successfully!');
      setEditGuestDialogOpen(false);
      setEditingGuest(null);
      fetchAllGuests();
    } catch (err) {
      setError('Failed to update guest: ' + (err.response?.data?.message || err.message));
    }
  };

  const handleDeleteGuest = async (guestId) => {
    if (window.confirm('Are you sure you want to delete this guest? This action cannot be undone.')) {
      try {
        await axios.delete(`${API_BASE_URL}/guests/${guestId}`);
        setSuccess('Guest deleted successfully!');
        fetchAllGuests();
      } catch (err) {
        setError('Failed to delete guest: ' + (err.response?.data?.message || err.message));
      }
    }
  };

  const handleOpenGuestDialog = () => {
    setNewGuest({
      name: '',
      email: '',
      phone: '',
      idNumber: '',
      loyaltyPoints: 0
    });
    setGuestDialogOpen(true);
  };

  const handleOpenEditGuestDialog = (guest) => {
    setEditingGuest({ ...guest });
    setEditGuestDialogOpen(true);
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'BOOKED': return 'primary';
      case 'CHECKED_IN': return 'success';
      case 'CHECKED_OUT': return 'info';
      case 'CANCELLED': return 'error';
      case 'NO_SHOW': return 'warning';
      default: return 'default';
    }
  };

  const getRoomStatusColor = (status) => {
    switch (status) {
      case 'AVAILABLE': return 'success';
      case 'OCCUPIED': return 'error';
      case 'MAINTENANCE': return 'warning';
      case 'OUT_OF_SERVICE': return 'default';
      default: return 'default';
    }
  };

  const getLoyaltyColor = (points) => {
    if (!points && points !== 0) return 'default';
    if (points === 0) return 'default';
    if (points <= 100) return 'info';
    if (points <= 500) return 'primary';
    return 'success';
  };

  const handleChangeRowsPerPage = (event) => {
    const value = parseInt(event.target.value, 10);
    setItemsToShow(value);
    setPage(0);
  };

  const handleChangeGuestRowsPerPage = (event) => {
    const value = parseInt(event.target.value, 10);
    setGuestItemsToShow(value);
    setGuestPage(0);
  };

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeGuestPage = (event, newPage) => {
    setGuestPage(newPage);
  };

  const handleShowAllToggle = () => {
    setShowAll(!showAll);
    setPage(0);
  };

  const handleGuestShowAllToggle = () => {
    setGuestShowAll(!guestShowAll);
    setGuestPage(0);
  };

  const handleRefresh = () => {
    if (tabValue === 0) {
      fetchAllReservations();
    } else if (tabValue === 1) {
      fetchAllRooms();
    } else if (tabValue === 2) {
      fetchAllGuests();
    }
  };

  const handleOpenCheckInDialog = () => {
    setCheckInData({
      reservationId: '',
      actualCheckIn: getCurrentDateTime()
    });
    setCheckInDialogOpen(true);
  };

  const handleOpenCheckOutDialog = () => {
    setCheckOutData({
      reservationId: '',
      actualCheckOut: getCurrentDateTime()
    });
    setCheckOutDialogOpen(true);
  };

  const statusOptions = ['ALL', 'BOOKED', 'CHECKED_IN', 'CHECKED_OUT', 'CANCELLED', 'NO_SHOW'];
  const dateOptions = ['ALL', 'TODAY', 'UPCOMING', 'ACTIVE', 'PAST'];
  const sortOptions = [
    { value: 'newest', label: 'Newest First' },
    { value: 'oldest', label: 'Oldest First' },
    { value: 'checkIn', label: 'Check-in Date' },
    { value: 'checkOut', label: 'Check-out Date' }
  ];

  // Room filter options - dynamically populate from room data
  const roomStatusOptions = ['ALL', 'AVAILABLE', 'OCCUPIED', 'MAINTENANCE', 'OUT_OF_SERVICE'];
  
  // Get unique room types and hotels from rooms data
  const roomTypes = ['ALL', ...new Set(allRooms
    .map(room => room.roomType?.name)
    .filter(name => name && name !== 'undefined' && name !== 'null')
  )];
  
  const hotels = ['ALL', ...new Set(allRooms
    .map(room => room.hotelName)
    .filter(name => name && name !== 'undefined' && name !== 'null')
  )];

  // Guest filter options
  const guestSortOptions = [
    { value: 'name', label: 'Name (A-Z)' },
    { value: 'name_desc', label: 'Name (Z-A)' },
    { value: 'points', label: 'Points (High to Low)' },
    { value: 'points_asc', label: 'Points (Low to High)' },
    { value: 'recent', label: 'Most Recent' }
  ];

  const loyaltyOptions = [
    { value: 'ALL', label: 'All Loyalty Levels' },
    { value: 'NONE', label: 'No Points' },
    { value: 'LOW', label: 'Low (1-100 points)' },
    { value: 'MEDIUM', label: 'Medium (101-500 points)' },
    { value: 'HIGH', label: 'High (500+ points)' }
  ];

  const itemsPerPageOptions = [10, 20, 30, 50, 100];

  // Function to format currency
  const formatCurrency = (amount) => {
    if (!amount && amount !== 0) return '$0.00';
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      minimumFractionDigits: 2
    }).format(amount);
  };

  // Function to render star rating
  const renderStarRating = (rating) => {
    return '★'.repeat(rating) + '☆'.repeat(5 - rating);
  };

  return (
    <Container maxWidth="xl">
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h3" gutterBottom>
          Hotel Reservations
        </Typography>
        <Typography variant="body1" color="text.secondary">
          Manage reservations, check-ins, check-outs, and room status
        </Typography>
      </Box>

      {/* Success/Error Messages */}
      {error && (
        <Alert severity="error" onClose={() => setError(null)} sx={{ mb: 2 }}>
          {error}
        </Alert>
      )}
      {success && (
        <Alert severity="success" onClose={() => setSuccess(null)} sx={{ mb: 2 }}>
          {success}
        </Alert>
      )}

      {/* Action Buttons - Only show reservation actions on Reservations tab */}
      {tabValue === 0 && (
        <Box sx={{ mb: 3, display: 'flex', gap: 2, flexWrap: 'wrap', justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={() => setCreateDialogOpen(true)}
              sx={{
                background: 'linear-gradient(135deg, #d8b4fe 0%, #a5b4fc 100%)',
              }}
            >
              New Reservation
            </Button>
            <Button
              variant="outlined"
              startIcon={<CheckInIcon />}
              onClick={handleOpenCheckInDialog}
            >
              Check In
            </Button>
            <Button
              variant="outlined"
              startIcon={<CheckOutIcon />}
              onClick={handleOpenCheckOutDialog}
            >
              Check Out
            </Button>
            <Button
              variant="outlined"
              startIcon={<RefreshIcon />}
              onClick={handleRefresh}
            >
              Refresh
            </Button>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Typography variant="body2" color="text.secondary">
              Total: {totalReservations} reservations
            </Typography>
          </Box>
        </Box>
      )}

      {/* Room Status Actions - Only show on Room Status tab */}
      {tabValue === 1 && (
        <Box sx={{ mb: 3, display: 'flex', gap: 2, flexWrap: 'wrap', justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
            <Button
              variant="outlined"
              startIcon={<RefreshIcon />}
              onClick={handleRefresh}
            >
              Refresh Rooms
            </Button>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Typography variant="body2" color="text.secondary">
              Total: {displayedRooms.length} rooms (out of {allRooms.length} total)
            </Typography>
          </Box>
        </Box>
      )}

      {/* Guests Actions - Only show on Guests tab */}
      {tabValue === 2 && (
        <Box sx={{ mb: 3, display: 'flex', gap: 2, flexWrap: 'wrap', justifyContent: 'space-between' }}>
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
            <Button
              variant="contained"
              startIcon={<AddIcon />}
              onClick={handleOpenGuestDialog}
              sx={{
                background: 'linear-gradient(135deg, #93c5fd 0%, #60a5fa 100%)',
              }}
            >
              Add Guest
            </Button>
            <Button
              variant="outlined"
              startIcon={<RefreshIcon />}
              onClick={handleRefresh}
            >
              Refresh Guests
            </Button>
          </Box>

          <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
            <Typography variant="body2" color="text.secondary">
              Total: {totalGuests} guests
            </Typography>
          </Box>
        </Box>
      )}

      {/* Tabs */}
      <Paper sx={{ mb: 3 }}>
        <Tabs value={tabValue} onChange={handleTabChange} sx={{ borderBottom: 1, borderColor: 'divider' }}>
          <Tab label={`Reservations (${totalReservations})`} />
          <Tab label={`Room Status (${displayedRooms.length})`} />
          <Tab label={`Guests (${totalGuests})`} />
        </Tabs>

        {/* Reservations Tab */}
        {tabValue === 0 && (
          <>
            {/* Reservation Filters */}
            <Paper sx={{ p: 2, mb: 3, mx: 3, mt: 2 }}>
              <Grid container spacing={2} alignItems="center">
                <Grid item xs={12} md={3}>
                  <TextField
                    fullWidth
                    placeholder="Search reservations..."
                    value={searchTerm}
                    onChange={(e) => setSearchTerm(e.target.value)}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <SearchIcon />
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>
                
                <Grid item xs={6} md={2}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Status</InputLabel>
                    <Select
                      value={statusFilter}
                      label="Status"
                      onChange={(e) => setStatusFilter(e.target.value)}
                    >
                      {statusOptions.map(option => (
                        <MenuItem key={option} value={option}>
                          {option === 'ALL' ? 'All Statuses' : option}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6} md={2}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Date Filter</InputLabel>
                    <Select
                      value={dateFilter}
                      label="Date Filter"
                      onChange={(e) => setDateFilter(e.target.value)}
                    >
                      {dateOptions.map(option => (
                        <MenuItem key={option} value={option}>
                          {option === 'ALL' ? 'All Dates' : option}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6} md={2}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Sort By</InputLabel>
                    <Select
                      value={sortBy}
                      label="Sort By"
                      onChange={(e) => setSortBy(e.target.value)}
                    >
                      {sortOptions.map(option => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6} md={1}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Show</InputLabel>
                    <Select
                      value={itemsToShow}
                      label="Show"
                      onChange={handleChangeRowsPerPage}
                    >
                      {itemsPerPageOptions.map(option => (
                        <MenuItem key={option} value={option}>
                          {option}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={12} md={2}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={showAll}
                        onChange={handleShowAllToggle}
                        color="primary"
                      />
                    }
                    label={showAll ? "Showing All" : "Show All"}
                  />
                </Grid>
              </Grid>
            </Paper>

            {/* Reservations Table */}
            <Box sx={{ p: 3 }}>
              {loading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
                  <CircularProgress />
                </Box>
              ) : displayedReservations.length === 0 ? (
                <Typography color="text.secondary" align="center" sx={{ p: 3 }}>
                  No reservations found
                </Typography>
              ) : (
                <>
                  <TableContainer>
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>ID</TableCell>
                          <TableCell>Guest</TableCell>
                          <TableCell>Rooms</TableCell>
                          <TableCell>Check-in</TableCell>
                          <TableCell>Check-out</TableCell>
                          <TableCell>Status</TableCell>
                          <TableCell>Actions</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {displayedReservations.map((reservation) => (
                          <TableRow key={reservation.reservationId || reservation.id} hover>
                            <TableCell>
                              <Typography variant="body2" fontWeight="bold">
                                #{reservation.reservationId || reservation.id}
                              </Typography>
                              <Typography variant="caption" color="text.secondary">
                                {reservation.createdAt || reservation.reservationDate ? 
                                  new Date(reservation.createdAt || reservation.reservationDate).toLocaleDateString() : 
                                  'N/A'}
                              </Typography>
                            </TableCell>
                            <TableCell>
                              <Box>
                                <Typography variant="body2" fontWeight="medium">
                                  {reservation.guest?.name || 'Unknown Guest'}
                                </Typography>
                                <Typography variant="caption" color="text.secondary">
                                  {reservation.guest?.email || 'No email'}
                                </Typography>
                                <Typography variant="caption" display="block" color="text.secondary">
                                  Points: {reservation.guest?.loyaltyPoints || 0}
                                </Typography>
                              </Box>
                            </TableCell>
                            <TableCell>
                              <Box sx={{ maxWidth: 200 }}>
                                {(reservation.rooms || []).map(room => (
                                  <Chip
                                    key={room.roomId || room.id}
                                    label={`${room.roomNumber || room.number} (${room.roomType?.name || room.type})`}
                                    size="small"
                                    sx={{ mr: 0.5, mb: 0.5 }}
                                    title={`Rate: ${formatCurrency(room.ratePerNight || room.rate)}/night`}
                                  />
                                ))}
                                {(!reservation.rooms || reservation.rooms.length === 0) && (
                                  <Typography variant="caption" color="text.secondary">
                                    No rooms assigned
                                  </Typography>
                                )}
                              </Box>
                            </TableCell>
                            <TableCell>
                              <Typography variant="body2">
                                {reservation.checkInDate || 'N/A'}
                              </Typography>
                              {reservation.status === 'CHECKED_IN' && (
                                <Typography variant="caption" color="success.main">
                                  Checked in
                                </Typography>
                              )}
                            </TableCell>
                            <TableCell>
                              <Typography variant="body2">
                                {reservation.checkOutDate || 'N/A'}
                              </Typography>
                              {reservation.status === 'CHECKED_OUT' && (
                                <Typography variant="caption" color="info.main">
                                  Checked out
                                </Typography>
                              )}
                            </TableCell>
                            <TableCell>
                              <Chip
                                label={reservation.status || 'UNKNOWN'}
                                color={getStatusColor(reservation.status)}
                                size="small"
                                sx={{ minWidth: 100 }}
                              />
                            </TableCell>
                            <TableCell>
                              {reservation.status === 'BOOKED' && (
                                <>
                                  <Tooltip title="Check In">
                                    <IconButton
                                      size="small"
                                      onClick={() => {
                                        setCheckInData({
                                          ...checkInData, 
                                          reservationId: reservation.reservationId || reservation.id,
                                          actualCheckIn: getCurrentDateTime()
                                        });
                                        setCheckInDialogOpen(true);
                                      }}
                                    >
                                      <CheckInIcon fontSize="small" />
                                    </IconButton>
                                  </Tooltip>
                                  <Tooltip title="Cancel">
                                    <IconButton
                                      size="small"
                                      onClick={() => handleCancelReservation(reservation.reservationId || reservation.id)}
                                    >
                                      <DeleteIcon fontSize="small" />
                                    </IconButton>
                                  </Tooltip>
                                </>
                              )}
                              {reservation.status === 'CHECKED_IN' && (
                                <Tooltip title="Check Out">
                                  <IconButton
                                    size="small"
                                    onClick={() => {
                                      setCheckOutData({
                                        ...checkOutData,
                                        reservationId: reservation.reservationId || reservation.id,
                                        actualCheckOut: getCurrentDateTime()
                                      });
                                      setCheckOutDialogOpen(true);
                                    }}
                                  >
                                    <CheckOutIcon fontSize="small" />
                                  </IconButton>
                                </Tooltip>
                              )}
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>

                  {/* Pagination Controls */}
                  {!showAll && totalReservations > itemsToShow && (
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2, p: 2 }}>
                      <Typography variant="body2" color="text.secondary">
                        Showing {Math.min(page * itemsToShow + 1, totalReservations)}-
                        {Math.min((page + 1) * itemsToShow, totalReservations)} of {totalReservations} reservations
                      </Typography>
                      
                      <Pagination
                        count={Math.ceil(totalReservations / itemsToShow)}
                        page={page + 1}
                        onChange={(event, value) => setPage(value - 1)}
                        color="primary"
                        showFirstButton
                        showLastButton
                      />

                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Typography variant="body2">Items per page:</Typography>
                        <Select
                          value={itemsToShow}
                          onChange={handleChangeRowsPerPage}
                          size="small"
                          sx={{ minWidth: 80 }}
                        >
                          {itemsPerPageOptions.map(option => (
                            <MenuItem key={option} value={option}>
                              {option}
                            </MenuItem>
                          ))}
                        </Select>
                      </Box>
                    </Box>
                  )}
                </>
              )}
            </Box>
          </>
        )}

        {/* Room Status Tab */}
        {tabValue === 1 && (
          <>
            {/* Room Filters */}
            <Paper sx={{ p: 2, mb: 3, mx: 3, mt: 2 }}>
              <Grid container spacing={2} alignItems="center">
                <Grid item xs={12} md={3}>
                  <TextField
                    fullWidth
                    placeholder="Search rooms..."
                    value={roomSearchTerm}
                    onChange={(e) => setRoomSearchTerm(e.target.value)}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <SearchIcon />
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>
                
                <Grid item xs={6} md={2}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Status</InputLabel>
                    <Select
                      value={roomStatusFilter}
                      label="Status"
                      onChange={(e) => setRoomStatusFilter(e.target.value)}
                    >
                      {roomStatusOptions.map(option => (
                        <MenuItem key={option} value={option}>
                          {option === 'ALL' ? 'All Statuses' : option}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6} md={2}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Room Type</InputLabel>
                    <Select
                      value={roomTypeFilter}
                      label="Room Type"
                      onChange={(e) => setRoomTypeFilter(e.target.value)}
                    >
                      {roomTypes.map(option => (
                        <MenuItem key={option} value={option}>
                          {option}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6} md={2}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Hotel</InputLabel>
                    <Select
                      value={hotelFilter}
                      label="Hotel"
                      onChange={(e) => setHotelFilter(e.target.value)}
                    >
                      {hotels.map(option => (
                        <MenuItem key={option} value={option}>
                          {option}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={12} md={3}>
                  <Typography variant="body2" color="text.secondary">
                    Showing {displayedRooms.length} of {allRooms.length} rooms
                  </Typography>
                </Grid>
              </Grid>
            </Paper>

            {/* Rooms Grid */}
            <Box sx={{ p: 3 }}>
              {roomLoading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
                  <CircularProgress />
                </Box>
              ) : displayedRooms.length === 0 ? (
                <Typography color="text.secondary" align="center" sx={{ p: 3 }}>
                  {allRooms.length === 0 ? 'No rooms found in the system' : 'No rooms match your filters'}
                </Typography>
              ) : (
                <Grid container spacing={3}>
                  {displayedRooms.map((room) => (
                    <Grid item xs={12} sm={6} md={4} lg={3} key={room.roomId}>
                      <Card>
                        <CardContent>
                          <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 2 }}>
                            <Typography variant="h6">
                              {room.roomNumber || 'N/A'}
                            </Typography>
                            <Chip
                              label={room.status || 'UNKNOWN'}
                              color={getRoomStatusColor(room.status)}
                              size="small"
                            />
                          </Box>
                          <Box sx={{ mb: 2 }}>
                            <Typography variant="body2" color="primary" fontWeight="medium" gutterBottom>
                              {room.roomType?.name || 'Unknown Type'}
                            </Typography>
                            <Typography variant="body2" color="text.secondary">
                              Capacity: {room.roomType?.capacity || 'N/A'} guests
                            </Typography>
                            <Typography variant="body2" color="text.secondary" gutterBottom>
                              Base Rate: {formatCurrency(room.roomType?.baseRate)}
                            </Typography>
                          </Box>
                          <Box sx={{ mb: 2 }}>
                            <Typography variant="body1" fontWeight="bold" color="primary">
                              Rate: {formatCurrency(room.ratePerNight)}/night
                            </Typography>
                            {room.ratePerNight !== room.roomType?.baseRate && (
                              <Typography variant="caption" color="text.secondary">
                                (Base rate: {formatCurrency(room.roomType?.baseRate)})
                              </Typography>
                            )}
                          </Box>
                          <Box sx={{ mb: 2 }}>
                            <Typography variant="body2" fontWeight="medium">
                              {room.hotelName || 'N/A'}
                            </Typography>
                            <Typography variant="caption" color="gold">
                              {renderStarRating(room.hotelRating || 0)}
                            </Typography>
                          </Box>
                          {room.bookedUntil && (room.status === 'OCCUPIED' || room.status === 'BOOKED') && (
                            <Typography variant="body2" color="warning.main">
                              Booked until: {room.bookedUntil}
                            </Typography>
                          )}
                          {room.status === 'AVAILABLE' && (
                            <Typography variant="body2" color="success.main">
                              ✓ Available for booking
                            </Typography>
                          )}
                          {room.status === 'MAINTENANCE' && (
                            <Typography variant="body2" color="error.main">
                              ⚠ Under maintenance
                            </Typography>
                          )}
                          {room.status === 'OUT_OF_SERVICE' && (
                            <Typography variant="body2" color="text.secondary">
                              ✗ Out of service
                            </Typography>
                          )}
                        </CardContent>
                      </Card>
                    </Grid>
                  ))}
                </Grid>
              )}
            </Box>
          </>
        )}

        {/* Guests Tab */}
        {tabValue === 2 && (
          <>
            {/* Guest Filters */}
            <Paper sx={{ p: 2, mb: 3, mx: 3, mt: 2 }}>
              <Grid container spacing={2} alignItems="center">
                <Grid item xs={12} md={3}>
                  <TextField
                    fullWidth
                    placeholder="Search guests..."
                    value={guestSearchTerm}
                    onChange={(e) => setGuestSearchTerm(e.target.value)}
                    InputProps={{
                      startAdornment: (
                        <InputAdornment position="start">
                          <SearchIcon />
                        </InputAdornment>
                      ),
                    }}
                  />
                </Grid>
                
                <Grid item xs={6} md={2}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Sort By</InputLabel>
                    <Select
                      value={guestSortBy}
                      label="Sort By"
                      onChange={(e) => setGuestSortBy(e.target.value)}
                    >
                      {guestSortOptions.map(option => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6} md={2}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Loyalty Points</InputLabel>
                    <Select
                      value={guestLoyaltyFilter}
                      label="Loyalty Points"
                      onChange={(e) => setGuestLoyaltyFilter(e.target.value)}
                    >
                      {loyaltyOptions.map(option => (
                        <MenuItem key={option.value} value={option.value}>
                          {option.label}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={6} md={1}>
                  <FormControl fullWidth size="small">
                    <InputLabel>Show</InputLabel>
                    <Select
                      value={guestItemsToShow}
                      label="Show"
                      onChange={handleChangeGuestRowsPerPage}
                    >
                      {itemsPerPageOptions.map(option => (
                        <MenuItem key={option} value={option}>
                          {option}
                        </MenuItem>
                      ))}
                    </Select>
                  </FormControl>
                </Grid>

                <Grid item xs={12} md={2}>
                  <FormControlLabel
                    control={
                      <Switch
                        checked={guestShowAll}
                        onChange={handleGuestShowAllToggle}
                        color="primary"
                      />
                    }
                    label={guestShowAll ? "Showing All" : "Show All"}
                  />
                </Grid>

                <Grid item xs={12} md={2}>
                  <Typography variant="body2" color="text.secondary" align="right">
                    {totalGuests} guests
                  </Typography>
                </Grid>
              </Grid>
            </Paper>

            {/* Guests Table */}
            <Box sx={{ p: 3 }}>
              {guestLoading ? (
                <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
                  <CircularProgress />
                </Box>
              ) : displayedGuests.length === 0 ? (
                <Typography color="text.secondary" align="center" sx={{ p: 3 }}>
                  {allGuests.length === 0 ? 'No guests found in the system' : 'No guests match your filters'}
                </Typography>
              ) : (
                <>
                  <TableContainer>
                    <Table>
                      <TableHead>
                        <TableRow>
                          <TableCell>ID</TableCell>
                          <TableCell>Name</TableCell>
                          <TableCell>Email</TableCell>
                          <TableCell>Phone</TableCell>
                          <TableCell>ID Number</TableCell>
                          <TableCell>Loyalty Points</TableCell>
                          <TableCell>Actions</TableCell>
                        </TableRow>
                      </TableHead>
                      <TableBody>
                        {displayedGuests.map((guest) => (
                          <TableRow key={guest.guestId} hover>
                            <TableCell>#{guest.guestId}</TableCell>
                            <TableCell>
                              <Typography variant="body2" fontWeight="medium">
                                {guest.name}
                              </Typography>
                            </TableCell>
                            <TableCell>{guest.email}</TableCell>
                            <TableCell>{guest.phone || 'N/A'}</TableCell>
                            <TableCell>
                              <Chip
                                label={guest.idNumber || 'N/A'}
                                size="small"
                                variant="outlined"
                              />
                            </TableCell>
                            <TableCell>
                              <Chip
                                label={guest.loyaltyPoints || 0}
                                color={getLoyaltyColor(guest.loyaltyPoints)}
                                size="small"
                              />
                            </TableCell>
                            <TableCell>
                              <Box sx={{ display: 'flex', gap: 1 }}>
                                <Tooltip title="Edit Guest">
                                  <IconButton
                                    size="small"
                                    onClick={() => handleOpenEditGuestDialog(guest)}
                                  >
                                    <EditIcon fontSize="small" />
                                  </IconButton>
                                </Tooltip>
                                <Tooltip title="Book Room">
                                  <Button
                                    size="small"
                                    startIcon={<AddIcon />}
                                    onClick={() => {
                                      setNewReservation({...newReservation, guestId: guest.guestId});
                                      setCreateDialogOpen(true);
                                    }}
                                    variant="outlined"
                                  >
                                    Book
                                  </Button>
                                </Tooltip>
                                <Tooltip title="Delete Guest">
                                  <IconButton
                                    size="small"
                                    onClick={() => handleDeleteGuest(guest.guestId)}
                                    color="error"
                                  >
                                    <DeleteIcon fontSize="small" />
                                  </IconButton>
                                </Tooltip>
                              </Box>
                            </TableCell>
                          </TableRow>
                        ))}
                      </TableBody>
                    </Table>
                  </TableContainer>

                  {/* Pagination Controls */}
                  {!guestShowAll && totalGuests > guestItemsToShow && (
                    <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mt: 2, p: 2 }}>
                      <Typography variant="body2" color="text.secondary">
                        Showing {Math.min(guestPage * guestItemsToShow + 1, totalGuests)}-
                        {Math.min((guestPage + 1) * guestItemsToShow, totalGuests)} of {totalGuests} guests
                      </Typography>
                      
                      <Pagination
                        count={Math.ceil(totalGuests / guestItemsToShow)}
                        page={guestPage + 1}
                        onChange={(event, value) => setGuestPage(value - 1)}
                        color="primary"
                        showFirstButton
                        showLastButton
                      />

                      <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                        <Typography variant="body2">Items per page:</Typography>
                        <Select
                          value={guestItemsToShow}
                          onChange={handleChangeGuestRowsPerPage}
                          size="small"
                          sx={{ minWidth: 80 }}
                        >
                          {itemsPerPageOptions.map(option => (
                            <MenuItem key={option} value={option}>
                              {option}
                            </MenuItem>
                          ))}
                        </Select>
                      </Box>
                    </Box>
                  )}
                </>
              )}
            </Box>
          </>
        )}
      </Paper>

      {/* Create Reservation Dialog */}
      <Dialog open={createDialogOpen} onClose={() => setCreateDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Create New Reservation</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <FormControl fullWidth>
              <InputLabel>Guest</InputLabel>
              <Select
                value={newReservation.guestId}
                label="Guest"
                onChange={(e) => setNewReservation({...newReservation, guestId: e.target.value})}
              >
                {allGuests.map(guest => (
                  <MenuItem key={guest.guestId} value={guest.guestId}>
                    {guest.name} ({guest.email})
                  </MenuItem>
                ))}
              </Select>
            </FormControl>
            
            <FormControl fullWidth>
              <InputLabel>Rooms</InputLabel>
              <Select
                multiple
                value={newReservation.roomIds}
                label="Rooms"
                onChange={(e) => setNewReservation({...newReservation, roomIds: e.target.value})}
              >
                {allRooms
                  .filter(room => room.status === 'AVAILABLE')
                  .map(room => (
                    <MenuItem key={room.roomId} value={room.roomId}>
                      {room.roomNumber} - {room.roomType?.name} ({room.hotelName}) - {formatCurrency(room.ratePerNight)}/night
                    </MenuItem>
                  ))}
              </Select>
            </FormControl>

            <TextField
              label="Check-in Date"
              type="date"
              value={newReservation.checkInDate}
              onChange={(e) => setNewReservation({...newReservation, checkInDate: e.target.value})}
              InputLabelProps={{ shrink: true }}
              fullWidth
              InputProps={{
                inputProps: { min: new Date().toISOString().split('T')[0] }
              }}
            />

            <TextField
              label="Check-out Date"
              type="date"
              value={newReservation.checkOutDate}
              onChange={(e) => setNewReservation({...newReservation, checkOutDate: e.target.value})}
              InputLabelProps={{ shrink: true }}
              fullWidth
              InputProps={{
                inputProps: { 
                  min: newReservation.checkInDate || new Date().toISOString().split('T')[0] 
                }
              }}
            />

            <TextField
              label="Special Requests"
              multiline
              rows={3}
              value={newReservation.specialRequests}
              onChange={(e) => setNewReservation({...newReservation, specialRequests: e.target.value})}
              fullWidth
              placeholder="Any special requests or notes..."
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCreateDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleCreateReservation} variant="contained">
            Create Reservation
          </Button>
        </DialogActions>
      </Dialog>

      {/* Check-in Dialog */}
      <Dialog open={checkInDialogOpen} onClose={() => setCheckInDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Check In Guest</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <Alert severity="info" sx={{ mb: 1 }}>
              Check-in allowed from 1 day before scheduled check-in date until check-out date.
            </Alert>
            <TextField
              label="Reservation ID"
              value={checkInData.reservationId}
              onChange={(e) => setCheckInData({...checkInData, reservationId: e.target.value})}
              fullWidth
              required
            />
            <TextField
              label="Check-in Time"
              type="datetime-local"
              value={checkInData.actualCheckIn}
              onChange={(e) => setCheckInData({...checkInData, actualCheckIn: e.target.value})}
              InputLabelProps={{ shrink: true }}
              fullWidth
              helperText="Actual check-in time (format: YYYY-MM-DDTHH:mm)"
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCheckInDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleCheckIn} variant="contained">
            Check In
          </Button>
        </DialogActions>
      </Dialog>

      {/* Check-out Dialog */}
      <Dialog open={checkOutDialogOpen} onClose={() => setCheckOutDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Check Out Guest</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <Alert severity="info" sx={{ mb: 1 }}>
              Enter the Reservation ID of the checked-in guest.
            </Alert>
            <TextField
              label="Reservation ID"
              value={checkOutData.reservationId}
              onChange={(e) => setCheckOutData({...checkOutData, reservationId: e.target.value})}
              fullWidth
              required
              helperText="Find Reservation ID from checked-in reservations list"
            />
            <TextField
              label="Check-out Time"
              type="datetime-local"
              value={checkOutData.actualCheckOut}
              onChange={(e) => setCheckOutData({...checkOutData, actualCheckOut: e.target.value})}
              InputLabelProps={{ shrink: true }}
              fullWidth
              helperText="Actual check-out time (defaults to now)"
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setCheckOutDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleCheckOut} variant="contained">
            Check Out
          </Button>
        </DialogActions>
      </Dialog>

      {/* Create Guest Dialog */}
      <Dialog open={guestDialogOpen} onClose={() => setGuestDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Add New Guest</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <Alert severity="info" sx={{ mb: 1 }}>
              Please fill in the guest information. Fields marked with * are required.
            </Alert>
            <TextField
              label="Full Name *"
              value={newGuest.name}
              onChange={(e) => setNewGuest({...newGuest, name: e.target.value})}
              fullWidth
              required
              error={!newGuest.name}
              helperText={!newGuest.name ? "Name is required" : ""}
            />
            <TextField
              label="Email *"
              type="email"
              value={newGuest.email}
              onChange={(e) => setNewGuest({...newGuest, email: e.target.value})}
              fullWidth
              required
              error={!newGuest.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(newGuest.email)}
              helperText={!newGuest.email ? "Email is required" : !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(newGuest.email) ? "Invalid email format" : ""}
            />
            <TextField
              label="ID Number *"
              value={newGuest.idNumber}
              onChange={(e) => setNewGuest({...newGuest, idNumber: e.target.value})}
              fullWidth
              required
              error={!newGuest.idNumber}
              helperText={!newGuest.idNumber ? "ID number is required" : "Must be unique"}
            />
            <TextField
              label="Phone Number"
              value={newGuest.phone}
              onChange={(e) => setNewGuest({...newGuest, phone: e.target.value})}
              fullWidth
              placeholder="+1-555-0100"
            />
            <TextField
              label="Loyalty Points"
              type="number"
              value={newGuest.loyaltyPoints}
              onChange={(e) => setNewGuest({...newGuest, loyaltyPoints: parseInt(e.target.value) || 0})}
              fullWidth
              InputProps={{
                inputProps: { min: 0 }
              }}
            />
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => setGuestDialogOpen(false)}>Cancel</Button>
          <Button onClick={handleCreateGuest} variant="contained">
            Add Guest
          </Button>
        </DialogActions>
      </Dialog>

      {/* Edit Guest Dialog */}
      <Dialog open={editGuestDialogOpen} onClose={() => setEditGuestDialogOpen(false)} maxWidth="sm" fullWidth>
        <DialogTitle>Edit Guest Information</DialogTitle>
        <DialogContent>
          <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
            <Alert severity="info" sx={{ mb: 1 }}>
              Update the guest information. Fields marked with * are required.
            </Alert>
            {editingGuest && (
              <>
                <TextField
                  label="Full Name *"
                  value={editingGuest.name || ''}
                  onChange={(e) => setEditingGuest({...editingGuest, name: e.target.value})}
                  fullWidth
                  required
                  error={!editingGuest.name}
                  helperText={!editingGuest.name ? "Name is required" : ""}
                />
                <TextField
                  label="Email *"
                  type="email"
                  value={editingGuest.email || ''}
                  onChange={(e) => setEditingGuest({...editingGuest, email: e.target.value})}
                  fullWidth
                  required
                  error={!editingGuest.email || !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(editingGuest.email)}
                  helperText={!editingGuest.email ? "Email is required" : !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(editingGuest.email) ? "Invalid email format" : ""}
                />
                <TextField
                  label="ID Number *"
                  value={editingGuest.idNumber || ''}
                  onChange={(e) => setEditingGuest({...editingGuest, idNumber: e.target.value})}
                  fullWidth
                  required
                  error={!editingGuest.idNumber}
                  helperText={!editingGuest.idNumber ? "ID number is required" : "Must be unique"}
                />
                <TextField
                  label="Phone Number"
                  value={editingGuest.phone || ''}
                  onChange={(e) => setEditingGuest({...editingGuest, phone: e.target.value})}
                  fullWidth
                  placeholder="+1-555-0100"
                />
                <TextField
                  label="Loyalty Points"
                  type="number"
                  value={editingGuest.loyaltyPoints || 0}
                  onChange={(e) => setEditingGuest({...editingGuest, loyaltyPoints: parseInt(e.target.value) || 0})}
                  fullWidth
                  InputProps={{
                    inputProps: { min: 0 }
                  }}
                />
              </>
            )}
          </Box>
        </DialogContent>
        <DialogActions>
          <Button onClick={() => {
            setEditGuestDialogOpen(false);
            setEditingGuest(null);
          }}>Cancel</Button>
          <Button onClick={handleUpdateGuest} variant="contained">
            Update Guest
          </Button>
        </DialogActions>
      </Dialog>
    </Container>
  );
}

export default Reservations;