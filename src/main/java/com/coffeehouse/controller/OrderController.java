package com.coffeehouse.controller;

import com.coffeehouse.entity.FoodOrder;
import com.coffeehouse.entity.Customer;
import com.coffeehouse.service.OrderService;
import com.coffeehouse.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AdminService adminService;

    // ✅ Customer places order
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(@RequestBody Map<String, Object> request) {
        try {
            Long customerId = Long.parseLong(request.get("customerId").toString());
            Long bookingId = Long.parseLong(request.get("bookingId").toString());
            List<Integer> itemIds = (List<Integer>) request.get("itemIds");

            List<Long> longIds = itemIds.stream().map(Integer::longValue).toList();
            FoodOrder order = orderService.placeOrder(customerId, bookingId, longIds);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid request: " + e.getMessage()));
        }
    }

    // ✅ Get orders for a specific customer
    @GetMapping("/customer/{customerId}")
    public List<FoodOrder> getCustomerOrders(@PathVariable Long customerId) {
        return orderService.getOrdersByCustomer(customerId);
    }
    @GetMapping("/status")
    public List<FoodOrder> getOrdersByStatus(@RequestParam String status) {
        return orderService.getOrdersByStatus(status);
    }
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders(@RequestParam String email) {
        Customer admin = adminService.getCustomerByEmail(email).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access denied: Only admin can view all orders."));
        }

        List<FoodOrder> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    // ✅ Admin/Chef updates status
    @PutMapping("/status/{orderId}")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId,
                                          @RequestParam String email,
                                          @RequestParam String status) {

        Customer admin = adminService.getCustomerByEmail(email).orElse(null);
        if (admin == null || !(admin.getRole().equalsIgnoreCase("admin") || admin.getRole().equalsIgnoreCase("chef"))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Only Admin or Chef can update order status."));
        }

        return orderService.updateStatus(orderId, status)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Order not found")));
    }


    // ✅ Admin deletes order
    @DeleteMapping("/delete/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable Long orderId, @RequestParam String email) {
        Customer admin = adminService.getCustomerByEmail(email).orElse(null);
        if (admin == null || !"admin".equalsIgnoreCase(admin.getRole())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Access denied: Only admin can delete orders."));
        }

        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(Map.of("message", "Order deleted successfully"));
    }
    @GetMapping("/track/{orderId}")
    public ResponseEntity<?> trackOrder(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId)
                .<ResponseEntity<?>>map(order -> ResponseEntity.ok(Map.of(
                        "orderId", order.getId(),
                        "status", order.getStatus(),
                        "orderTime", order.getOrderTime(),
                        "items", order.getItems()
                )))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "Order not found")));
    }



}
