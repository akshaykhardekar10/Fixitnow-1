package com.fixitnow.dto;

import com.fixitnow.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    
    private Long id;
    private Long serviceListingId;
    private String serviceTitle;
    private String serviceCategoryName;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private Long providerId;
    private String providerName;
    private String providerEmail;
    private LocalDateTime bookingDate;
    private String timeSlot;
    private Integer durationHours;
    private BookingStatus status;
    private BigDecimal totalPrice;
    private String serviceLocation;
    private String customerNotes;
    private String providerNotes;
    private String cancellationReason;
    private Long cancelledBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
