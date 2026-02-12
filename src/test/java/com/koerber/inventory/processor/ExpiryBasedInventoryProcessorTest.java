package com.koerber.inventory.processor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.koerber.inventory.dto.InventoryUpdateResponse;
import com.koerber.inventory.entity.Inventory;
import com.koerber.inventory.entity.InventoryReservation;
import com.koerber.inventory.exception.InsufficientInventoryException;
import com.koerber.inventory.factory.ExpiryBasedInventoryProcessor;
import com.koerber.inventory.repository.InventoryRepository;
import com.koerber.inventory.repository.InventoryReservationRepository;

@ExtendWith(MockitoExtension.class)
class ExpiryBasedInventoryProcessorTest {

	@Mock
	private InventoryRepository inventoryRepository;

	@Mock
	private InventoryReservationRepository reservationRepository;

	@InjectMocks
	private ExpiryBasedInventoryProcessor processor;

	@Test
	void testReserveSuccess() {

		final Inventory batch = new Inventory();
		batch.setBatchId(9L);
		batch.setProductId(1002L);
		batch.setProductName("Smartphone");
		batch.setQuantity(10);
		batch.setExpiryDate(LocalDate.now());

		when(inventoryRepository.findByProductIdOrderByExpiryDateAsc(1002L)).thenReturn(List.of(batch));

		final InventoryUpdateResponse response = processor.reserve(5012L, 1002L, 3);

		assertEquals("PLACED", response.getStatus());
		assertEquals(1, response.getReservedFromBatchIds().size());

		verify(reservationRepository, times(1)).save(any(InventoryReservation.class));

		verify(inventoryRepository, times(1)).save(batch);
	}

	@Test
	void testReserveFailure() {

		final Inventory batch = new Inventory();
		batch.setBatchId(9L);
		batch.setProductId(1002L);
		batch.setProductName("Smartphone");
		batch.setQuantity(2);
		batch.setExpiryDate(LocalDate.now());

		when(inventoryRepository.findByProductIdOrderByExpiryDateAsc(1002L)).thenReturn(List.of(batch));

		assertThrows(InsufficientInventoryException.class, () -> processor.reserve(5012L, 1002L, 5));

		verify(reservationRepository, never()).save(any());
	}
}
