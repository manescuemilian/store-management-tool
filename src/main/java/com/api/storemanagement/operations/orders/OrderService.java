package com.api.storemanagement.operations.orders;

import com.api.storemanagement.exceptions.InsufficientQuantityException;
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

/**
 * Service that does operations on an order repository
 */
@Service
public class OrderService {
	/**
	 * Repositories for products and orders
	 */
	private final OrderRepository orderRepository;
	private final ProductRepository productRepository;

	/**
	 * Logger
	 */
	private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

	/**
	 * Constructor
	 * @param orderRepository
	 * @param productRepository
	 */
	@Autowired
	public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
		logger.info("OrderService initialized");
	}

	/**
	 * Find orders for a given username
	 * @param username - user for which to retrieve orders
	 * @return list of orders for the user
	 */
	@Transactional(readOnly = true)
	public List<Order> findOrdersByUsername(String username) {
		logger.info("Retrieving orders for user: {}", username);
		return orderRepository.findByUsername(username);
	}

	/**
	 * Create an order for a given user
	 * @param username
	 * @param items - list of items in the order
	 * @return created order
	 */
	@Transactional
	public Order createOrder(String username, List<OrderItemRequest> items) {
		Order order = new Order();
		order.setUsername(username);

		List<OrderItem> orderItems = new ArrayList<>();
		items.stream().forEach(item -> {
			Product product = productRepository.findById(item.productId()).orElseThrow(() -> new ProductNotFoundException("Product not found with id: " + item.productId() + "."));

			OrderItem orderItem = new OrderItem();
			orderItem.setProduct(product);

			if (item.quantity() > product.getQuantity()) {
				throw new InsufficientQuantityException("Cannot place order for product " + item.productId() + ". Quantity available is " + product.getQuantity() + ".");
			}

			orderItem.setQuantity(item.quantity());
			orderItem.setOrder(order);
			orderItems.add(orderItem); // Add item to the list
		});

		order.setItems(orderItems);
		return orderRepository.save(order);
	}

	/**
	 * Find an order by its id
	 * @param orderId - order ID
	 * @return retrieved order
	 */
	@Transactional(readOnly = true)
	public Order findOrderById(Long orderId) {
		logger.info("Retrieving order with ID: {}", orderId);
		return orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("Order not found with id: " + orderId));
	}

	/**
	 * Delete a given order
	 * @param orderId - order ID
	 */
	@Transactional
	public void deleteOrderById(Long orderId) {
		logger.info("Deleting order with ID: {}", orderId);
		orderRepository.deleteById(orderId);
	}

	/**
	 * Warning - delete all orders! Only for admins
	 */
	@Transactional
	public void deleteAll() {
		logger.warn("Deleting all orders!");
		orderRepository.deleteAll();
	}
}
