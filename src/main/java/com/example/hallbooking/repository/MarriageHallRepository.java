package com.example.hallbooking.repository;

import com.example.hallbooking.entity.MarriageHallBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface MarriageHallRepository extends JpaRepository<MarriageHallBooking, Integer> {

    List<MarriageHallBooking> findByEventDateBetween(LocalDate start, LocalDate end);

    List<MarriageHallBooking> findByHallCapacityGreaterThanEqual(Integer capacity);

    List<MarriageHallBooking> findByBookingStatusIgnoreCase(String status);

    /**
     * True when the same hall already has a non-cancelled booking on that date.
     * excludeId is the booking being updated (use -1 for a new booking).
     */
    @Query("select count(b) > 0 from MarriageHallBooking b " +
           "where lower(b.hallName) = lower(:hallName) " +
           "and b.eventDate = :eventDate " +
           "and upper(b.bookingStatus) <> 'CANCELLED' " +
           "and b.bookingId <> :excludeId")
    boolean existsConflict(@Param("hallName") String hallName,
                           @Param("eventDate") LocalDate eventDate,
                           @Param("excludeId") Integer excludeId);
}
