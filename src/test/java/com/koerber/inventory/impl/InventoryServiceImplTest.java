package com.koerber.inventory.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.koerber.inventory.dto.InventoryResponse;
import com.koerber.inventory.dto.InventoryUpdateRequest;
import com.koerber.inventory.dto.InventoryUpdateResponse;
import com.koerber.inventory.entity.InventoryReservation;
import com.koerber.inventory.exception.InventoryReservationNotFoundException;
import com.koerber.inventory.factory.InventoryProcessor;
import com.koerber.inventory.factory.InventoryProcessorFactory;
import com.koerber.inventory.repository.InventoryReservationRepository;
import com.koerber.inventory.service.impl.InventoryServiceImpl;

@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTest {

	@Mock
	private InventoryProcessorFactory factory;

	@Mock
	private InventoryProcessor processor;

	@Mock
	private InventoryReservationRepository reservationRepository;

	@InjectMocks
	private InventoryServiceImpl service;

	@Test
	void testGetInventory() {

		when(factory.getProcessor("expiryBasedInventoryProcessor")).thenReturn(processor);

		final InventoryResponse response = new InventoryResponse();
		response.setProductId(1001L);

		when(processor.getInventory(1001L)).thenReturn(response);

		final InventoryResponse result = service.getInventory(1001L);

		assertNotNull(result);
		verify(processor, times(1)).getInventory(1001L);
	}

	@Test
	void testUpdateInventory() {

		final InventoryUpdateRequest request = new InventoryUpdateRequest();
		request.setOrderId(5012L);
		request.setProductId(1002L);
		request.setQuantity(3);

		when(factory.getProcessor("expiryBasedInventoryProcessor")).thenReturn(processor);

		final InventoryUpdateResponse response = new InventoryUpdateResponse();
		response.setStatus("PLACED");

		when(processor.reserve(5012L, 1002L, 3)).thenReturn(response);

		final InventoryUpdateResponse result = service.updateInventory(request);

		assertEquals("PLACED", result.getStatus());
		verify(processor, times(1)).reserve(5012L, 1002L, 3);
	}

	@Test
	void testGetReservedBatchIdsSuccess() {

		final Long orderId = 5012L;

		final InventoryReservation reservation = new InventoryReservation();
		reservation.setBatchId(9L);

		when(reservationRepository.findByOrderId(orderId)).thenReturn(List.of(reservation));

		final List<Long> result = service.getReservedBatchIds(orderId);

		assertEquals(1, result.size());
		assertEquals(9L, result.get(0));
	}

	@Test
	void testGetReservedBatchIdsNotFound() {

		final Long orderId = 999L;

		when(reservationRepository.findByOrderId(orderId)).thenReturn(List.of());

		assertThrows(InventoryReservationNotFoundException.class, () -> service.getReservedBatchIds(orderId));
	}
}
