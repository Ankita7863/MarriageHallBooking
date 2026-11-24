package com.example.hallbooking.controller;

import com.example.hallbooking.dto.HallBookingDTO;
import com.example.hallbooking.service.MarriageHallService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/hall-bookings")
public class MarriageHallController {

    private final MarriageHallService service;

    public MarriageHallController(MarriageHallService service) {
        this.service = service;
    }

    /** HallBookAdd - 201 */
    @PostMapping
    public ResponseEntity<HallBookingDTO> add(@Valid @RequestBody HallBookingDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.addBooking(dto));
    }

    /** HallBookDisplay - 200 */
    @GetMapping
    public ResponseEntity<List<HallBookingDTO>> display() {
        return ResponseEntity.ok(service.getAllBookings());
    }

    /** HallBookGetId - 200 */
    @GetMapping("/{id}")
    public ResponseEntity<HallBookingDTO> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.getBookingById(id));
    }

    /** HallBookUpdate - 200 */
    @PutMapping("/{id}")
    public ResponseEntity<HallBookingDTO> update(@PathVariable Integer id,
                                                 @Valid @RequestBody HallBookingDTO dto) {
        return ResponseEntity.ok(service.updateBooking(id, dto));
    }

    /** HallBookCancel - 204 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancel(@PathVariable Integer id) {
        service.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }

    /** Rep: Dates - 200 */
    @GetMapping("/reports/dates")
    public ResponseEntity<List<HallBookingDTO>> reportDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate end) {
        return ResponseEntity.ok(service.reportByDates(start, end));
    }

    /** Rep: Capacity - 200 */
    @GetMapping("/reports/capacity")
    public ResponseEntity<List<HallBookingDTO>> reportCapacity(@RequestParam Integer minCapacity) {
        return ResponseEntity.ok(service.reportByCapacity(minCapacity));
    }

    /** Rep: Status - 200 */
    @GetMapping("/reports/status")
    public ResponseEntity<List<HallBookingDTO>> reportStatus(@RequestParam String status) {
        return ResponseEntity.ok(service.reportByStatus(status));
    }
}
