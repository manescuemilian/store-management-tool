package com.api.storemanagement.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 * Handler of exceptions thrown during the use of the REST controller
 */
@ControllerAdvice
public class StoreExceptionHandler {
	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<?> handleProductNotFoundException(ProductNotFoundException ex, WebRequest request) {
		String bodyOfResponse = ex.getMessage();
		return new ResponseEntity<>(bodyOfResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InsufficientQuantityException.class)
	public ResponseEntity<?> handleInsufficientQuantityException(InsufficientQuantityException ex, WebRequest request) {
		String bodyOfResponse = ex.getMessage();
		return new ResponseEntity<>(bodyOfResponse, HttpStatus.UNPROCESSABLE_ENTITY);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
		// Handle cases where data integrity is violated (e.g., null constraints, duplicate keys)
		String bodyOfResponse = "Data integrity violation: " + ex.getRootCause().getMessage();
		return new ResponseEntity<>(bodyOfResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalAccessException.class)
	public ResponseEntity<?> handleIllegalAccessException(IllegalAccessException ex) {
		return new ResponseEntity<>("Illegal access error", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
		// Fallback for other uncaught exceptions
		String bodyOfResponse = "An unexpected error occurred: " + ex.getMessage();
		return new ResponseEntity<>(bodyOfResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
