package com.koerber.inventory.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.koerber.inventory.dto.InventoryResponse;
import com.koerber.inventory.dto.InventoryUpdateRequest;
import com.koerber.inventory.dto.InventoryUpdateResponse;
import com.koerber.inventory.service.InventoryService;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Inventory API", description = "Handles inventory operations")
public class InventoryController {

	private static final Logger logger = LogManager.getLogger(InventoryController.class);

	private final InventoryService inventoryService;

	public InventoryController(final InventoryService inventoryService) {
		this.inventoryService = inventoryService;
	}

    @Operation(summary = "Get inventory batches",
            description = "Returns inventory batches sorted by expiry date (FEFO)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory fetched successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
	@GetMapping("/{productId}")
	public ResponseEntity<InventoryResponse> getInventory(@PathVariable final Long productId) {

		logger.info("inventory check for productId={}", productId);
		return ResponseEntity.ok(inventoryService.getInventory(productId));
	}

    @Operation(summary = "Update inventory",
            description = "Reserves inventory for an order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory reserved"),
            @ApiResponse(responseCode = "400", description = "Insufficient inventory")
    })
	@PostMapping("/update")
	public ResponseEntity<InventoryUpdateResponse> updateInventory(@RequestBody final InventoryUpdateRequest request) {

		logger.info("inventory update for orderId={}, productId={}", request.getOrderId(), request.getProductId());

		return ResponseEntity.ok(inventoryService.updateInventory(request));
	}

    @Operation(summary = "Get reserved batch IDs",
            description = "Returns reserved batch IDs for a given order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory reserved batches for orderId"),
            @ApiResponse(responseCode = "400", description = "Reservation not found")
    })
	@GetMapping("/reservation/{orderId}")
	public ResponseEntity<List<Long>> getReservedBatches(@PathVariable final Long orderId) {

		logger.info("inventory reservation check orderId={}", orderId);

		return ResponseEntity.ok(inventoryService.getReservedBatchIds(orderId));
	}

}
