package com.example.hallbooking.service;

import com.example.hallbooking.dto.HallBookingDTO;
import com.example.hallbooking.entity.MarriageHallBooking;
import com.example.hallbooking.exception.BookingConflictException;
import com.example.hallbooking.exception.ResourceNotFoundException;
import com.example.hallbooking.repository.MarriageHallRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class MarriageHallServiceImpl implements MarriageHallService {

    private static final Set<String> VALID_STATUS = Set.of("CONFIRMED", "PENDING", "CANCELLED");

    private final MarriageHallRepository repository;

    public MarriageHallServiceImpl(MarriageHallRepository repository) {
        this.repository = repository;
    }

    // ---------- CRUD ----------

    @Override
    public HallBookingDTO addBooking(HallBookingDTO dto) {
        validate(dto);
        checkDoubleBooking(dto, -1);
        MarriageHallBooking entity = toEntity(dto, new MarriageHallBooking());
        return toDto(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallBookingDTO> getAllBookings() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public HallBookingDTO getBookingById(Integer id) {
        return toDto(find(id));
    }

    @Override
    public HallBookingDTO updateBooking(Integer id, HallBookingDTO dto) {
        MarriageHallBooking existing = find(id);
        validate(dto);
        checkDoubleBooking(dto, id);
        return toDto(repository.save(toEntity(dto, existing)));
    }

    @Override
    public void cancelBooking(Integer id) {
        repository.delete(find(id));
    }

    // ---------- Reports ----------

    @Override
    @Transactional(readOnly = true)
    public List<HallBookingDTO> reportByDates(LocalDate start, LocalDate end) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start date must not be after end date");
        }
        return repository.findByEventDateBetween(start, end).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallBookingDTO> reportByCapacity(Integer minCapacity) {
        if (minCapacity == null || minCapacity < 0) {
            throw new IllegalArgumentException("minCapacity must be zero or more");
        }
        return repository.findByHallCapacityGreaterThanEqual(minCapacity).stream().map(this::toDto).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<HallBookingDTO> reportByStatus(String status) {
        if (status == null || !VALID_STATUS.contains(status.trim().toUpperCase())) {
            throw new IllegalArgumentException("Status must be CONFIRMED, PENDING or CANCELLED");
        }
        return repository.findByBookingStatusIgnoreCase(status.trim()).stream().map(this::toDto).toList();
    }

    // ---------- Business rules ----------

    /** Second line of defence: the same rules as the DTO annotations. */
    private void validate(HallBookingDTO dto) {
        if (dto.getEventDate() == null || !dto.getEventDate().isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Event date must be a future date");
        }
        if (dto.getHallCapacity() == null || dto.getHallCapacity() <= 50) {
            throw new IllegalArgumentException("Hall capacity must be greater than 50");
        }
        if (dto.getRentAmount() == null || dto.getRentAmount().signum() <= 0) {
            throw new IllegalArgumentException("Rent amount must be greater than 0");
        }
    }

    /** Same hall + same date + not cancelled => conflict. A CANCELLED booking never blocks. */
    private void checkDoubleBooking(HallBookingDTO dto, Integer excludeId) {
        if ("CANCELLED".equalsIgnoreCase(dto.getBookingStatus())) {
            return;
        }
        if (repository.existsConflict(dto.getHallName().trim(), dto.getEventDate(), excludeId)) {
            throw new BookingConflictException("Hall '" + dto.getHallName().trim()
                    + "' is already booked on " + dto.getEventDate());
        }
    }

    private MarriageHallBooking find(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id " + id));
    }

    // ---------- Mapping ----------

    private MarriageHallBooking toEntity(HallBookingDTO dto, MarriageHallBooking e) {
        e.setCustomerName(dto.getCustomerName().trim());
        e.setHallName(dto.getHallName().trim());
        e.setEventDate(dto.getEventDate());
        e.setHallCapacity(dto.getHallCapacity());
        e.setRentAmount(dto.getRentAmount());
        e.setBookingStatus(dto.getBookingStatus().trim().toUpperCase());
        return e;
    }

    private HallBookingDTO toDto(MarriageHallBooking e) {
        HallBookingDTO dto = new HallBookingDTO();
        dto.setBookingId(e.getBookingId());
        dto.setCustomerName(e.getCustomerName());
        dto.setHallName(e.getHallName());
        dto.setEventDate(e.getEventDate());
        dto.setHallCapacity(e.getHallCapacity());
        dto.setRentAmount(e.getRentAmount());
        dto.setBookingStatus(e.getBookingStatus());
        return dto;
    }
}
