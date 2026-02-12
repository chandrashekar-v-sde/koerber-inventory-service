package com.koerber.inventory.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.koerber.inventory.entity.InventoryReservation;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservation, Long> {

	List<InventoryReservation> findByOrderId(Long orderId);
}
