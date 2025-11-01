package com.coffeehouse.service;

import com.coffeehouse.entity.*;
import com.coffeehouse.repository.MenuItemRepository;
import com.coffeehouse.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MenuItemRepository menuRepository;

    public FoodOrder placeOrder(Long customerId, Long bookingId, List<Long> itemIds) {
        List<MenuItem> items = menuRepository.findAllById(itemIds);


        Customer customer = new Customer();
        customer.setId(customerId);

        CustomerBooking booking = new CustomerBooking();
        booking.setId(bookingId);

        FoodOrder order = FoodOrder.builder()
                .customer(customer)
                .booking(booking)
                .orderTime(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .items(items)
                .build();

        return orderRepository.save(order);
    }

    public List<FoodOrder> getOrdersByCustomer(Long customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    public List<FoodOrder> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<FoodOrder> updateStatus(Long orderId, String newStatus) {
        Optional<FoodOrder> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            FoodOrder order = orderOpt.get();

            OrderStatus current = order.getStatus();
            OrderStatus next;
            try {
                next = OrderStatus.valueOf(newStatus.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("❌ Invalid status value: " + newStatus);
            }

            // Optional: restrict invalid transitions
            if (!isValidTransition(current, next)) {
                throw new RuntimeException("⚠️ Invalid status transition: " + current + " → " + next);
            }

            order.setStatus(next);
            orderRepository.save(order);
            return Optional.of(order);
        }
        return Optional.empty();
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        switch (current) {
            case PENDING:
                return next == OrderStatus.PREPARING || next == OrderStatus.CANCELLED;
            case PREPARING:
                return next == OrderStatus.READY || next == OrderStatus.CANCELLED;
            case READY:
                return next == OrderStatus.SERVED;
            case SERVED:
                return next == OrderStatus.COMPLETED;
            default:
                return false;
        }
    }


    public void deleteOrder(Long orderId) {
        orderRepository.deleteById(orderId);
    }

    public Optional<FoodOrder> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    public List<FoodOrder> getOrdersByStatus(String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            return orderRepository.findByStatus(orderStatus);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid status: " + status);
        }
    }


}
