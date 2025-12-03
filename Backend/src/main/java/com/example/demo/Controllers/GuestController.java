package com.example.demo.Controllers;

import com.example.demo.DTOs.GuestDTO;
import com.example.demo.Entities.Guest;
import com.example.demo.Services.GuestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/guests")
@CrossOrigin(origins = "*")
public class GuestController {

    @Autowired
    private GuestService guestService;

    // ========== GET ENDPOINTS ==========

    @GetMapping
    public ResponseEntity<List<GuestDTO>> getAllGuests() {
        List<GuestDTO> guests = guestService.getAllGuests();
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/all")
    public ResponseEntity<List<GuestDTO>> getAllGuestsAlternative() {
        return getAllGuests();
    }

    @GetMapping("/{guestId}")
    public ResponseEntity<GuestDTO> getGuestById(@PathVariable Long guestId) {
        GuestDTO guest = guestService.getGuestById(guestId);
        return ResponseEntity.ok(guest);
    }

    @GetMapping("/search")
    public ResponseEntity<List<GuestDTO>> searchGuests(@RequestParam String query) {
        List<GuestDTO> guests = guestService.searchGuests(query);
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<GuestDTO> getGuestByEmail(@PathVariable String email) {
        GuestDTO guest = guestService.getGuestByEmail(email);
        return ResponseEntity.ok(guest);
    }

    @GetMapping("/id-number/{idNumber}")
    public ResponseEntity<GuestDTO> getGuestByIdNumber(@PathVariable String idNumber) {
        GuestDTO guest = guestService.getGuestByIdNumber(idNumber);
        return ResponseEntity.ok(guest);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getGuestCount() {
        Long count = guestService.getGuestCount();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/sorted/name")
    public ResponseEntity<List<GuestDTO>> getGuestsSortedByName() {
        List<GuestDTO> guests = guestService.getAllGuestsSortedByName();
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/sorted/points")
    public ResponseEntity<List<GuestDTO>> getGuestsSortedByPoints() {
        List<GuestDTO> guests = guestService.getAllGuestsSortedByPoints();
        return ResponseEntity.ok(guests);
    }

    @GetMapping("/{guestId}/info")
    public ResponseEntity<Map<String, Object>> getGuestInfo(@PathVariable Long guestId) {
        Map<String, Object> info = guestService.getGuestInfo(guestId);
        return ResponseEntity.ok(info);
    }

    // ========== POST ENDPOINTS ==========

    @PostMapping
    public ResponseEntity<GuestDTO> createGuest(@RequestBody Guest guest) {
        GuestDTO createdGuest = guestService.createGuest(guest);
        return ResponseEntity.ok(createdGuest);
    }

    @PostMapping("/clear-cache")
    public ResponseEntity<Map<String, String>> clearGuestCache() {
        guestService.clearAllGuestCaches();
        Map<String, String> response = new HashMap<>();
        response.put("message", "Guest cache cleared");
        response.put("status", "OK");
        return ResponseEntity.ok(response);
    }

    // ========== PUT ENDPOINTS ==========

    @PutMapping("/{guestId}")
    public ResponseEntity<GuestDTO> updateGuest(@PathVariable Long guestId, @RequestBody Guest guestDetails) {
        GuestDTO updatedGuest = guestService.updateGuest(guestId, guestDetails);
        return ResponseEntity.ok(updatedGuest);
    }

    // ========== PATCH ENDPOINTS (Archive/Restore) ==========

    @PatchMapping("/{guestId}/archive")
    public ResponseEntity<GuestDTO> archiveGuest(@PathVariable Long guestId) {
        GuestDTO archivedGuest = guestService.archiveGuest(guestId);
        return ResponseEntity.ok(archivedGuest);
    }

    // Optional: Restore archived guest (comment out if not implemented yet)
    // @PatchMapping("/{guestId}/restore")
    // public ResponseEntity<GuestDTO> restoreGuest(@PathVariable Long guestId) {
    //     GuestDTO restoredGuest = guestService.restoreGuest(guestId);
    //     return ResponseEntity.ok(restoredGuest);
    // }

    // ========== DELETE ENDPOINTS ==========

    @DeleteMapping("/{guestId}")
    public ResponseEntity<Void> deleteGuest(@PathVariable Long guestId) {
        guestService.deleteGuest(guestId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{guestId}/force")
    public ResponseEntity<Map<String, Object>> forceDeleteGuest(@PathVariable Long guestId) {
        System.out.println("⚠️ FORCE DELETE requested for guest: " + guestId);
        Map<String, Object> result = guestService.forceDeleteGuest(guestId);
        return ResponseEntity.ok(result);
    }
}