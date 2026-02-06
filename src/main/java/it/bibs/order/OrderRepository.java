package it.bibs.order;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {

  Order findFirstByStoreId(UUID id);

  Order findFirstByUserId(UUID id);
}
