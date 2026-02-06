package it.bibs.product;

import java.util.List;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import it.bibs.events.BeforeDeleteProduct;
import it.bibs.events.BeforeDeleteProductCategory;
import it.bibs.product_category.ProductCategoryRepository;
import it.bibs.product_stock.ProductStockRepository;
import it.bibs.util.NotFoundException;

@Service
@Transactional(rollbackFor = Exception.class)
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductCategoryRepository productCategoryRepository;
  private final ProductStockRepository productStockRepository;
  private final ApplicationEventPublisher publisher;
  private final ProductMapper productMapper;

  public List<ProductDTO> findAll() {
    final List<Product> products = productRepository.findAll(Sort.by("id"));
    return products.stream()
        .map(product -> productMapper.updateProductDTO(product, new ProductDTO()))
        .toList();
  }

  public ProductDTO get(final UUID id) {
    return productRepository
        .findById(id)
        .map(product -> productMapper.updateProductDTO(product, new ProductDTO()))
        .orElseThrow(NotFoundException::new);
  }

  public UUID create(final ProductDTO productDTO) {
    final Product product = new Product();
    productMapper.updateProduct(
        productDTO, product, productCategoryRepository, productStockRepository);
    return productRepository.save(product).getId();
  }

  public void update(final UUID id, final ProductDTO productDTO) {
    final Product product = productRepository.findById(id).orElseThrow(NotFoundException::new);
    productMapper.updateProduct(
        productDTO, product, productCategoryRepository, productStockRepository);
    productRepository.save(product);
  }

  public void delete(final UUID id) {
    final Product product = productRepository.findById(id).orElseThrow(NotFoundException::new);
    publisher.publishEvent(new BeforeDeleteProduct(id));
    productRepository.delete(product);
  }

  public boolean productStockExists(final UUID id) {
    return productRepository.existsByProductStockId(id);
  }

  @EventListener(BeforeDeleteProductCategory.class)
  public void on(final BeforeDeleteProductCategory event) {
    // remove many-to-many relations at owning side
    productRepository
        .findAllByProductCategoriesId(event.getId())
        .forEach(
            product ->
                product
                    .getProductCategories()
                    .removeIf(productCategory -> productCategory.getId().equals(event.getId())));
  }
}
