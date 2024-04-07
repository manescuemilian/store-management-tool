package com.api.storemanagement.operations.orders;

import java.util.List;

public record OrderRequest(List<OrderItemRequest> items) {}
