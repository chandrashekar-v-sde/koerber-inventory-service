package com.koerber.inventory.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.koerber.inventory.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(ProductNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleNotFound(final ProductNotFoundException ex) {

		logger.error("Product not found: {}", ex.getMessage());

		return new ResponseEntity<>(new ErrorResponse(404, ex.getMessage()), HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InsufficientInventoryException.class)
	public ResponseEntity<ErrorResponse> handleInventoryError(final InsufficientInventoryException ex) {

		logger.error("Inventory error: {}", ex.getMessage());

		return new ResponseEntity<>(new ErrorResponse(400, ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InventoryReservationNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleInventoryReservationNotFoundError(
			final InventoryReservationNotFoundException ex) {

		logger.error("Inventory Reservation Not Found error: {}", ex.getMessage());

		return new ResponseEntity<>(new ErrorResponse(400, ex.getMessage()), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGeneric(final Exception ex) {

		logger.error("Internal error", ex);

		return new ResponseEntity<>(new ErrorResponse(500, "Something went wrong"), HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
