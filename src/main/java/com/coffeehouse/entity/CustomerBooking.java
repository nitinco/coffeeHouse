package com.coffeehouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<FoodOrder> orders;

    @ManyToOne // Many bookings can belong to one customer
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(nullable = false)
    private int tableNumber;

    @Column(nullable = false)
    private LocalDateTime bookingTime;

    private String status; // optional: e.g., "Booked", "Cancelled"
}
