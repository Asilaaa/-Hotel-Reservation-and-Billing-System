package com.example.demo.DTOs;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GuestDTO {
    private Long guestId;
    private String name;
    private String phone;
    private String email;
    private String idNumber;
    private Integer loyaltyPoints;

    // ✅ MANDATORY: Default constructor (no-args) for Jackson
    public GuestDTO() {
        // Empty constructor - Jackson needs this!
    }

    // Constructor 1: Without idNumber
    public GuestDTO(Long guestId, String name, String phone, String email, Integer loyaltyPoints) {
        this.guestId = guestId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.idNumber = ""; // Default empty
        this.loyaltyPoints = loyaltyPoints;
    }

    // Constructor 2: With idNumber
    public GuestDTO(Long guestId, String name, String phone, String email, String idNumber, Integer loyaltyPoints) {
        this.guestId = guestId;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.idNumber = idNumber;
        this.loyaltyPoints = loyaltyPoints;
    }

    // Constructor from Guest entity
    public GuestDTO(com.example.demo.Entities.Guest guest) {
        this.guestId = guest.getGuestId();
        this.name = guest.getName();
        this.phone = guest.getPhone();
        this.email = guest.getEmail();
        this.idNumber = guest.getIdNumber();
        this.loyaltyPoints = guest.getLoyaltyPoints();
    }

    // ✅ MANDATORY: Getters and setters for ALL fields
    public Long getGuestId() {
        return guestId;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    // Optional: toString() for debugging
    @Override
    public String toString() {
        return "GuestDTO{" +
                "guestId=" + guestId +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}