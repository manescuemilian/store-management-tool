package com.api.storemanagement.operations.orders;

import com.api.storemanagement.orders.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
	private final OrderService orderService;

	@Autowired
	public OrderController(OrderService orderService) {
		this.orderService = orderService;
	}

	@PostMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Order> createOrder(@RequestBody OrderRequest request, Principal principal) {
		var items = request.items();
		Order createdOrder = orderService.createOrder(principal.getName(), items);
		return ResponseEntity.ok(createdOrder);
	}

	@GetMapping
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<List<Order>> getUserOrders(Principal principal) {
		// Retrieve orders by the username of the logged-in user
		List<Order> orders = orderService.findOrdersByUsername(principal.getName());
		return ResponseEntity.ok(orders);
	}

	@GetMapping("/{orderId}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Order> getOrderById(@PathVariable Long orderId, Principal principal) {
		Order order = orderService.findOrderById(orderId);
		// Check if the order belongs to the requesting user
		if (!order.getUsername().equals(principal.getName())) {
			return ResponseEntity.status(403).build();
		}
		return ResponseEntity.ok(order);
	}

	@DeleteMapping("/{orderId}")
	@PreAuthorize("hasRole('USER')")
	public ResponseEntity<Void> deleteOrder(@PathVariable Long orderId, Principal principal) {
		Order order = orderService.findOrderById(orderId);
		// Check if the requesting user is allowed to delete this order
		if (!order.getUsername().equals(principal.getName())) {
			return ResponseEntity.status(403).build();
		}
		orderService.deleteOrderById(orderId);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/admin/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<String> deleteAll() {
		orderService.deleteAll();
		return ResponseEntity.ok("Deleted all orders");
	}
}
