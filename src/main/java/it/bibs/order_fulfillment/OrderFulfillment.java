package it.bibs.order_fulfillment;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import it.bibs.entity.AuditableEntity;
import it.bibs.order.Order;

@Entity
@Table(name = "order_fulfillments")
@Getter
@Setter
public class OrderFulfillment extends AuditableEntity {

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderFulfillmentMethod method;

  @Column private String pickupCode;

  @Column private OffsetDateTime pickupDeadline;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;
}
