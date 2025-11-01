package com.coffeehouse.repository;

import com.coffeehouse.entity.FoodOrder;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import com.coffeehouse.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<FoodOrder, Long> {
    List<FoodOrder> findByCustomerId(Long customerId);
    List<FoodOrder> findByStatus(OrderStatus status);


}
