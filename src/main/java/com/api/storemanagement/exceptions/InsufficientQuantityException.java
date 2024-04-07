package com.api.storemanagement.exceptions;

public class InsufficientQuantityException extends RuntimeException {
	public InsufficientQuantityException(String message) {
		super(message);
	}
}
