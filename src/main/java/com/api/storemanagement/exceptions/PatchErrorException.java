package com.api.storemanagement.exceptions;

/**
 * Exception thrown when patch operation on a product fails
 */
public class PatchErrorException extends RuntimeException {
	public PatchErrorException(String message) {
		super(message);
	}
}
