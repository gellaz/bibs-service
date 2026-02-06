package it.bibs.product_stock;

import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import it.bibs.util.CustomCollectors;

@Service
@RequiredArgsConstructor
public class ProductStockService {

  private final ProductStockRepository productStockRepository;

  public Map<UUID, UUID> getProductStockValues() {
    return productStockRepository.findAll(Sort.by("id")).stream()
        .collect(CustomCollectors.toSortedMap(ProductStock::getId, ProductStock::getId));
  }
}
