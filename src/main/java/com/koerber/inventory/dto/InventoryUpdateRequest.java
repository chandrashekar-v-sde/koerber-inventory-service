package com.koerber.inventory.dto;

import lombok.Data;

@Data
public class InventoryUpdateRequest {

	private Long orderId;

	private Long productId;

	private Integer quantity;
}
