package com.example.demo.Controllers;

import com.example.demo.Entities.Hotel;
import com.example.demo.Repositories.HotelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class HotelController {

    @Autowired
    private HotelRepository hotelRepository;

    @GetMapping("/hotels")
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    @GetMapping("/hotels/{id}")
    public Hotel getHotelById(@PathVariable Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found with id: " + id));
    }
}