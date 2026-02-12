package com.koerber.inventory.service;

import java.util.List;

import com.koerber.inventory.dto.InventoryResponse;
import com.koerber.inventory.dto.InventoryUpdateRequest;
import com.koerber.inventory.dto.InventoryUpdateResponse;

public interface InventoryService {

	InventoryResponse getInventory(Long productId);

	InventoryUpdateResponse updateInventory(InventoryUpdateRequest request);

	List<Long> getReservedBatchIds(Long orderId);

}
