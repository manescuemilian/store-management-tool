package com.api.storemanagement.operations.orders;

/**
 * Record encapsulating a product added to the order
 * @param productId - ID of added product
 * @param quantity - quantity of product
 */
public record OrderItemRequest(Long productId, Integer quantity) {}
