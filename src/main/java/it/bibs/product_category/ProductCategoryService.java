package it.bibs.product_category;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteProductCategory;
import it.bibs.util.CustomCollectors;
import it.bibs.util.NotFoundException;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProductCategoryService {

  private final ProductCategoryRepository productCategoryRepository;
  private final ApplicationEventPublisher publisher;
  private final ProductCategoryMapper productCategoryMapper;

  public List<ProductCategoryDTO> findAll() {
    final List<ProductCategory> productCategories =
        productCategoryRepository.findAll(Sort.by("id"));
    return productCategories.stream()
        .map(
            productCategory ->
                productCategoryMapper.updateProductCategoryDTO(
                    productCategory, new ProductCategoryDTO()))
        .toList();
  }

  public ProductCategoryDTO get(final UUID id) {
    return productCategoryRepository
        .findById(id)
        .map(
            productCategory ->
                productCategoryMapper.updateProductCategoryDTO(
                    productCategory, new ProductCategoryDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public UUID create(final ProductCategoryDTO productCategoryDTO) {
    final ProductCategory productCategory = new ProductCategory();
    productCategoryMapper.updateProductCategory(productCategoryDTO, productCategory);
    return productCategoryRepository.save(productCategory).getId();
  }

  public void update(final UUID id, final ProductCategoryDTO productCategoryDTO) {
    final ProductCategory productCategory =
        productCategoryRepository.findById(id).orElseThrow(NotFoundException::new);
    productCategoryMapper.updateProductCategory(productCategoryDTO, productCategory);
    productCategoryRepository.save(productCategory);
  }

  public void delete(final UUID id) {
    final ProductCategory productCategory =
        productCategoryRepository.findById(id).orElseThrow(NotFoundException::new);
    publisher.publishEvent(new BeforeDeleteProductCategory(id));
    productCategoryRepository.delete(productCategory);
  }

  public boolean nameExists(final String name) {
    return productCategoryRepository.existsByNameIgnoreCase(name);
  }

  public Map<UUID, String> getProductCategoryValues() {
    return productCategoryRepository.findAll(Sort.by("id")).stream()
        .collect(CustomCollectors.toSortedMap(ProductCategory::getId, ProductCategory::getName));
  }
}
