package com.example.hallbooking.config;

import com.example.hallbooking.entity.MarriageHallBooking;
import com.example.hallbooking.repository.MarriageHallRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Inserts sample rows only when the table is empty (first run). */
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner seedData(MarriageHallRepository repo) {
        return args -> {
            if (repo.count() > 0) {
                return;
            }
            LocalDate today = LocalDate.now();
            repo.save(row("Ravi Kumar", "Royal Palace Hall", today.plusDays(15), 400, "85000.00", "CONFIRMED"));
            repo.save(row("Sneha Shetty", "Grand Orchid Banquet", today.plusDays(20), 250, "60000.00", "CONFIRMED"));
            repo.save(row("Mohammed Faiz", "Sunrise Convention Hall", today.plusDays(30), 600, "120000.00", "PENDING"));
            repo.save(row("Anita Rao", "Lotus Garden Hall", today.plusDays(45), 120, "35000.00", "CONFIRMED"));
            repo.save(row("Prakash Naik", "Royal Palace Hall", today.plusDays(60), 400, "85000.00", "CANCELLED"));
        };
    }

    private MarriageHallBooking row(String customer, String hall, LocalDate date,
                                    int capacity, String rent, String status) {
        MarriageHallBooking b = new MarriageHallBooking();
        b.setCustomerName(customer);
        b.setHallName(hall);
        b.setEventDate(date);
        b.setHallCapacity(capacity);
        b.setRentAmount(new BigDecimal(rent));
        b.setBookingStatus(status);
        return b;
    }
}
