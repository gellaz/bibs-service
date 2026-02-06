package it.bibs.product_stock;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import it.bibs.entity.AuditableEntity;

@Entity
@Table(name = "product_stocks")
@Getter
@Setter
public class ProductStock extends AuditableEntity {

  @Column(nullable = false)
  private Integer quantityAvailable;
}
