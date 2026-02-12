package com.koerber.inventory.factory;

import com.koerber.inventory.dto.InventoryResponse;
import com.koerber.inventory.dto.InventoryUpdateResponse;

public interface InventoryProcessor {

	InventoryResponse getInventory(Long productId);

	InventoryUpdateResponse reserve(Long orderId, Long productId, Integer quantity);
}
