package com.api.storemanagement.product;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Product repository
 */
public interface ProductRepository extends JpaRepository<Product, Long> {
	Boolean existsByName(String name); //Checks if there are any records by name

	@Query("SELECT p FROM Product p WHERE p.price > ?1 AND p.quantity < ?2 ORDER BY p.price DESC")
	List<Product> findExpensiveLowStockProducts(double minPrice, int maxQuantity);

}