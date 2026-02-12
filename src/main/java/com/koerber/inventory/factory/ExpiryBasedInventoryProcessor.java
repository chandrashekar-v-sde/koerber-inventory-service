package com.koerber.inventory.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.koerber.inventory.dto.InventoryResponse;
import com.koerber.inventory.dto.InventoryUpdateResponse;
import com.koerber.inventory.entity.Inventory;
import com.koerber.inventory.entity.InventoryReservation;
import com.koerber.inventory.exception.InsufficientInventoryException;
import com.koerber.inventory.exception.ProductNotFoundException;
import com.koerber.inventory.repository.InventoryRepository;
import com.koerber.inventory.repository.InventoryReservationRepository;

import jakarta.transaction.Transactional;

@Component
public class ExpiryBasedInventoryProcessor implements InventoryProcessor {

	private static final Logger logger = LogManager.getLogger(ExpiryBasedInventoryProcessor.class);

	private final InventoryRepository inventoryRepository;
	private final InventoryReservationRepository reservationRepository;

	public ExpiryBasedInventoryProcessor(final InventoryRepository inventoryRepository,
			final InventoryReservationRepository reservationRepository) {

		this.inventoryRepository = inventoryRepository;
		this.reservationRepository = reservationRepository;
	}

    /**
     * Retrieves inventory details for a specific product
     * @param productId Product ID
     * @return {@link InventoryResponse}
     */
	@Override
	public InventoryResponse getInventory(final Long productId) {

		logger.info("Fetching inventory for productId={}", productId);

		final List<Inventory> batches = inventoryRepository.findByProductIdOrderByExpiryDateAsc(productId);

		if (batches.isEmpty()) {
			throw new ProductNotFoundException("Product not found");
		}

		final InventoryResponse response = new InventoryResponse();
		response.setProductId(productId);
		response.setProductName(batches.get(0).getProductName());

		final List<InventoryResponse.BatchInfo> list = new ArrayList<>();

		for (final Inventory batch : batches) {

			final InventoryResponse.BatchInfo info = new InventoryResponse.BatchInfo();

			info.setBatchId(batch.getBatchId());
			info.setQuantity(batch.getQuantity());
			info.setExpiryDate(batch.getExpiryDate());

			list.add(info);
		}

		response.setBatches(list);

		return response;
	}

    /**
     * Reserves inventory for a specific order based on expiry date
     * @param orderId Order ID
     * @param productId Product ID
     * @param quantity Quantity to reserve
     * @return {@link InventoryUpdateResponse}
     */
	@Override
	@Transactional
	public InventoryUpdateResponse reserve(final Long orderId, final Long productId, final Integer quantity) {

		logger.info("Reserving inventory for orderId={}", orderId);

		final List<Inventory> batches = inventoryRepository.findByProductIdOrderByExpiryDateAsc(productId);

		if (batches.isEmpty()) {
			throw new ProductNotFoundException("Product not found");
		}

		final int totalAvailable = batches.stream().mapToInt(Inventory::getQuantity).sum();

		if (totalAvailable < quantity) {
			logger.error("Insufficient inventory for orderId={}", orderId);
			throw new InsufficientInventoryException("Insufficient inventory available");
		}

		int remaining = quantity;
		final List<Long> reservedBatchIds = new ArrayList<>();

		for (final Inventory batch : batches) {

			if (remaining <= 0) {
				break;
			}

			final int available = batch.getQuantity();
			final int reserveQty = Math.min(available, remaining);

			batch.setQuantity(available - reserveQty);
			inventoryRepository.save(batch);

			final InventoryReservation reservation = new InventoryReservation();

			reservation.setOrderId(orderId);
			reservation.setBatchId(batch.getBatchId());
			reservation.setReservedQuantity(reserveQty);

			reservationRepository.save(reservation);

			reservedBatchIds.add(batch.getBatchId());
			remaining -= reserveQty;
		}

		final InventoryUpdateResponse response = new InventoryUpdateResponse();

		response.setOrderId(orderId);
		response.setProductId(productId);
		response.setProductName(batches.get(0).getProductName());
		response.setQuantity(quantity);
		response.setStatus("PLACED");
		response.setReservedFromBatchIds(reservedBatchIds);
		response.setMessage("Order placed. Inventory reserved");

		return response;
	}
}
