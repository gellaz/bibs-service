package it.bibs.product;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import it.bibs.entity.AuditableEntity;
import it.bibs.product_category.ProductCategory;
import it.bibs.product_stock.ProductStock;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends AuditableEntity {

  @Column(nullable = false, columnDefinition = "text")
  private String title;

  @Column(columnDefinition = "text")
  private String description;

  @Column(nullable = false)
  private Boolean isActive;

  @ManyToMany
  @JoinTable(
      name = "ProductClassificationses",
      joinColumns = @JoinColumn(name = "productId"),
      inverseJoinColumns = @JoinColumn(name = "productCategoryId"))
  private Set<ProductCategory> productCategories = new HashSet<>();

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_stock_id", nullable = false, unique = true)
  private ProductStock productStock;
}
