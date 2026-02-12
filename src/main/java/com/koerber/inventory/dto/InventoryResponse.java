package com.koerber.inventory.dto;

import java.time.LocalDate;
import java.util.List;

import lombok.Data;

@Data
public class InventoryResponse {

	private Long productId;

	private String productName;

	private List<BatchInfo> batches;

	@Data
	public static class BatchInfo {

		private Long batchId;

		private Integer quantity;

		private LocalDate expiryDate;
	}
}
