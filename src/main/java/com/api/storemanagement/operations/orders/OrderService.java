package com.api.storemanagement.operations.orders;

import com.api.storemanagement.exceptions.ProductNotFoundException;
import com.api.storemanagement.operations.products.ProductService;
import com.api.storemanagement.orders.Order;
import com.api.storemanagement.orders.OrderItem;
import com.api.storemanagement.orders.OrderRepository;
import com.api.storemanagement.product.Product;
import com.api.storemanagement.product.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	@Autowired
	public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
		logger.info("OrderService initialized");
	}

	@Transactional(readOnly = true)
	public List<Order> findOrdersByUsername(String username) {
		return orderRepository.findByUsername(username);
	}

	@Transactional
	public Order createOrder(String username, List<OrderItemRequest> items) {
		Order order = new Order();
		order.setUsername(username);

		List<OrderItem> orderItems = new ArrayList<>();
		items.stream().forEach(item -> {
			Product product = productRepository.findById(item.productId()).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + item.productId() + "."));

			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(product);
			orderItem.setQuantity(item.quantity());
			orderItem.setOrder(order);
			orderItems.add(orderItem); // Add item to the list
		});

		order.setItems(orderItems);
		return orderRepository.save(order);
	}

	@Transactional(readOnly = true)
	public Order findOrderById(Long orderId) {
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
	}

	@Transactional
	public void deleteOrderById(Long orderId) {
		orderRepository.deleteById(orderId);
	}

	@Transactional
	public void deleteAll() {
		orderRepository.deleteAll();
	}
}
