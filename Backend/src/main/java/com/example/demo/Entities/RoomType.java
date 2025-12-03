package com.example.demo.Entities;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "room_type")
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomTypeId;

    private String name;
    private String description;
    private BigDecimal baseRate;
    private Integer capacity;

    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms = new ArrayList<>();

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getBaseRate() {
        return baseRate;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public List<Room> getRooms() {
        return rooms;
    }

    public Long getRoomTypeId() {
        return roomTypeId;
    }
}