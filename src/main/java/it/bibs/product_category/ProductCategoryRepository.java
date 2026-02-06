package it.bibs.product_category;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {

  boolean existsByNameIgnoreCase(String name);
}
