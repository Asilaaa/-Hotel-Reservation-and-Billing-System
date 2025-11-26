package com.example.demo.Entities;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guest")
@Data
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;

    private String name;
    private String phone;
    private String email;

    @Column(name = "id_number", unique = true)
    private String idNumber;

    private Integer loyaltyPoints = 0;

    @OneToMany(mappedBy = "guest", cascade = CascadeType.ALL)
    private List<Reservation> reservations = new ArrayList<>();

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public Integer getLoyaltyPoints() {
        return loyaltyPoints;
    }
}