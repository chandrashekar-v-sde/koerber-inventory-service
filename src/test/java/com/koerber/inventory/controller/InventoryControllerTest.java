package com.koerber.inventory.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koerber.inventory.dto.InventoryResponse;
import com.koerber.inventory.dto.InventoryUpdateRequest;
import com.koerber.inventory.dto.InventoryUpdateResponse;
import com.koerber.inventory.exception.GlobalExceptionHandler;
import com.koerber.inventory.exception.ProductNotFoundException;
import com.koerber.inventory.service.InventoryService;

@WebMvcTest(controllers = InventoryController.class)
@Import(GlobalExceptionHandler.class)
class InventoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private InventoryService inventoryService;

	@Test
	void testGetInventorySuccess() throws Exception {

		final InventoryResponse response = new InventoryResponse();
		response.setProductId(1002L);
		response.setProductName("Smartphone");

		final InventoryResponse.BatchInfo batch = new InventoryResponse.BatchInfo();
		batch.setBatchId(9L);
		batch.setQuantity(29);

		response.setBatches(List.of(batch));

		Mockito.when(inventoryService.getInventory(1002L)).thenReturn(response);

		mockMvc.perform(get("/inventory/1002")).andExpect(status().isOk())
				.andExpect(jsonPath("$.productId").value(1002))
				.andExpect(jsonPath("$.productName").value("Smartphone"));
	}

	@Test
	void testGetInventoryNotFound() throws Exception {

		Mockito.when(inventoryService.getInventory(999L)).thenThrow(new ProductNotFoundException("Product not found"));

		mockMvc.perform(get("/inventory/999")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void testUpdateInventorySuccess() throws Exception {

		final InventoryUpdateRequest request = new InventoryUpdateRequest();
		request.setOrderId(5012L);
		request.setProductId(1002L);
		request.setQuantity(3);

		final InventoryUpdateResponse response = new InventoryUpdateResponse();
		response.setStatus("PLACED");

		Mockito.when(inventoryService.updateInventory(Mockito.any())).thenReturn(response);

		mockMvc.perform(post("/inventory/update").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isOk())
				.andExpect(jsonPath("$.status").value("PLACED"));
	}

	@Test
	void testUpdateInventoryFailure() throws Exception {

		final InventoryUpdateRequest request = new InventoryUpdateRequest();
		request.setOrderId(5012L);
		request.setProductId(1002L);
		request.setQuantity(3);

		Mockito.when(inventoryService.updateInventory(Mockito.any()))
				.thenThrow(new RuntimeException("Something went wrong"));

		mockMvc.perform(post("/inventory/update").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))).andExpect(status().isInternalServerError())
				.andExpect(jsonPath("$.status").value(500));
	}
}
