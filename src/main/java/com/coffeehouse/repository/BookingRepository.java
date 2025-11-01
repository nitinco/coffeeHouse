package com.coffeehouse.repository;

import com.coffeehouse.entity.CustomerBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<CustomerBooking, Long> {

    // Check if a table is already booked at a specific time
    Optional<CustomerBooking> findByTableNumberAndBookingTime(int tableNumber, LocalDateTime bookingTime);
    boolean existsByTableNumberAndBookingTime(int tableNumber, LocalDateTime bookingTime);
}
