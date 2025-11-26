package com.example.demo.Entities;
import jakarta.persistence.*;

@Entity
@Table(name = "staff")
public class Staff {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffId;

    private String name;
    private String role;
    private String username;
    private String password;

    @ManyToOne
    @JoinColumn(name = "hotel_id")
    private Hotel hotel;
}
