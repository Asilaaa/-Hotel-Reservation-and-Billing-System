package com.example.demo.DTOs;

public class HotelDTO {
    private Long hotelId;
    private String name;
    private String address;
    private String contactNo;
    private Integer rating;

    // Constructors
    public HotelDTO() {}

    public HotelDTO(Long hotelId, String name, String address, String contactNo, Integer rating) {
        this.hotelId = hotelId;
        this.name = name;
        this.address = address;
        this.contactNo = contactNo;
        this.rating = rating;
    }

    // Getters and Setters
    public Long getHotelId() { return hotelId; }
    public void setHotelId(Long hotelId) { this.hotelId = hotelId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getContactNo() { return contactNo; }
    public void setContactNo(String contactNo) { this.contactNo = contactNo; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
}