package it.bibs.loyalty_point_transaction;

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
import it.bibs.user.User;

@Entity
@Table(name = "loyalty_point_transactions")
@Getter
@Setter
public class LoyaltyPointTransaction extends AuditableEntity {

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private LoyaltyPointTransactionType txType;

  @Column(nullable = false)
  private Integer points;

  @Column(columnDefinition = "text")
  private String reason;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;
}
