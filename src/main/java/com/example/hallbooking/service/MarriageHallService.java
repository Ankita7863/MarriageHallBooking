package com.example.hallbooking.service;

import com.example.hallbooking.dto.HallBookingDTO;

import java.time.LocalDate;
import java.util.List;

public interface MarriageHallService {
    HallBookingDTO addBooking(HallBookingDTO dto);
    List<HallBookingDTO> getAllBookings();
    HallBookingDTO getBookingById(Integer id);
    HallBookingDTO updateBooking(Integer id, HallBookingDTO dto);
    void cancelBooking(Integer id);

    List<HallBookingDTO> reportByDates(LocalDate start, LocalDate end);
    List<HallBookingDTO> reportByCapacity(Integer minCapacity);
    List<HallBookingDTO> reportByStatus(String status);
}
