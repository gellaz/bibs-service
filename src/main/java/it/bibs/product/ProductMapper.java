package it.bibs.product;

import java.util.HashSet;
import java.util.List;

import org.mapstruct.AfterMapping;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

import it.bibs.product_category.ProductCategory;
import it.bibs.product_category.ProductCategoryRepository;
import it.bibs.product_stock.ProductStock;
import it.bibs.product_stock.ProductStockRepository;
import it.bibs.util.NotFoundException;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

  @Mapping(target = "productCategories", ignore = true)
  @Mapping(target = "productStock", ignore = true)
  ProductDTO updateProductDTO(Product product, @MappingTarget ProductDTO productDTO);

  @AfterMapping
  default void afterUpdateProductDTO(Product product, @MappingTarget ProductDTO productDTO) {
    productDTO.setProductCategories(
        product.getProductCategories().stream()
            .map(productCategory -> productCategory.getId())
            .toList());
    productDTO.setProductStock(
        product.getProductStock() == null ? null : product.getProductStock().getId());
  }

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "productCategories", ignore = true)
  @Mapping(target = "productStock", ignore = true)
  Product updateProduct(
      ProductDTO productDTO,
      @MappingTarget Product product,
      @Context ProductCategoryRepository productCategoryRepository,
      @Context ProductStockRepository productStockRepository);

  @AfterMapping
  default void afterUpdateProduct(
      ProductDTO productDTO,
      @MappingTarget Product product,
      @Context ProductCategoryRepository productCategoryRepository,
      @Context ProductStockRepository productStockRepository) {
    final List<ProductCategory> productCategories =
        productCategoryRepository.findAllById(
            productDTO.getProductCategories() == null
                ? List.of()
                : productDTO.getProductCategories());
    if (productCategories.size()
        != (productDTO.getProductCategories() == null
            ? 0
            : productDTO.getProductCategories().size())) {
      throw new NotFoundException("one of productCategories not found");
    }
    product.setProductCategories(new HashSet<>(productCategories));
    final ProductStock productStock =
        productDTO.getProductStock() == null
            ? null
            : productStockRepository
                .findById(productDTO.getProductStock())
                .orElseThrow(() -> new NotFoundException("productStock not found"));
    product.setProductStock(productStock);
  }
}
