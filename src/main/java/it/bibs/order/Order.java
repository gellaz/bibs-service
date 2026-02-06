package it.bibs.order;

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
import it.bibs.store.Store;
import it.bibs.user.User;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends AuditableEntity {

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderType orderType;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  @Column private OffsetDateTime expiresAt;

  @Column private String notes;

  @Column(nullable = false)
  private Integer subtotalCents;

  @Column(nullable = false)
  private Integer shippingCents;

  @Column(nullable = false)
  private Integer discountCents;

  @Column(nullable = false)
  private Integer totalCents;

  @Column(nullable = false, length = 3)
  private String currency;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private Store store;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
