package com.api.storemanagement.product;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
	Boolean existsByName(String name); //Checks if there are any records by name
}