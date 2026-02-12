package com.koerber.inventory.service.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.koerber.inventory.dto.InventoryResponse;
import com.koerber.inventory.dto.InventoryUpdateRequest;
import com.koerber.inventory.dto.InventoryUpdateResponse;
import com.koerber.inventory.entity.InventoryReservation;
import com.koerber.inventory.exception.InventoryReservationNotFoundException;
import com.koerber.inventory.factory.InventoryProcessor;
import com.koerber.inventory.factory.InventoryProcessorFactory;
import com.koerber.inventory.repository.InventoryReservationRepository;
import com.koerber.inventory.service.InventoryService;

@Service
public class InventoryServiceImpl implements InventoryService {

	private static final Logger logger = LogManager.getLogger(InventoryServiceImpl.class);

	private final InventoryProcessorFactory factory;
	private final InventoryReservationRepository reservationRepository;

	public InventoryServiceImpl(final InventoryProcessorFactory factory,
			final InventoryReservationRepository reservationRepository) {

		this.factory = factory;
		this.reservationRepository = reservationRepository;
	}

    /**
     * Fetches inventory for a given product.
     * @param productId Product ID
     * @return {@link InventoryResponse}
     */
	@Override
	public InventoryResponse getInventory(final Long productId) {

		logger.info("fetching inventory for productId={}", productId);

		final InventoryProcessor processor = factory.getProcessor("expiryBasedInventoryProcessor");

		return processor.getInventory(productId);
	}

    /**
     * Reserves inventory for a given order.
     * @param request {@link InventoryUpdateRequest}
     * @return {@link InventoryUpdateResponse}
     */
	@Override
	public InventoryUpdateResponse updateInventory(final InventoryUpdateRequest request) {

		logger.info("reserving inventory for orderId={}", request.getOrderId());

		final InventoryProcessor processor = factory.getProcessor("expiryBasedInventoryProcessor");

		return processor.reserve(request.getOrderId(), request.getProductId(), request.getQuantity());
	}

    /**
     * Returns reserved batch IDs for a given order.
     * @param orderId Order ID
     * @return List of batch IDs
     */
	@Override
	public List<Long> getReservedBatchIds(final Long orderId) {

		logger.info("Fetching reserved batches for orderId={}", orderId);

		final List<InventoryReservation> reservations = reservationRepository.findByOrderId(orderId);

		if (reservations.isEmpty()) {
			throw new InventoryReservationNotFoundException("No reservations found for orderId: " + orderId);
		}

		return reservations.stream().map(InventoryReservation::getBatchId).toList();
	}
}
