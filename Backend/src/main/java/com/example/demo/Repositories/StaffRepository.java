package com.example.demo.Repositories;

import com.example.demo.Entities.Staff;
import com.example.demo.Entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
    List<Staff> findByHotel(Hotel hotel);
    List<Staff> findByRole(String role);
    Staff findByUsername(String username);
}