package com.example.demo.Entities;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "hotel")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hotelId;

    private String name;
    private String address;;
    private String contactNo;
    private Integer rating;

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    @OneToMany(mappedBy = "hotel", cascade = CascadeType.ALL)
    private List<Staff> staff = new ArrayList<>();

    public String getName() {
        return name;
    }

    public Long getHotelId(){
        return hotelId;
    }

    public String getAddress() {
        return address;
    }

    public String getContactNo() {
        return contactNo;
    }

    public Integer getRating() {
        return rating;
    }

    public List<Room> getRooms() {
        return rooms;
    }
    public List<Staff> getStaff() {
        return staff;
    }
    public void setHotelId(Long hotelId) {
        this.hotelId = hotelId;
    }
}
