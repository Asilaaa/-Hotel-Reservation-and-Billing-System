package com.example.demo.Entities;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "guest")
@Data
@Setter
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long guestId;

    private String name;
    private String phone;
    private String email;

    @Column(name = "archived")
    private Boolean archived = false;

    public Boolean isArchived() { return archived; }
    public void setArchived(Boolean archived) { this.archived = archived; }

    @Column(name = "id_number", unique = true)
    private String idNumber;

    private Integer loyaltyPoints = 0;

    @OneToMany(mappedBy = "guest",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
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

    public Long getGuestId() {
        return guestId;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public void setLoyaltyPoints(Integer loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }

    public void setReservations(List<Reservation> reservations) {
        this.reservations = reservations;
    }

    public void setGuestId(Long guestId) {
        this.guestId = guestId;
    }

    public void addReservation(Reservation reservation) {
        reservations.add(reservation);
        reservation.setGuest(this);
    }

    public void removeReservation(Reservation reservation) {
        reservations.remove(reservation);
        reservation.setGuest(null);
    }
}