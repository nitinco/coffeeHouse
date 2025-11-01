package com.coffeehouse.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "food_order")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private CustomerBooking booking;  // ✅ Actual relationship

    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;  // ✅ Direct link to customer

    private LocalDateTime orderTime;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @ManyToMany
    @JoinTable(
            name = "order_items",
            joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "menu_item_id")
    )
    private List<MenuItem> items;
}
