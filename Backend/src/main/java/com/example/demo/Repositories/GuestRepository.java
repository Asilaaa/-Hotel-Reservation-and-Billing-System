package com.example.demo.Repositories;

import com.example.demo.Entities.Guest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuestRepository extends JpaRepository<Guest, Long> {

    // ========== DEFAULT SPRING DATA JPA METHODS ==========

    // These use default implementations from JpaRepository:
    // - save(), saveAll()
    // - findById(), findAll()
    // - delete(), deleteAll(), etc.

    // ========== CUSTOM QUERY METHODS ==========

    /**
     * Find guest by ID number (case-sensitive exact match)
     */
    Optional<Guest> findByIdNumber(String idNumber);

    /**
     * Find guests with loyalty points greater than or equal to given value
     */
    List<Guest> findByLoyaltyPointsGreaterThanEqual(Integer points);

    // ========== CUSTOM JPQL QUERIES ==========

    /**
     * Custom implementation of findAll() that excludes archived guests
     * Overrides the default findAll() from JpaRepository
     */
    @Query("SELECT g FROM Guest g WHERE g.archived IS NULL OR g.archived = false")
    @Override
    List<Guest> findAll();

    /**
     * Find guest by email (excludes archived guests)
     */
    @Query("SELECT g FROM Guest g WHERE g.email = :email AND (g.archived IS NULL OR g.archived = false)")
    Optional<Guest> findByEmail(@Param("email") String email);

    /**
     * Search guests by name, email, phone, or ID number (excludes archived guests)
     * Case-insensitive for name and email, case-sensitive for phone and ID number
     */
    @Query("SELECT g FROM Guest g WHERE " +
            "(LOWER(g.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(g.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "g.phone LIKE CONCAT('%', :query, '%') OR " +
            "g.idNumber LIKE CONCAT('%', :query, '%')) AND " +
            "(g.archived IS NULL OR g.archived = false)")
    List<Guest> searchGuests(@Param("query") String query);

    /**
     * Find all non-archived guests sorted by name ascending
     */
    @Query("SELECT g FROM Guest g WHERE g.archived IS NULL OR g.archived = false ORDER BY g.name ASC")
    List<Guest> findAllByOrderByNameAsc();

    /**
     * Find all non-archived guests sorted by loyalty points descending
     * Note: You also have a derived query method with same name below
     * This JPQL version ensures archived guests are excluded
     */
    @Query("SELECT g FROM Guest g WHERE g.archived IS NULL OR g.archived = false ORDER BY g.loyaltyPoints DESC")
    List<Guest> findAllByOrderByLoyaltyPointsDesc();

    /**
     * Find all archived guests (admin only)
     */
    @Query("SELECT g FROM Guest g WHERE g.archived = true")
    List<Guest> findAllArchived();

    /**
     * Load guest with ALL relationships (for force delete operations)
     * Uses JOIN FETCH to avoid N+1 query problem
     */
    @Query("SELECT DISTINCT g FROM Guest g " +
            "LEFT JOIN FETCH g.reservations r " +
            "LEFT JOIN FETCH r.reservationRooms rr " +
            "LEFT JOIN FETCH rr.room rm " +
            "LEFT JOIN FETCH rm.roomType rt " +
            "LEFT JOIN FETCH rm.hotel h " +
            "WHERE g.guestId = :guestId")
    Optional<Guest> findByIdWithAllRelationships(@Param("guestId") Long guestId);

    // ========== DERIVED QUERY METHODS (Spring Data JPA auto-implements) ==========
    // These work alongside the JPQL queries above

    /**
     * Spring Data JPA will implement this automatically
     * Returns ALL guests (including archived) - use with caution
     */
    // ========== ADDITIONAL HELPER METHODS ==========

    /**
     * Check if email exists (excluding archived guests)
     */
    @Query("SELECT COUNT(g) > 0 FROM Guest g WHERE g.email = :email AND (g.archived IS NULL OR g.archived = false)")
    boolean existsByEmailAndNotArchived(@Param("email") String email);

    /**
     * Check if ID number exists (excluding archived guests)
     */
    @Query("SELECT COUNT(g) > 0 FROM Guest g WHERE g.idNumber = :idNumber AND (g.archived IS NULL OR g.archived = false)")
    boolean existsByIdNumberAndNotArchived(@Param("idNumber") String idNumber);

    /**
     * Count non-archived guests
     */
    @Query("SELECT COUNT(g) FROM Guest g WHERE g.archived IS NULL OR g.archived = false")
    long countActive();

    /**
     * Count archived guests
     */
    @Query("SELECT COUNT(g) FROM Guest g WHERE g.archived = true")
    long countArchived();
}