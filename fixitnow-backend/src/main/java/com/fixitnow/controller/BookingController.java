package com.fixitnow.controller;

import com.fixitnow.dto.BookingRequest;
import com.fixitnow.dto.BookingResponse;
import com.fixitnow.dto.BookingStatusUpdate;
import com.fixitnow.model.BookingStatus;
import com.fixitnow.model.User;
import com.fixitnow.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {
    
    private final BookingService bookingService;
    
    @PostMapping
    public ResponseEntity<?> createBooking(
            @AuthenticationPrincipal User customer,
            @Valid @RequestBody BookingRequest request) {
        try {
            BookingResponse booking = bookingService.createBooking(customer.getId(), request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking request created successfully");
            response.put("booking", booking);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getBooking(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        try {
            BookingResponse booking = bookingService.getBookingById(id, user.getId());
            return ResponseEntity.ok(booking);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBookingStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @Valid @RequestBody BookingStatusUpdate update) {
        try {
            BookingResponse booking = bookingService.updateBookingStatus(id, user.getId(), update);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Booking status updated successfully");
            response.put("booking", booking);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/customer/my-bookings")
    public ResponseEntity<?> getMyBookings(
            @AuthenticationPrincipal User customer,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            Sort sort = sortOrder.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BookingResponse> bookings = bookingService.getCustomerBookings(customer.getId(), status, pageable);
            
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/customer/upcoming")
    public ResponseEntity<?> getMyUpcomingBookings(@AuthenticationPrincipal User customer) {
        try {
            List<BookingResponse> bookings = bookingService.getUpcomingBookingsForCustomer(customer.getId());
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/provider/my-bookings")
    public ResponseEntity<?> getProviderBookings(
            @AuthenticationPrincipal User provider,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        try {
            Sort sort = sortOrder.equalsIgnoreCase("asc") 
                ? Sort.by(sortBy).ascending() 
                : Sort.by(sortBy).descending();
            
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<BookingResponse> bookings = bookingService.getProviderBookings(provider.getId(), status, pageable);
            
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/provider/upcoming")
    public ResponseEntity<?> getProviderUpcomingBookings(@AuthenticationPrincipal User provider) {
        try {
            List<BookingResponse> bookings = bookingService.getUpcomingBookingsForProvider(provider.getId());
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/provider/pending")
    public ResponseEntity<?> getPendingBookings(@AuthenticationPrincipal User provider) {
        try {
            Pageable pageable = PageRequest.of(0, 50, Sort.by("createdAt").descending());
            Page<BookingResponse> bookings = bookingService.getProviderBookings(
                provider.getId(), BookingStatus.PENDING, pageable);
            return ResponseEntity.ok(bookings.getContent());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
