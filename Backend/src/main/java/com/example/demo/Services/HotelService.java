package com.example.demo.Services;

import com.example.demo.DTOs.HotelDTO;
import com.example.demo.Entities.Hotel;
import com.example.demo.Repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class HotelService {

    @Autowired
    private HotelRepository hotelRepository;

    public List<HotelDTO> getAllHotels() {
        List<Hotel> hotels = hotelRepository.findAllByOrderByName();
        return hotels.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public HotelDTO getHotelById(Long id) {
        Hotel hotel = hotelRepository.findById(id).orElse(null);
        return hotel != null ? convertToDTO(hotel) : null;
    }

    private HotelDTO convertToDTO(Hotel hotel) {
        return new HotelDTO(
                hotel.getHotelId(),
                hotel.getName(),
                hotel.getAddress(),
                hotel.getContactNo(),
                hotel.getRating()
        );
    }
}