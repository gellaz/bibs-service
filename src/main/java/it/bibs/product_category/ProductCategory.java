package it.bibs.product_category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import it.bibs.entity.AuditableEntity;

@Entity
@Table(name = "product_categories")
@Getter
@Setter
public class ProductCategory extends AuditableEntity {

  @Column(nullable = false, unique = true)
  private String name;
}
