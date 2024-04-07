package com.api.storemanagement.operations.orders;

import java.util.List;

/**
 * Record encapsulating a request for creating an order
 * @param items - list of order items
 */
public record OrderRequest(List<OrderItemRequest> items) {}
