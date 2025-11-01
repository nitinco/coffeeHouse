package com.coffeehouse.service;

import com.coffeehouse.entity.CustomerBooking;
import com.coffeehouse.entity.Customer;
import com.coffeehouse.repository.BookingRepository;
import com.coffeehouse.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private CustomerRepository customerRepository;

    public boolean isTableAvailable(int tableNumber, LocalDateTime bookingTime) {
        return !bookingRepository.existsByTableNumberAndBookingTime(tableNumber, bookingTime);
    }

    // Book a table
    public CustomerBooking bookTable(Long customerId, int tableNumber, LocalDateTime bookingTime) {
        // Check if table is already booked at that time
        Optional<CustomerBooking> existingBooking = bookingRepository.findByTableNumberAndBookingTime(tableNumber, bookingTime);
        if (existingBooking.isPresent()) {
            throw new RuntimeException("❌ Table " + tableNumber + " is already booked at " + bookingTime);
        }
        if (!isTableAvailable(tableNumber, bookingTime)) {
            throw new RuntimeException("❌ Table " + tableNumber + " is already booked at " + bookingTime);
        }

        // Get customer
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("❌ Customer not found"));

        CustomerBooking booking = CustomerBooking.builder()
                .customer(customer)
                .tableNumber(tableNumber)
                .bookingTime(bookingTime)
                .status("Booked")
                .build();

        return bookingRepository.save(booking);
    }
}
