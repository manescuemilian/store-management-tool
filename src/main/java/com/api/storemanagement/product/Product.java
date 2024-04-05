package com.api.storemanagement.product;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	@Column(length = 1000)
	private String description;

	@Column(nullable = false)
	private double price;

	@Column(nullable = false)
	private int quantity;

	@Column(nullable = false, updatable = false)
	@CreationTimestamp
	private LocalDateTime createdAt;

	@Column(nullable = false)
	@UpdateTimestamp
	private LocalDateTime updatedAt;

//	@PrePersist
//	void createdAt() {
//		this.createdAt = this.updatedAt = LocalDateTime.now();
//	}
//
//	@PreUpdate
//	void updatedAt() {
//		this.updatedAt = LocalDateTime.now();
//	}
}
