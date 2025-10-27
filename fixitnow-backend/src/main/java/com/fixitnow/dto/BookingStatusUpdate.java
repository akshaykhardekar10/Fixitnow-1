package com.fixitnow.dto;

import com.fixitnow.model.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookingStatusUpdate {
    
    @NotNull(message = "Status is required")
    private BookingStatus status;
    
    private String providerNotes;
    
    private String cancellationReason;
}
