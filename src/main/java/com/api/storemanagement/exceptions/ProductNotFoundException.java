package com.api.storemanagement.exceptions;

/**
 * Exception thrown when a product cannot be found
 */
public class ProductNotFoundException extends RuntimeException {
	public ProductNotFoundException(String message) {
		super(message);
	}
}
