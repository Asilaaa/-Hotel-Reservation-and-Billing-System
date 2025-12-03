package com.example.demo.DTOs;

import java.math.BigDecimal;

public class RoomTypeDTO {
    private Long roomTypeId;
    private String name;
    private String description;
    private Integer capacity;
    private BigDecimal baseRate;

    // Constructors
    public RoomTypeDTO() {}

    public RoomTypeDTO(Long roomTypeId, String name, String description,
                       Integer capacity, BigDecimal baseRate) {
        this.roomTypeId = roomTypeId;
        this.name = name;
        this.description = description;
        this.capacity = capacity;
        this.baseRate = baseRate;
    }

    // Getters and Setters
    public Long getRoomTypeId() { return roomTypeId; }
    public void setRoomTypeId(Long roomTypeId) { this.roomTypeId = roomTypeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }

    public BigDecimal getBaseRate() { return baseRate; }
    public void setBaseRate(BigDecimal baseRate) { this.baseRate = baseRate; }
}