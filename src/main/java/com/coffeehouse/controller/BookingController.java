package com.coffeehouse.controller;

import com.coffeehouse.entity.CustomerBooking;
import com.coffeehouse.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/book")
    public ResponseEntity<?> bookTable(@RequestBody Map<String, String> request) {
        try {
            Long customerId = Long.parseLong(request.get("customerId"));
            int tableNumber = Integer.parseInt(request.get("tableNumber"));
            LocalDateTime bookingTime = LocalDateTime.parse(request.get("bookingTime"));

            CustomerBooking booking = bookingService.bookTable(customerId, tableNumber, bookingTime);
            return ResponseEntity.ok(booking);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    @GetMapping("/availability")
    public ResponseEntity<?> checkAvailability(@RequestParam int tableNumber,
                                               @RequestParam String bookingTime) {
        try {
            LocalDateTime time = LocalDateTime.parse(bookingTime);
            boolean available = bookingService.isTableAvailable(tableNumber, time);
            return ResponseEntity.ok(Map.of(
                    "tableNumber", tableNumber,
                    "bookingTime", bookingTime,
                    "available", available
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }



}
