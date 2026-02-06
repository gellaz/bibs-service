package it.bibs.product;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, UUID> {

  List<Product> findAllByProductCategoriesId(UUID id);

  boolean existsByProductStockId(UUID id);
}
