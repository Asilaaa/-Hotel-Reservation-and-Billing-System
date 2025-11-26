package com.example.demo.Entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stay")
public class Stay {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stayId;

    private LocalDateTime actualCheckIn;
    private LocalDateTime actualCheckOut;

    @OneToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @OneToMany(mappedBy = "stay", cascade = CascadeType.ALL)
    private List<ServiceCharge> serviceCharges = new ArrayList<>();

    @OneToOne(mappedBy = "stay")
    private Invoice invoice;
}
