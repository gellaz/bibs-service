package it.bibs.order_item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import it.bibs.entity.AuditableEntity;
import it.bibs.order.Order;
import it.bibs.product.Product;

@Entity
@Table(name = "order_items")
@Getter
@Setter
public class OrderItem extends AuditableEntity {

  @Column(nullable = false)
  private Integer quantity;

  @Column(nullable = false)
  private Integer unitPriceCents;

  @Column(nullable = false, length = 3)
  private String currency;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;
}
