package com.example.demo.Repositories;

import com.example.demo.Entities.Payment;
import com.example.demo.Entities.Invoice;
import com.example.demo.Entities.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByInvoice(Invoice invoice);
    List<Payment> findByStatus(PaymentStatus status);
}