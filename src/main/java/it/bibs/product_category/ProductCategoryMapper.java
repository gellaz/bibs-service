package it.bibs.product_category;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ReportingPolicy;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductCategoryMapper {

  ProductCategoryDTO updateProductCategoryDTO(
      ProductCategory productCategory, @MappingTarget ProductCategoryDTO productCategoryDTO);

  @Mapping(target = "id", ignore = true)
  ProductCategory updateProductCategory(
      ProductCategoryDTO productCategoryDTO, @MappingTarget ProductCategory productCategory);
}
