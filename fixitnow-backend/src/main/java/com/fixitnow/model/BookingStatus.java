package com.fixitnow.model;

public enum BookingStatus {
    PENDING,      // Booking request created, waiting for provider response
    CONFIRMED,    // Provider accepted the booking
    COMPLETED,    // Service has been completed
    CANCELLED     // Booking was cancelled by either party
}
