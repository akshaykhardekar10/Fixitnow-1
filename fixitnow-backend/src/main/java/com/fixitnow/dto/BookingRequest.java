package com.fixitnow.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BookingRequest {
    
    @NotNull(message = "Service listing ID is required")
    private Long serviceListingId;
    
    @NotNull(message = "Booking date is required")
    private LocalDateTime bookingDate;
    
    @NotNull(message = "Time slot is required")
    private String timeSlot; // e.g., "09:00-12:00", "14:00-17:00"
    
    private Integer durationHours;
    
    @NotNull(message = "Total price is required")
    private BigDecimal totalPrice;
    
    private String serviceLocation;
    
    private String customerNotes;
}
