package it.bibs.product_stock;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductStockRepository extends JpaRepository<ProductStock, UUID> {}
