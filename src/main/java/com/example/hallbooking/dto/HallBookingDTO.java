package com.example.hallbooking.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

public class HallBookingDTO {

    private Integer bookingId;

    @NotBlank(message = "Customer name is required")
    @Size(max = 100, message = "Customer name must be at most 100 characters")
    private String customerName;

    @NotBlank(message = "Hall name is required")
    @Size(max = 100, message = "Hall name must be at most 100 characters")
    private String hallName;

    @NotNull(message = "Event date is required")
    @Future(message = "Event date must be a future date")
    private LocalDate eventDate;

    @NotNull(message = "Hall capacity is required")
    @Min(value = 51, message = "Hall capacity must be greater than 50")
    private Integer hallCapacity;

    @NotNull(message = "Rent amount is required")
    @DecimalMin(value = "0.00", inclusive = false, message = "Rent amount must be greater than 0")
    @Digits(integer = 8, fraction = 2, message = "Rent amount allows up to 8 digits and 2 decimals")
    private BigDecimal rentAmount;

    @NotBlank(message = "Booking status is required")
    @Pattern(regexp = "(?i)CONFIRMED|PENDING|CANCELLED",
             message = "Booking status must be CONFIRMED, PENDING or CANCELLED")
    private String bookingStatus;

    public HallBookingDTO() { }

    public Integer getBookingId() { return bookingId; }
    public void setBookingId(Integer bookingId) { this.bookingId = bookingId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getHallName() { return hallName; }
    public void setHallName(String hallName) { this.hallName = hallName; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public Integer getHallCapacity() { return hallCapacity; }
    public void setHallCapacity(Integer hallCapacity) { this.hallCapacity = hallCapacity; }

    public BigDecimal getRentAmount() { return rentAmount; }
    public void setRentAmount(BigDecimal rentAmount) { this.rentAmount = rentAmount; }

    public String getBookingStatus() { return bookingStatus; }
    public void setBookingStatus(String bookingStatus) { this.bookingStatus = bookingStatus; }
}
