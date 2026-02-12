package com.koerber.inventory.factory;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class InventoryProcessorFactory {

	private static final Logger logger = LogManager.getLogger(InventoryProcessorFactory.class);

	private final Map<String, InventoryProcessor> processors;

	public InventoryProcessorFactory(final Map<String, InventoryProcessor> processors) {
		this.processors = processors;
	}

	public InventoryProcessor getProcessor(final String type) {

		logger.info("Getting processor for type={}", type);

		final InventoryProcessor processor = processors.get(type);

		if (processor == null) {
			logger.error("Invalid processor type={}", type);
			throw new IllegalArgumentException("Invalid processor type");
		}

		return processor;
	}
}
