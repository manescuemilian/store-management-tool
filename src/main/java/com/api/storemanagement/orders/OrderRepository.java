package com.api.storemanagement.orders;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Order repository
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
	List<Order> findByUsername(String username);
}