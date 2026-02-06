package it.bibs.order_fulfillment;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderFulfillmentRepository extends JpaRepository<OrderFulfillment, UUID> {}
